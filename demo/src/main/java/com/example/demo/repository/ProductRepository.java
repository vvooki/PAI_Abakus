package com.example.demo.repository;

import com.example.demo.model.entities.Order;
import com.example.demo.model.entities.Product;
import com.example.demo.model.entities.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.HashSet;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {



}
