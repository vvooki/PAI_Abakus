package com.example.demo.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum Permission {
    ROLE_ADMIN{
        public String toString() {
            return "admin";
        }
    },
    ROLE_USER{
        public String toString() {
            return "user";
        }
    }
}
