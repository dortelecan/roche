package org.dortelecan.roche.repository;

import org.dortelecan.roche.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCreationDateTimeAfter(LocalDateTime since);
}
