package com.susanta.SwiftMart.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;

public interface ProductService {
    long countTotalProductTypesBySeller(User seller);

    long calculateTotalStockBySeller(User seller);

    public List<Product> getAllProductsWithSellers();

    public Product getProductById(Long id);

    public Page<Product> getAllProducts(Pageable pageable);
}
