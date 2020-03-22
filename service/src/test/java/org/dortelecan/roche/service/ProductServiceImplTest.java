package org.dortelecan.roche.service;

import org.aspectj.lang.annotation.Before;
import org.dortelecan.roche.IntegrationConfiguration;
import org.dortelecan.roche.model.Product;
import org.dortelecan.roche.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;


@SpringBootTest(classes = IntegrationConfiguration.class)
public class ProductServiceImplTest {
    @Autowired
    private ProductService toTest;
    @Autowired
    private ProductRepository productRepository;

    private Product getValidProduct() {
        Product toReturn = new Product();
        toReturn.setName("name");
        toReturn.setPriceInPence(1L);
        return toReturn;
    }

    @AfterEach
    public void before() {
        productRepository.deleteAll();
    }

    @Test
    public void canCreateProduct() {
        toTest.createProduct(getValidProduct());
    }

    @Test
    void cannotCreateProductWithId() {
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate.setSku(1L);
            toTest.createProduct(toCreate);
        });
        Assertions.assertEquals("Should not have a key", thrown.getMessage());
    }

    @Test
    void cannotCreateProductWithCreatedDate() {
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate.setCreationDateTime(LocalDateTime.now());
            toTest.createProduct(toCreate);
        });
        Assertions.assertEquals("Should not have a creation date", thrown.getMessage());
    }

    @Test
    void cannotCreateProductWithNoName() {
        Exception thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate.setName(null);
            toTest.createProduct(toCreate);
        });
        Assertions.assertEquals("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement", thrown.getMessage());
    }

    @Test
    void cannotCreateProductWithDuplicateName() {
        Exception thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Product toCreate = getValidProduct();
            toTest.createProduct(toCreate);
            toCreate = getValidProduct();
            toTest.createProduct(toCreate);
        });
        Assertions.assertEquals("could not execute statement; SQL [n/a]; constraint [PRODUCT_NAME_UNIQUE]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement",
                thrown.getMessage());
    }

    @Test
    void cannotCreateProductWithNullPrice() {
        Exception thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate.setPriceInPence(null);
            toTest.createProduct(toCreate);
        });
        Assertions.assertEquals("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement", thrown.getMessage());
    }

    @Test
    void cannotCreateProductWithNegativePrice() {
        Exception thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate.setPriceInPence(-1L);
            toTest.createProduct(toCreate);
        });
        Assertions.assertEquals("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement", thrown.getMessage());
    }

    /**
     * Probably this could/should be split
     */
    @Test
    public void getProductsPaginationWorksFine() {
        Product first = toTest.createProduct(getValidProduct());

        Product second = getValidProduct();
        second.setName("second");
        second = toTest.createProduct(second);

        Product third = getValidProduct();
        third.setName("third");
        third.setDeleted(true);
        third = toTest.createProduct(third);

        Pageable request = PageRequest.of(0, 1);
        Page<Product> actual = toTest.getProducts(false, request);
        Page<Product> expected = new PageImpl<Product>(Collections.singletonList(first), request, 2);
        Assertions.assertEquals(expected, actual);

        request = PageRequest.of(1, 1);
        actual = toTest.getProducts(false, request);
        expected = new PageImpl<Product>(Collections.singletonList(second), request, 2);
        Assertions.assertEquals(expected, actual);

        request = PageRequest.of(2, 1);
        actual = toTest.getProducts(false, request);
        expected = new PageImpl<Product>(Collections.emptyList(), request, 2);
        Assertions.assertEquals(expected, actual);

        request = PageRequest.of(2, 1);
        actual = toTest.getProducts(true, request);
        expected = new PageImpl<Product>(Collections.singletonList(third), request, 2);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void cannotUpdateProductWithNoId() {
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate.setCreationDateTime(LocalDateTime.now());
            toTest.updateProduct(toCreate);
        });
        Assertions.assertEquals("Should have a key", thrown.getMessage());
    }

    @Test
    void cannotUpdateProductWithNoCreatedDate() {
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate.setSku(1L);
            toTest.updateProduct(toCreate);
        });
        Assertions.assertEquals("Should have a creation date", thrown.getMessage());
    }

    @Test
    void cannotUpdateProductToNullName() {
        Exception thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate = toTest.createProduct(toCreate);

            toCreate.setName(null);
            toTest.updateProduct(toCreate);
        });
        Assertions.assertEquals("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement", thrown.getMessage());
    }

    @Test
    void cannotUpdateProductToBreakUniqueConstraintOnName() {
        Exception thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Product toCreate = getValidProduct();
            toTest.createProduct(toCreate);
            Product toUpdate = getValidProduct();
            toUpdate.setName(toCreate.getName() + "23");
            toTest.createProduct(toUpdate);

            toUpdate.setName(toCreate.getName());
            toTest.updateProduct(toUpdate);
        });
        Assertions.assertEquals("could not execute statement; SQL [n/a]; constraint [PRODUCT_NAME_UNIQUE]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement", thrown.getMessage());
    }

    @Test
    void cannotUpdateProductToNullPrice() {
        Exception thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate = toTest.createProduct(toCreate);

            toCreate.setPriceInPence(null);
            toTest.updateProduct(toCreate);
        });
        Assertions.assertEquals("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement", thrown.getMessage());
    }

    @Test
    void cannotUpdateProductToNegativePrice() {
        Exception thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate = toTest.createProduct(toCreate);

            toCreate.setPriceInPence(-1l);
            toTest.updateProduct(toCreate);
        });
        Assertions.assertEquals("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement", thrown.getMessage());
    }

    @Test
    void canUpdateProduct() {
        Product toCreate = getValidProduct();
        toCreate = toTest.createProduct(toCreate);
        toCreate.setName(toCreate.getName() + "2");
        toCreate = toTest.updateProduct(toCreate);

        Product retrieved = productRepository.findById(toCreate.getSku()).get();

        Assertions.assertFalse(retrieved == toCreate);
        Assertions.assertEquals(retrieved.getCreationDateTime().toEpochSecond(ZoneOffset.UTC), toCreate.getCreationDateTime().toEpochSecond(ZoneOffset.UTC));
        retrieved.setCreationDateTime(null);
        toCreate.setCreationDateTime(null);
        Assertions.assertEquals(retrieved.toString(), toCreate.toString());
    }

    @Test
    void canDeleteProduct() {
        Product toCreate = getValidProduct();
        toCreate = toTest.createProduct(toCreate);

        Product retrieved = productRepository.findById(toCreate.getSku()).get();
        Assertions.assertNotNull(retrieved);
        Assertions.assertFalse(retrieved.isDeleted());

        toTest.deleteProduct(toCreate.getSku());
        retrieved = productRepository.findById(toCreate.getSku()).get();
        Assertions.assertNotNull(retrieved);
        Assertions.assertTrue(retrieved.isDeleted());
    }

    @Test
    public void cannotDeleteNullSku() {
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            toTest.deleteProduct(null);
        });
        Assertions.assertEquals("Should have received a key", thrown.getMessage());
    }

    @Test
    public void cannotDeleteMissingProduct() {
        Exception thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            toTest.deleteProduct(1L);
        });
        Assertions.assertEquals("Unable to find org.dortelecan.roche.model.Product with id 1", thrown.getMessage());
    }

    @Test
    public void cannotDeleteAlreadyDeletedProduct() {
        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Product toCreate = getValidProduct();
            toCreate = toTest.createProduct(toCreate);

            toTest.deleteProduct(toCreate.getSku());
            toTest.deleteProduct(toCreate.getSku());
        });
        Assertions.assertEquals("Should not already be deleted", thrown.getMessage());
    }
}