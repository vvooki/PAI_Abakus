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

    @Query(value = "select p FROM Product p where p.id_product > (:id) ")
    List<Product> getGreaterThan(@Param("id") long id);

    List<Product> findByOrder (Order order);

    Product findByProductType(ProductType productType);

    Product findById(long id);

}
