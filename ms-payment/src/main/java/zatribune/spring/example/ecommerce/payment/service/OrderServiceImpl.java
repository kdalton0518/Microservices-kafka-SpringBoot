package zatribune.spring.example.ecommerce.payment.service;


import domain.Order;
import domain.OrderSource;
import domain.Topics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import zatribune.spring.example.ecommerce.payment.db.entities.Customer;
import zatribune.spring.example.ecommerce.payment.db.repository.CustomerRepository;

import static domain.OrderStatus.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    private static final OrderSource SOURCE = OrderSource.PAYMENT;
    private final CustomerRepository repository;
    private final KafkaTemplate<Long, Order> template;

    public OrderServiceImpl(CustomerRepository repository, KafkaTemplate<Long, Order> template) {
        this.repository = repository;
        this.template = template;
    }

    @Override
    public void reserve(Order order) {
        Customer customer = repository.findById(order.getCustomerId()).orElseThrow();
        log.info("reserve order [{}] , for customer[{}]",order.getId(), customer);
        if (order.getPrice() < customer.getAmountAvailable()) {
            order.setStatus(ACCEPT);
            customer.setAmountReserved(customer.getAmountReserved() + order.getPrice());
            customer.setAmountAvailable(customer.getAmountAvailable() - order.getPrice());
        } else {
            order.setStatus(REJECTED);
        }
        order.setSource(SOURCE);
        repository.save(customer);
        template.send(Topics.PAYMENTS, order.getId(), order);
        log.info("Sent: {}", order);
    }

    @Override
    public void confirm(Order order) {
        Customer customer = repository.findById(order.getCustomerId()).orElseThrow();
        log.info("confirm order [{}] , for customer[{}]",order.getId(), customer);
        if (order.getStatus().equals(CONFIRMED)) {
            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
            repository.save(customer);
        } else if (order.getStatus().equals(ROLLBACK) && !order.getSource().equals(SOURCE)) {
            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
            customer.setAmountAvailable(customer.getAmountAvailable() + order.getPrice());
            repository.save(customer);
        }

    }
}
