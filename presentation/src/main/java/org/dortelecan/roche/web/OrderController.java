package org.dortelecan.roche.web;

import org.dortelecan.roche.model.Order;
import org.dortelecan.roche.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;

    // TODO validate email
    @PutMapping("/order")
    public Order addOrder(@RequestParam String email, @RequestParam Set<Long> productIds) {
        log.info("Adding a new order for email: {} , with products: {}", email, productIds);
        return orderService.createOrder(email, productIds);
    }

    @GetMapping("/order")
    public List<Order> getOrders(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                         LocalDateTime since) {
        log.info("Getting all orders since {}", since);
        return orderService.getOrders(since);

    }
}
