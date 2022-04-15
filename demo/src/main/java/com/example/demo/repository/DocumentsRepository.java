package com.example.demo.repository;

import com.example.demo.model.entities.Documents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsRepository extends JpaRepository<Documents, Long> {
    Documents findById(long id);
}
