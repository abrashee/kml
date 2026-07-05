package com.kml.services.inventory.search;

import com.kml.services.inventory.dto.InventoryItemResponseDto;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class InventorySearchService {

    private final RestClient restClient;
    private final OpenSearchProperties properties;

    public InventorySearchService(RestClient.Builder restClientBuilder, OpenSearchProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(properties.url()).build();
    }

    @SuppressWarnings("unchecked")
    public Page<InventoryItemResponseDto> search(String query, Long warehouseId, Pageable pageable) {
        int safePage = Math.max(pageable.getPageNumber(), 0);
        int safeSize = Math.min(Math.max(pageable.getPageSize(), 1), 100);
        int from = safePage * safeSize;

        List<Map<String, Object>> filters = warehouseId == null
            ? List.of()
            : List.of(Map.of("term", Map.of("warehouseId", warehouseId)));

        Map<String, Object> body = Map.of(
            "from", from,
            "size", safeSize,
            "query", Map.of(
                "bool", Map.of(
                    "filter", filters,
                    "must", List.of(
                        isBlank(query)
                            ? Map.of("match_all", Map.of())
                            : Map.of("bool", Map.of(
                                "should", List.of(
                                    Map.of("multi_match", Map.of(
                                        "query", query,
                                        "fields", List.of("sku^4", "name^3"),
                                        "fuzziness", "AUTO"
                                    )),
                                    Map.of("match_phrase_prefix", Map.of(
                                        "name", Map.of("query", query, "boost", 3)
                                    )),
                                    Map.of("prefix", Map.of(
                                        "sku.keyword", Map.of("value", query, "boost", 4)
                                    ))
                                ),
                                "minimum_should_match", 1
                            ))
                    )
                )
            )
        );

        Map<String, Object> response = restClient.post()
            .uri("/{index}/_search", properties.inventoryIndex())
            .body(body)
            .retrieve()
            .body(Map.class);

        Map<String, Object> hits = (Map<String, Object>) response.get("hits");
        Map<String, Object> total = (Map<String, Object>) hits.get("total");
        long totalElements = ((Number) total.get("value")).longValue();

        List<Map<String, Object>> rawHits = (List<Map<String, Object>>) hits.get("hits");
        List<InventoryItemResponseDto> items = rawHits.stream()
            .map(hit -> (Map<String, Object>) hit.get("_source"))
            .map(this::toDto)
            .toList();

        return new PageImpl<>(items, pageable, totalElements);
    }

    private InventoryItemResponseDto toDto(Map<String, Object> source) {
        return new InventoryItemResponseDto(
            asLong(source.get("id")),
            null,
            (String) source.get("sku"),
            (String) source.get("name"),
            ((Number) source.getOrDefault("quantity", 0)).intValue(),
            asLong(source.get("warehouseId")),
            asLong(source.get("storageUnitId")),
            0,
            0,
            null,
            null,
            null);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Long asLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }
}
