package com.susanta.SwiftMart.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.susanta.SwiftMart.entities.User;
import com.susanta.SwiftMart.repositories.OrderRepository;
import com.susanta.SwiftMart.services.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public long countOrdersBySeller(User seller) {
        return orderRepository.countBySeller(seller);
    }

    public double calculateTotalSalesBySeller(User seller) {
        Double totalSales = orderRepository.calculateTotalSalesBySeller(seller);
        return totalSales != null ? totalSales : 0.0; // Handle null case
    }

    public long countUniqueCustomersBySeller(User seller) {
        return orderRepository.countDistinctCustomersBySeller(seller);
    }

}
