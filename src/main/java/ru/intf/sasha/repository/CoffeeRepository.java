package ru.intf.sasha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.intf.sasha.model.Coffee;

import java.util.Optional;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    Optional<Coffee> findByName(String name);
}
