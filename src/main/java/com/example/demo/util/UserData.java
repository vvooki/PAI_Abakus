package com.example.demo.util;

import com.example.demo.model.entities.Client;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.Setter;

@SpringComponent
@UIScope
public class UserData {
    @Getter
    @Setter
    private Client client;
}