package com.example.demo.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_product;
    @ManyToOne
    @JoinColumn(name = "id_order")
    private Order order;
    private LocalDate startExecuting;
    @OneToOne
    private ProductType productType;
    private LocalDate deadline;
    private int prices;

    //private int averageTimeInDays;

}