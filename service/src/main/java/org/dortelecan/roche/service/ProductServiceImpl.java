package org.dortelecan.roche.service;

import org.dortelecan.roche.model.Product;
import org.dortelecan.roche.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product createProduct(Product toCreate) {
        log.info("Creating product: {}", toCreate);
        Assert.isNull(toCreate.getSku(), "Should not have a key");
        Assert.isNull(toCreate.getCreationDateTime(), "Should not have a creation date");
        return productRepository.save(toCreate);
    }

    @Override
    public Page<Product> getProducts(boolean includeDeleted, Pageable pageable) {
        log.info("Getting products, including deleted: {}", includeDeleted);
        if (includeDeleted) {
            return productRepository.findAll(pageable);
        } else {
            return productRepository.findByDeleted(false, pageable);
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Product toUpdate) {
        log.info("Updating product: {}", toUpdate);
        Assert.notNull(toUpdate.getSku(), "Should have a key");
        Assert.notNull(toUpdate.getCreationDateTime(), "Should have a creation date");

        Optional<Product> alreadyPresent = productRepository.findById(toUpdate.getSku());
        Assert.isTrue(alreadyPresent.isPresent(), "Should already have been present");
        toUpdate.setCreationDateTime(alreadyPresent.get().getCreationDateTime());
        return productRepository.save(toUpdate);
    }

    @Override
    @Transactional
    public Product deleteProduct(Long sku) {
        log.info("Marking product: {} as deleted", sku);
        Assert.notNull(sku, "Should have received a key");
        Product toDelete = productRepository.getOne(sku);
        Assert.notNull(toDelete, "Should already have been present");
        Assert.isTrue(!toDelete.isDeleted(), "Should not already be deleted");
        toDelete.setDeleted(true);
        return productRepository.save(toDelete);
    }
}
