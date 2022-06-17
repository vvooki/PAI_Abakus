package com.example.demo.repository;

import com.example.demo.model.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ClientRepository extends JpaRepository <Client, Long>{

    Client findByEmail(String email);

    Client findById(long id);

    List<Client> findByName(String name);

    List<Client> findBySurname(String surname);

    List<Client> findByNameAndSurname(String name, String surname);

    List<Client> findByRole(String role);
}
