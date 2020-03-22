package org.dortelecan.roche.service;

import org.dortelecan.roche.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface OrderService {
    Order createOrder(String email, Set<Long> productIds);

    List<Order> getOrders(LocalDateTime since);
}
