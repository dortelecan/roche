package org.dortelecan.roche.service;

import org.dortelecan.roche.model.Order;
import org.dortelecan.roche.model.Product;
import org.dortelecan.roche.repository.OrderRepository;
import org.dortelecan.roche.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public Order createOrder(String email, Set<Long> productIds) {
        log.info("Creating order for email:{} , productIds: {}", email, productIds);
        Assert.notNull(email, "Should have an email");
        Assert.notNull(productIds, "Should have products");
        Assert.isTrue(productIds.size() != 0, "Should have products");

        List<Product> products = productRepository.findByDeletedAndSkuIn(false, productIds);

        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("Could not find product(s) with id:" +
                    productIds.stream().filter(prodId -> products.stream().noneMatch(x -> x.getSku() == prodId)).collect(Collectors.toList()));
        }

        Order toCreate = new Order();
        toCreate.setProducts(products);
        toCreate.setEmail(email);
        toCreate.setTotalPrice(products.stream().mapToLong(x -> x.getPriceInPence()).sum());

        return orderRepository.save(toCreate);
    }

    @Override
    public List<Order> getOrders(LocalDateTime since) {
        return orderRepository.findByCreationDateTimeAfter(since);
    }
}
