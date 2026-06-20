set -a
source .env
set +a

docker exec -it 0a8de59730ac psql -U kml_admin -d kml_db

running dbs

cd infra

docker compose up -d

docker compose ps


# This is read-only. It shows the route annotations Spring should have registered for user, authentication, internal-user, avatar, and service-info controllers; we will use that output to choose a safe endpoint for the next verification.

- grep -RInE '@(RequestMapping|GetMapping|PostMapping|PutMapping|PatchMapping|DeleteMapping)' \
  services/user-service/src/main/java