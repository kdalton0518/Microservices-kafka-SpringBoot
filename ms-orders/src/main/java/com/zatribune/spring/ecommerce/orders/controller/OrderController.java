package com.zatribune.spring.ecommerce.orders.controller;


import domain.Order;
import domain.OrderStatus;
import domain.Topics;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequestMapping("/orders")
@RestController
public class OrderController {


    private final AtomicLong id = new AtomicLong();
    private final KafkaTemplate<Long, Order> kafkaTemplate;
    private final StreamsBuilderFactoryBean kafkaStreamsFactory;


    @Autowired
    public OrderController(KafkaTemplate<Long, Order> kafkaTemplate,
                           StreamsBuilderFactoryBean kafkaStreamsFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaStreamsFactory = kafkaStreamsFactory;
    }

    @PostMapping
    public Order create(@RequestBody Order order) throws ExecutionException, InterruptedException {
        order.setId(id.incrementAndGet());
        order.setStatus(OrderStatus.NEW);
        log.info("Sent: {}", order);
        return kafkaTemplate.send(Topics.ORDERS, order.getId(), order).get().getProducerRecord().value();
    }

    @GetMapping
    public List<Order> all() {
        List<Order> orders = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, Order> store = kafkaStreamsFactory
                .getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(
                        Topics.ORDERS,
                        QueryableStoreTypes.keyValueStore()));
        KeyValueIterator<Long, Order> it = store.all();
        it.forEachRemaining(kv -> orders.add(kv.value));
        return orders;
    }
}
