package com.zatribune.spring.ecommerce.payments.db;


import com.zatribune.spring.ecommerce.payments.db.entities.Customer;
import com.zatribune.spring.ecommerce.payments.db.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class DevBootstrap implements CommandLineRunner {


    private final CustomerRepository repository;

    @Autowired
    public DevBootstrap(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        Customer p1=Customer.builder().name("John Doe")
                .amountAvailable(4000)
                .amountReserved(0)
                .build();

        Customer p2=Customer.builder().name("Muhammad Ali")
                .amountAvailable(8000)
                .amountReserved(0)
                .build();

        Customer p3=Customer.builder().name("Steve Jobs")
                .amountAvailable(1000)
                .amountReserved(0)
                .build();

        Customer p4=Customer.builder().name("Bill Gits")
                .amountAvailable(2000)
                .amountReserved(0)
                .build();

        repository.saveAll(List.of(p1,p2,p3,p4));
    }
}
