#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/infra/docker-compose.microservices.yml"
ENV_FILE="${KML_ENV_FILE:-$ROOT_DIR/infra/.env}"
PROJECT_NAME="${COMPOSE_PROJECT_NAME:-kml-it}"
BASE_URL="${KML_GATEWAY_URL:-http://localhost:8080}"
REUSE_STACK="${KML_REUSE_STACK:-false}"
ADMIN_PASSWORD="${KML_ADMIN_PASSWORD:?KML_ADMIN_PASSWORD is required}"

if [[ ! -f "$ENV_FILE" ]]; then
  cp "$ROOT_DIR/infra/.env.example" "$ENV_FILE"
fi

compose() {
  COMPOSE_PROJECT_NAME="$PROJECT_NAME" docker compose \
    --env-file "$ENV_FILE" \
    -f "$COMPOSE_FILE" \
    --profile microservices \
    "$@"
}

json_get() {
  python3 -c 'import json,sys; data=json.load(sys.stdin); print(eval(sys.argv[1], {"len": len}, {"data": data}))' "$1"
}

curl_json() {
  local method="$1"
  local url="$2"
  local body="${3:-}"
  local auth_header="${4:-}"
  local response
  local status

  if [[ -n "$body" ]]; then
    if [[ -n "$auth_header" ]]; then
      response="$(curl -sS -w '\n%{http_code}' -X "$method" "$url" -H 'Content-Type: application/json' -H "$auth_header" -d "$body")"
    else
      response="$(curl -sS -w '\n%{http_code}' -X "$method" "$url" -H 'Content-Type: application/json' -d "$body")"
    fi
  else
    if [[ -n "$auth_header" ]]; then
      response="$(curl -sS -w '\n%{http_code}' -X "$method" "$url" -H "$auth_header")"
    else
      response="$(curl -sS -w '\n%{http_code}' -X "$method" "$url")"
    fi
  fi

  status="$(printf '%s' "$response" | tail -n 1)"
  response="$(printf '%s' "$response" | sed '$d')"

  if [[ "$status" -lt 200 || "$status" -ge 300 ]]; then
    echo "HTTP $status from $method $url" >&2
    echo "$response" >&2
    return 22
  fi

  printf '%s' "$response"
}

wait_for() {
  local name="$1"
  local url="$2"
  for attempt in {1..90}; do
    if curl -fsS "$url" >/dev/null; then
      echo "$name is ready"
      return 0
    fi
    echo "Waiting for $name at $url ($attempt/90)" >&2
    sleep 2
  done
  echo "Timed out waiting for $name at $url" >&2
  return 1
}

cleanup() {
  if [[ "$REUSE_STACK" != "true" ]]; then
    compose down -v --remove-orphans >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT

if [[ "$REUSE_STACK" != "true" ]]; then
  compose up --build -d api-gateway
fi

wait_for "user-service" "http://localhost:8081/actuator/health/readiness"
wait_for "inventory-service" "http://localhost:8082/actuator/health/readiness"
wait_for "order-service" "http://localhost:8083/actuator/health/readiness"
wait_for "shipment-service" "http://localhost:8084/actuator/health/readiness"
wait_for "warehouse-service" "http://localhost:8085/actuator/health/readiness"
wait_for "api-gateway" "$BASE_URL/actuator/health/readiness"

suffix="$(date +%s%N)"
sku="IT-SKU-$suffix"
shipping_address="12 Integration Street, 10115 Berlin, Germany"

user_response="$(
  curl_json POST "$BASE_URL/api/v1/users/register/customer" "{\"name\":\"Integration Customer\",\"username\":\"customer-$suffix\",\"password\":\"integration-password\",\"role\":\"CUSTOMER\",\"address\":\"$shipping_address\"}"
)"
user_id="$(printf '%s' "$user_response" | json_get "data['id']")"

admin_login_response="$(
  curl_json POST "$BASE_URL/api/v1/auth/login" "{\"username\":\"admin\",\"password\":\"$ADMIN_PASSWORD\"}"
)"
admin_access_token="$(printf '%s' "$admin_login_response" | json_get "data['accessToken']")"
admin_auth_header="Authorization: Bearer $admin_access_token"



customer_login_response="$(
  curl_json POST "$BASE_URL/api/v1/auth/login" "{\"username\":\"customer-$suffix\",\"password\":\"integration-password\"}"
)"
customer_access_token="$(printf '%s' "$customer_login_response" | json_get "data['accessToken']")"
customer_auth_header="Authorization: Bearer $customer_access_token"

warehouse_response="$(
  curl_json POST "$BASE_URL/api/v1/warehouses" "{\"ownerUserId\":1,\"name\":\"Integration Warehouse $suffix\",\"address\":\"Integration Dock\",\"storageUnits\":[{\"code\":\"A-$suffix\",\"capacity\":50}]}" "$admin_auth_header"
)"
warehouse_id="$(printf '%s' "$warehouse_response" | json_get "data['data']['id']")"

storage_units_response="$(curl_json GET "$BASE_URL/api/v1/warehouses/$warehouse_id/storage-units" "" "$admin_auth_header")"
storage_unit_id="$(printf '%s' "$storage_units_response" | json_get "data['data'][0]['id']")"

inventory_response="$(
  curl_json POST "$BASE_URL/api/v1/inventories" "{\"ownerUserId\":1,\"sku\":\"$sku\",\"name\":\"Integration Part\",\"quantity\":10,\"warehouseId\":$warehouse_id,\"storageUnitId\":$storage_unit_id,\"reorderThreshold\":2,\"safetyStockLevel\":1}" "$admin_auth_header"
)"
inventory_id="$(printf '%s' "$inventory_response" | json_get "data['data']['id']")"

order_response="$(
  curl_json POST "$BASE_URL/api/v1/orders" "{\"code\":\"IT-ORDER-$suffix\",\"userId\":$user_id,\"items\":[{\"sku\":\"$sku\",\"quantity\":2,\"priceAtOrder\":12.50}]}" "$customer_auth_header"
)"
order_id="$(printf '%s' "$order_response" | json_get "data['data']['id']")"

for _ in {1..60}; do
  shipments_response="$(curl_json GET "$BASE_URL/api/v1/shipments?orderId=$order_id" "" "$customer_auth_header")"
  shipment_count="$(printf '%s' "$shipments_response" | json_get "len(data['data'])")"
  if [[ "$shipment_count" != "0" ]]; then
    tracking_code="$(printf '%s' "$shipments_response" | json_get "data['data'][0]['trackingCode']")"
    shipment_address="$(printf '%s' "$shipments_response" | json_get "data['data'][0]['address']")"
    if [[ "$shipment_address" != "$shipping_address" ]]; then
      echo "Expected shipment address '$shipping_address', got '$shipment_address'" >&2
      exit 1
    fi
    inventory_after="$(curl_json GET "$BASE_URL/api/v1/inventories/$inventory_id" "" "$admin_auth_header")"
    remaining_quantity="$(printf '%s' "$inventory_after" | json_get "data['data']['quantity']")"
    if [[ "$remaining_quantity" != "8" ]]; then
      echo "Expected inventory quantity 8 after reservation, got $remaining_quantity" >&2
      exit 1
    fi
    echo "Async order fulfillment reserved inventory and created shipment $tracking_code for order $order_id"
    exit 0
  fi
  sleep 2
done

echo "Shipment was not created for order $order_id" >&2
exit 1
