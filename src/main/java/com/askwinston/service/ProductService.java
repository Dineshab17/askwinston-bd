package com.askwinston.service;

import com.askwinston.model.Product;

import java.util.List;

public interface ProductService {
    Product getById(Long id);

    Product create(Product product);

    List<Product> getAll();
}
