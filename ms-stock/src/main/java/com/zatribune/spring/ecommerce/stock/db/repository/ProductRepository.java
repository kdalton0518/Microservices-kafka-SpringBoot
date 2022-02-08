package com.zatribune.spring.ecommerce.stock.db.repository;

import com.zatribune.spring.ecommerce.stock.db.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
