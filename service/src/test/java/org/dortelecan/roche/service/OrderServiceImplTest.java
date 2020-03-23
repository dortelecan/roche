package org.dortelecan.roche.service;

import org.dortelecan.roche.IntegrationConfiguration;
import org.dortelecan.roche.model.Order;
import org.dortelecan.roche.model.Product;
import org.dortelecan.roche.repository.OrderRepository;
import org.dortelecan.roche.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;

@SpringBootTest(classes = IntegrationConfiguration.class)
public class OrderServiceImplTest {
    @Autowired
    private OrderService toTest;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void before() {
        product = new Product();
        product.setName("p1");
        product.setPriceInPence(1L);
        productService.createProduct(product);
    }

    @AfterEach
    public void after() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    public void canCreateOrder() {
        Order created = toTest.createOrder("xxx@xxx.com", Collections.singleton(product.getSku()));
        Assertions.assertTrue(orderRepository.findById(created.getOrderId()).isPresent());
        Assertions.assertEquals(1l, created.getTotalPrice());
    }

    @Test
    public void cannotCreateOrderWithoutEmail() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            toTest.createOrder(null, Collections.singleton(product.getSku()));
        });
        Assertions.assertEquals("Should have an email", e.getMessage());
    }

    @Test
    public void cannotCreateOrderWithoutProducts() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            toTest.createOrder("null", null);
        });
        Assertions.assertEquals("Should have products", e.getMessage());
        e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            toTest.createOrder("null", new HashSet<>());
        });
        Assertions.assertEquals("Should have products", e.getMessage());
    }

    @Test
    public void cannotCreateOrderWithUnknownProduct() {
        long unknownSku = -1;
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            toTest.createOrder("null", Collections.singleton(unknownSku));
        });
        Assertions.assertEquals("Could not find product(s) with id:[" + unknownSku + "]", e.getMessage());
    }

    @Test
    public void cannotCreateOrderWithDeletedProduct() {
        Product deletedProduct = new Product();
        deletedProduct.setName("p2");
        deletedProduct.setPriceInPence(1L);
        deletedProduct.setDeleted(true);
        productRepository.save(deletedProduct);

        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            toTest.createOrder("null", Collections.singleton(deletedProduct.getSku()));
        });
        Assertions.assertEquals("Could not find product(s) with id:[" + deletedProduct.getSku() + "]", e.getMessage());
    }

    @Test
    public void whenCreatingOrderTotalPriceIsCalculatedCorrectly() {
        Product secondProduct = new Product();
        secondProduct.setName("p2");
        secondProduct.setPriceInPence(991L);
        productRepository.save(secondProduct);

        HashSet products = new HashSet();
        products.add(product.getSku());
        products.add(secondProduct.getSku());
        Order created = toTest.createOrder("", products);

        Assertions.assertEquals(secondProduct.getPriceInPence() + product.getPriceInPence(), created.getTotalPrice());
    }
}