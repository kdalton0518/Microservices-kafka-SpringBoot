package com.zatribune.spring.ecommerce.payments.service;

import domain.Order;

public interface OrderService {

    void reserve(Order order);

    void confirm(Order order);
}
