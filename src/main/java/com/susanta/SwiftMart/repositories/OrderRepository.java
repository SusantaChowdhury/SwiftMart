package com.susanta.SwiftMart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.susanta.SwiftMart.entities.Order;
import com.susanta.SwiftMart.entities.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COUNT(o) FROM Order o WHERE o.seller = :seller")
    long countBySeller(@Param("seller") User seller);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.seller = :seller")
    Double calculateTotalSalesBySeller(@Param("seller") User seller);

    @Query("SELECT COUNT(DISTINCT o.customer) FROM Order o WHERE o.seller = :seller")
    long countDistinctCustomersBySeller(@Param("seller") User seller);
}
