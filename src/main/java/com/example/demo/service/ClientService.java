package com.example.demo.service;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.ClientDocuments;
import com.example.demo.model.entities.Documents;
import com.example.demo.repository.ClientDocumentsRepository;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientDocumentsRepository clientDocumentsRepository;

    public List<Client> getClients() { return clientRepository.findAll(); }

    public List<ClientDocuments> getClientDocuments() { return clientDocumentsRepository.findAll(); }

}
