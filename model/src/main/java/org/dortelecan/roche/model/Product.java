package org.dortelecan.roche.model;

import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "product_name_unique", columnNames = {"name"}))
@Check(constraints = "price_in_pence >= 0")
public class Product {
    @Id
    @GeneratedValue
    private Long sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long priceInPence;

    @Column(nullable = false)
    private LocalDateTime creationDateTime;

    @Column
    private boolean deleted;

    public Long getSku() {
        return sku;
    }

    public void setSku(Long sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPriceInPence() {
        return priceInPence;
    }

    public void setPriceInPence(Long priceInPence) {
        this.priceInPence = priceInPence;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return "Product{" +
                "sku=" + sku +
                ", name='" + name + '\'' +
                ", priceInPence=" + priceInPence +
                ", creationDateTime=" + creationDateTime +
                ", deleted=" + deleted +
                '}';
    }

    @PrePersist
    void onCreate() {
        this.setCreationDateTime(LocalDateTime.now());
    }
}
