package org.dortelecan.roche.repository;

import org.dortelecan.roche.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByDeleted(boolean deleted, Pageable pageable);
    List<Product> findByDeletedAndSkuIn(boolean deleted, Set<Long> sku);
}
