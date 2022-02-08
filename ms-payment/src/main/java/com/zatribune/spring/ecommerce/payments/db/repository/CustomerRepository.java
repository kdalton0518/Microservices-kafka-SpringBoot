package com.zatribune.spring.ecommerce.payments.db.repository;

import com.zatribune.spring.ecommerce.payments.db.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}