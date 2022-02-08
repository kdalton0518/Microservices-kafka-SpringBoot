package com.zatribune.spring.ecommerce.orders.config;


import domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.zatribune.spring.ecommerce.orders.service.OrderService;

import java.time.Duration;

import static domain.Topics.*;


@Slf4j
@Configuration
public class KafkaConfig {


    private final OrderService orderService;


    @Autowired
    public KafkaConfig(OrderService orderService) {
        this.orderService = orderService;
    }

    @Bean
    public NewTopic orders() {
        return TopicBuilder.name(ORDERS)
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name(PAYMENTS)
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic stockTopic() {
        return TopicBuilder.name(STOCK)
                .partitions(3)
                .compact()
                .build();
    }

    //this is the one that will get results from other topics to check
    @Bean
    public KStream<Long, Order> stream(StreamsBuilder builder) {
        //provides serialization and deserialization in JSON format


        Serde<Long> keySerde =  Serdes.Long();// same as // Serdes.LongSerde keySerde=new Serdes.LongSerde();
        JsonSerde<Order> valueSerde = new JsonSerde<>(Order.class);

        // Kafka stream => it's a record stream that represents key & value pairs
        // key and value serdes means Key & value serializers & deserializers
        //of course, the key in our case is the order id
        KStream<Long, Order> paymentStream = builder
                .stream(PAYMENTS, Consumed.with(keySerde, valueSerde));//Consumed With == passing some parameters for configuring the generated stream

        KStream<Long, Order> stockStream = builder
                .stream(STOCK,Consumed.with(keySerde, valueSerde));

        //join records from both tables
        paymentStream.join(
                stockStream,
                orderService::confirm,//the value joiner == the one responsible for joining the two records
                JoinWindows.of(Duration.ofSeconds(10)), // timestamps of matched records must fall within this window of time
                StreamJoined.with(keySerde, valueSerde, valueSerde)//the key must be the same, 1st stream serde, 2nd stream serde
                  )
                .peek((k,v)->log.info("Kafka stream match: key[{}],value[{}]",k,v))
                .to(ORDERS);

        return paymentStream;
    }
    /**
     * To build a persistent key-value store
     * This KTable will be used to store all the Orders
     ***/

    @Bean
    public KTable<Long, Order> table(StreamsBuilder builder) {

        KeyValueBytesStoreSupplier store = Stores.persistentKeyValueStore(ORDERS);

        Serde<Long> keySerde =  Serdes.Long();
        JsonSerde<Order> valueSerde = new JsonSerde<>(Order.class);

        KStream<Long, Order> stream = builder
                .stream(ORDERS, Consumed.with(keySerde, valueSerde))
                .peek((k,v)->log.info("Kafka persistence table: key[{}],value[{}]",k,v));

        return stream.toTable(Materialized.<Long, Order>as(store)
                .withKeySerde(keySerde)
                .withValueSerde(valueSerde));
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("kafkaSender-");
        executor.initialize();
        return executor;
    }
}
