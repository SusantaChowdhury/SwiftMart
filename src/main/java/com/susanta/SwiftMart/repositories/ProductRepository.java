package com.susanta.SwiftMart.repositories;

import com.susanta.SwiftMart.entities.Category;
import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findBySellerAndCategory(@NonNull User seller, @NonNull Category category, @NonNull Pageable pageable);

    Page<Product> findBySeller(@NonNull User seller, @NonNull Pageable pageable);

    long countBySeller(@NonNull User seller);

    @Query("SELECT SUM(p.stockQuantity) FROM Product p WHERE p.seller = :seller")
    Long calculateTotalStockBySeller(@Param("seller") @NonNull User seller);

    @Query("SELECT p FROM Product p JOIN FETCH p.seller")
    List<Product> findAllWithSellers();

    Optional<Product> findById(Long id);
}