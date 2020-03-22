package org.dortelecan.roche.web;

import org.dortelecan.roche.model.Product;
import org.dortelecan.roche.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    public Product createProduct(@RequestBody Product toCreate) {
        log.info("Creating product: {}", toCreate);
        return productService.createProduct(toCreate);
    }

    @GetMapping("/product")
    public Page<Product> getProducts(@RequestParam(required = false) boolean includeDeleted, @RequestParam int pageSize, @RequestParam int page) {
        log.info("Listing all products, deleted products included: {}", includeDeleted);
        return productService.getProducts(includeDeleted, PageRequest.of(page, pageSize));
    }

    @PutMapping("/product")
    public Product updateProduct(@RequestBody Product toUpdate) {
        log.info("updating product: {}", toUpdate);
        return productService.updateProduct(toUpdate);
    }

    @DeleteMapping("/product/{sku}")
    public Product deleteProduct(@PathVariable("sku") Long sku) {
        log.info("deleting product: {}", sku);
        return productService.deleteProduct(sku);
    }
}
