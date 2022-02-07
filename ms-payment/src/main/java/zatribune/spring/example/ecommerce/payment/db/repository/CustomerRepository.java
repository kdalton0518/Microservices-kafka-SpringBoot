package zatribune.spring.example.ecommerce.payment.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zatribune.spring.example.ecommerce.payment.db.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}