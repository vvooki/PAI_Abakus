package com.example.demo.repository;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.ClientDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientDocumentsRepository extends JpaRepository<ClientDocuments, Long> {
    List<ClientDocuments> findByClient(Client client);

    List<ClientDocuments> findByClientAndIsSend(Client client, boolean isSend);


    @Modifying
    @Query("update ClientDocuments c set c.isSend = ?1 where c.id = ?2")
    void updateDocuments(Boolean send, Long id);
}
