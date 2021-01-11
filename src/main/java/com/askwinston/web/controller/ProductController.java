package com.askwinston.web.controller;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.Product;
import com.askwinston.service.ProductService;
import com.askwinston.web.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {
    private final ProductService productService;
    private ParsingHelper parsingHelper;

    public ProductController(ProductService productService, ParsingHelper parsingHelper) {
        this.productService = productService;
        this.parsingHelper = parsingHelper;
    }

    /**
     * @param productDto
     * @return ProductDto
     * To create new product by admin
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ProductDto create(@Validated(ProductDto.CreateProductValidation.class) @RequestBody ProductDto productDto) {
        Product newProduct = productService.create(parsingHelper.mapObject(productDto, Product.class));
        return parsingHelper.mapObject(newProduct, ProductDto.class);
    }

    /**
     * @param id
     * @return ProductDto
     * To get product with product id
     */
    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable("id") Long id) {
        Product product = productService.getById(id);
        log.info("Product with id {} found", id);
        return parsingHelper.mapObject(product, ProductDto.class);
    }

    /**
     * @return List<ProductDto>
     * To get all the products
     */
    @GetMapping
    public List<ProductDto> getAll() {
        List<Product> products = productService.getAll();
        return parsingHelper.mapObjects(products, ProductDto.class);
    }
}
