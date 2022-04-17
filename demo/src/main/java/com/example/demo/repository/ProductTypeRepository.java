package com.example.demo.repository;

import com.example.demo.model.entities.Documents;
import com.example.demo.model.entities.Product;
import com.example.demo.model.entities.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

    @Query("SELECT p from ProductType p WHERE ?1 member of p.documents")
    public List<ProductType> selectDocuemnt(Documents documents);


}
