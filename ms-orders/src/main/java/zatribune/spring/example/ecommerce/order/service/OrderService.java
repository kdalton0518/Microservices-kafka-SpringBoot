package zatribune.spring.example.ecommerce.order.service;

import domain.Order;

public interface OrderService {

    Order confirm(Order orderPayment, Order orderStock);
}
