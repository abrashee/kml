package com.kml.services.inventory.product;

import com.kml.services.inventory.entity.Product;
import com.kml.services.inventory.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        return productRepository
            .findByActiveTrueAndSearchableTrue(pageable)
            .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id) {
        return productRepository.findById(id)
            .filter(Product::isActive)
            .filter(Product::isSearchable)
            .map(this::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    private ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getAvailableQuantity(),
            product.getPrimaryWarehouseId());
    }
}