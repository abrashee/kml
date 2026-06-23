package com.kml.services.inventory.search;

import com.kml.services.inventory.product.ProductResponseDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ProductSearchService {

    private final RestClient restClient;
    private final OpenSearchProperties properties;

    public ProductSearchService(RestClient.Builder restClientBuilder, OpenSearchProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(properties.url()).build();
    }

    @SuppressWarnings("unchecked")
    public SearchPage<ProductResponseDto> search(String query, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int from = safePage * safeSize;

        Map<String, Object> body = Map.of(
            "from", from,
            "size", safeSize,
            "query", Map.of(
                "bool", Map.of(
                    "filter", List.of(
                        Map.of("term", Map.of("active", true)),
                        Map.of("term", Map.of("searchable", true))
                    ),
                    "must", List.of(
                        isBlank(query)
                            ? Map.of("match_all", Map.of())
                            : Map.of("multi_match", Map.of(
                                "query", query,
                                "fields", List.of("name^3", "sku^4", "description"),
                                "fuzziness", "AUTO"
                            ))
                    )
                )
            )
        );

        Map<String, Object> response = restClient.post()
            .uri("/{index}/_search", properties.productsIndex())
            .body(body)
            .retrieve()
            .body(Map.class);

        Map<String, Object> hits = (Map<String, Object>) response.get("hits");
        Map<String, Object> total = (Map<String, Object>) hits.get("total");
        int totalElements = ((Number) total.get("value")).intValue();

        List<Map<String, Object>> rawHits = (List<Map<String, Object>>) hits.get("hits");
        List<ProductResponseDto> items = rawHits.stream()
            .map(hit -> (Map<String, Object>) hit.get("_source"))
            .map(this::toDto)
            .toList();

        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / safeSize);

        return new SearchPage<>(items, safePage, safeSize, totalPages, totalElements);
    }

    private ProductResponseDto toDto(Map<String, Object> source) {
        return new ProductResponseDto(
            asLong(source.get("id")),
            (String) source.get("sku"),
            (String) source.get("name"),
            (String) source.get("description"),
            asBigDecimal(source.get("price")),
            ((Number) source.getOrDefault("availableQuantity", 0)).intValue(),
            asLong(source.get("primaryWarehouseId")));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Long asLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private BigDecimal asBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal decimal) return decimal;
        return new BigDecimal(value.toString());
    }

    public record SearchPage<T>(
        List<T> content,
        int number,
        int size,
        int totalPages,
        int totalElements) {
    }
}
