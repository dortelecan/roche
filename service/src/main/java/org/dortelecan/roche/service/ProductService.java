package org.dortelecan.roche.service;

import org.dortelecan.roche.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Product createProduct(Product toCreate);

    Page<Product> getProducts(boolean includeDeleted, Pageable pageable);

    Product updateProduct(Product toUpdate);

    Product deleteProduct(Long sku);
}
