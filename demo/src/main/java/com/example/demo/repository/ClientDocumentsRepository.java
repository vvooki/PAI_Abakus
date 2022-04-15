package com.example.demo.repository;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.ClientDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientDocumentsRepository extends JpaRepository<ClientDocuments, Long> {

}
