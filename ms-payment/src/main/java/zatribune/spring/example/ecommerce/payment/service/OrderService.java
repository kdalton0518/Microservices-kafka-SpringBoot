package zatribune.spring.example.ecommerce.payment.service;

import domain.Order;

public interface OrderService {

    void reserve(Order order);

    void confirm(Order order);
}
