package com.susanta.SwiftMart.services.impl;

import com.susanta.SwiftMart.entities.Category;
import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;
import com.susanta.SwiftMart.repositories.ProductRepository;
import com.susanta.SwiftMart.services.ProductService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    // old method to get all products to show in tabular format in admin panel
    // public List<Product> getProductsBySeller(User seller) {
    // return productRepository.findBySeller(seller);
    // }

    // new method to get all products in card format with pagination and sorting
    public Page<Product> getProductsBySeller(User seller, String sort, String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort != null ? sort : "name"));
        if (filter != null && !filter.isEmpty()) {
            return productRepository.findBySellerAndCategory(seller, Category.valueOf(filter), pageable);
        }
        return productRepository.findBySeller(seller, pageable);
    }

    public Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    
    public void updateProductStock(Product product, int additionalStock) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Add the entered stock quantity to the current stock quantity
        int updatedStockQuantity = existingProduct.getStockQuantity() + additionalStock;
        existingProduct.setStockQuantity(updatedStockQuantity);

        // Update other product details
        existingProduct.setName(product.getName());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setImageUrl(product.getImageUrl());

        productRepository.save(existingProduct);
    }

    @Override
    public long countTotalProductTypesBySeller(User seller) {
        return productRepository.countBySeller(seller);
    }

    @Override
    public long calculateTotalStockBySeller(User seller) {
        Long totalStock = productRepository.calculateTotalStockBySeller(seller);
        return totalStock != null ? totalStock : 0; // Handle null case
    }

    @Override
    public List<Product> getAllProductsWithSellers() {
        return productRepository.findAllWithSellers();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}