package com.zatribune.spring.ecommerce.orders.service;

import domain.Order;
import domain.OrderSource;
import domain.OrderStatus;
import org.springframework.stereotype.Service;

import static domain.OrderStatus.*;
import static domain.OrderSource.*;

@Service
public class OrderServiceImpl implements OrderService{


    @Override
    public Order confirm(Order orderPayment, Order orderStock) {

        Order o = Order.builder()
                .id(orderPayment.getId())
                .customerId(orderPayment.getCustomerId())
                .productId(orderPayment.getProductId())
                .productCount(orderPayment.getProductCount())
                .price(orderPayment.getPrice())
                .build();

        if (orderPayment.getStatus().equals(ACCEPT) &&
                orderStock.getStatus().equals(ACCEPT)) {
            o.setStatus(CONFIRMED);
        } else if (orderPayment.getStatus().equals(REJECT) &&
                orderStock.getStatus().equals(REJECT)) {
            o.setStatus(REJECTED);
        } else if (orderPayment.getStatus().equals(REJECT) ||
                orderStock.getStatus().equals(REJECT)) {
            OrderSource source = orderPayment.getStatus().equals(REJECT)
                    ? PAYMENT : STOCK;
            o.setStatus(OrderStatus.ROLLBACK);
            o.setSource(source);
        }
        return o;
    }

}
