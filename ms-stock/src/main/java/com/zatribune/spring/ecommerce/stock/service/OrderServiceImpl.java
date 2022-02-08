package com.zatribune.spring.ecommerce.stock.service;

import com.zatribune.spring.ecommerce.stock.db.entities.Product;
import com.zatribune.spring.ecommerce.stock.db.repository.ProductRepository;
import domain.OrderSource;
import domain.OrderStatus;
import domain.Topics;
import domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    private static final OrderSource SOURCE = OrderSource.STOCK;
    private final ProductRepository repository;
    private final KafkaTemplate<Long, Order> template;

    public OrderServiceImpl(ProductRepository repository, KafkaTemplate<Long, Order> template) {
        this.repository = repository;
        this.template = template;
    }

    @Override
    public void reserve(Order order) {
        Product product = repository.findById(order.getProductId()).orElseThrow();
        log.info("Found: {}", product);
        if (order.getStatus().equals(OrderStatus.NEW)) {
            if (order.getProductCount() < product.getAvailableItems()) {
                product.setReservedItems(product.getReservedItems() + order.getProductCount());
                product.setAvailableItems(product.getAvailableItems() - order.getProductCount());
                order.setStatus(OrderStatus.ACCEPT);
                repository.save(product);
            } else {
                order.setStatus(OrderStatus.REJECT);
            }
            template.send(Topics.STOCK, order.getId(), order);
            log.info("Sent: {}", order);
        }
    }

    @Override
    public void confirm(Order order) {
        Product product = repository.findById(order.getProductId()).orElseThrow();
        log.info("Found: {}", product);
        if (order.getStatus().equals(OrderStatus.CONFIRMED)) {
            product.setReservedItems(product.getReservedItems() - order.getProductCount());
            repository.save(product);
        } else if (order.getStatus().equals(OrderStatus.ROLLBACK) && !order.getSource().equals(SOURCE)) {
            product.setReservedItems(product.getReservedItems() - order.getProductCount());
            product.setAvailableItems(product.getAvailableItems() + order.getProductCount());
            repository.save(product);
        }
    }

}
