package com.askwinston.service.impl;

import com.askwinston.model.Product;
import com.askwinston.model.ProductQuantity;
import com.askwinston.repository.ProductQuantityRepository;
import com.askwinston.repository.ProductRepository;
import com.askwinston.service.ProductService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    private ProductQuantityRepository productQuantityRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductQuantityRepository productQuantityRepository) {
        this.productRepository = productRepository;
        this.productQuantityRepository = productQuantityRepository;
    }

    /**
     * @param id
     * @return Product
     * To get product with id
     */
    @Override
    public Product getById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        );
    }

    /**
     * @param product
     * @return Product
     * To create new product with product details
     */
    @Override
    public Product create(Product product) {
        final Product newProduct = productRepository.save(Product.builder()
                .name(product.getName())
                .description(product.getDescription())
                .category(Product.Category.PILLS)
                .safetyInfo(product.getSafetyInfo())
                .build());
        log.info("New Product created with id {}", newProduct.getId());
        List<ProductQuantity> quantities = product.getQuantities();
        quantities = quantities.stream().map(quantity -> productQuantityRepository.save(ProductQuantity.builder()
                .quantity(quantity.getQuantity())
                .product(newProduct)
                .ordinal(quantity.getOrdinal())
                .price(quantity.getPrice())
                .build())).collect(Collectors.toList());

        newProduct.setQuantities(quantities);
        log.info("Quantity information for product added for product with id {}", newProduct.getId());

        return newProduct;
    }

    /**
     * @return List<Product>
     * To get all the products
     */
    @Override
    public List<Product> getAll() {
        return Lists.newArrayList(productRepository.findAll());
    }
}
