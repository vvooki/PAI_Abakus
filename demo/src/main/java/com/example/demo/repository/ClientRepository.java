package com.example.demo.repository;

import com.example.demo.model.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ClientRepository extends JpaRepository <Client, Long>{


}
