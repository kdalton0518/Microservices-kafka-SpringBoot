package com.zatribune.spring.ecommerce.orders.service;

import domain.Order;

public interface OrderService {

    Order confirm(Order orderPayment, Order orderStock);
}
