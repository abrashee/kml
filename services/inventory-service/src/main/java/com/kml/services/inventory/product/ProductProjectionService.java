package com.kml.services.inventory.product;

import com.kml.services.inventory.entity.InventoryItem;
import com.kml.services.inventory.entity.Product;
import com.kml.services.inventory.repository.InventoryRepository;
import com.kml.services.inventory.repository.ProductRepository;
import com.kml.services.inventory.search.ProductSearchIndexer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductProjectionService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final ProductSearchIndexer productSearchIndexer;

    public ProductProjectionService(
        InventoryRepository inventoryRepository,
        ProductRepository productRepository,
        ProductSearchIndexer productSearchIndexer) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.productSearchIndexer = productSearchIndexer;
    }

    @Transactional
    public void rebuildSku(String sku) {

        InventoryItem representative =
            inventoryRepository.findFirstBySkuOrderByQuantityDesc(sku)
                .orElse(null);

        if (representative == null) {
            return;
        }

        int availableQuantity =
            inventoryRepository.sumQuantityBySku(sku);

        Product product =
            productRepository.findBySku(sku)
                .orElseGet(() ->
                    new Product(
                        representative.getSku(),
                        representative.getName()));

        product.updateProjection(
            representative.getName(),
            availableQuantity,
            representative.getWarehouseId());

        Product saved = productRepository.save(product);
        productSearchIndexer.index(saved);
    }
}
