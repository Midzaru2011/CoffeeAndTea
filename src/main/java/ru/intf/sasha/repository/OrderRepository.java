package ru.intf.sasha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.intf.sasha.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
//