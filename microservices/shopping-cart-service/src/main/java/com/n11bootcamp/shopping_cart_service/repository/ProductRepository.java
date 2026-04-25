package com.n11bootcamp.shopping_cart_service.repository;
import java.util.Optional;


import com.n11bootcamp.shopping_cart_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);
}