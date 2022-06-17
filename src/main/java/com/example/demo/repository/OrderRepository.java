package com.example.demo.repository;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClient(Client client);

    Order findById(long id);

}
