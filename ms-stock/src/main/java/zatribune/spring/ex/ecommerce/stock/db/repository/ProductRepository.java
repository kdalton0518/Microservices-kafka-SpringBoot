package zatribune.spring.ex.ecommerce.stock.db.repository;

import zatribune.spring.ex.ecommerce.stock.db.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
