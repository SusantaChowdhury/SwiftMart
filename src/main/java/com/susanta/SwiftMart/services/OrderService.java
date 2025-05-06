package com.susanta.SwiftMart.services;

import com.susanta.SwiftMart.entities.User;

public interface OrderService {
    public long countOrdersBySeller(User seller);

    public double calculateTotalSalesBySeller(User seller);

    public long countUniqueCustomersBySeller(User seller);
}
