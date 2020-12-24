package com.askwinston.web.controller;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.Product;
import com.askwinston.service.ProductService;
import com.askwinston.web.dto.ProductDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private ParsingHelper parsingHelper;

    public ProductController(ProductService productService, ParsingHelper parsingHelper) {
        this.productService = productService;
        this.parsingHelper = parsingHelper;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ProductDto create(@Validated(ProductDto.CreateProductValidation.class) @RequestBody ProductDto productDto) {
        Product newProduct = productService.create(parsingHelper.mapObject(productDto, Product.class));
        return parsingHelper.mapObject(newProduct, ProductDto.class);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable("id") Long id) {
        Product product = productService.getById(id);
        return parsingHelper.mapObject(product, ProductDto.class);
    }

    @GetMapping
    public List<ProductDto> getAll() {
        List<Product> products = productService.getAll();
        return parsingHelper.mapObjects(products, ProductDto.class);
    }
}
