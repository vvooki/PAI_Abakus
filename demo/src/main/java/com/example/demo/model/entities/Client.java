package com.example.demo.model.entities;

import com.vaadin.flow.component.textfield.TextField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.context.annotation.Bean;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_client;
    @Type(type="text")
    private String name;
    @Type(type="text")
    private String surname;
    private int phoneNumber;
    @Type(type="text")
    private String email;
    @Type(type="text")
    private String password;
    @Type(type="text")
    private String role;

    public String getRole() {
        return role;
    }
}
