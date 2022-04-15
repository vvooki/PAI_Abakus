package com.example.demo.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Documents document;
    @OneToOne
    private Client client;
    @OneToOne
    private Product product;
    private boolean isSend;
    private LocalDate notificationDate;
}
