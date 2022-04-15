package com.example.demo.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_order;
//    @JsonIgnore
//    @OneToMany
//            cascade = CascadeType.ALL,
//            fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "order_to_product",
//            joinColumns = @JoinColumn(name = "id_order"),
//            inverseJoinColumns = @JoinColumn(name = "id_product")
//    )
    //private Set<Product> products = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;
    private LocalDate registrationDateTime;

}
