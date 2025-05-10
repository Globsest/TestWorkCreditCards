package com.globsest.testworkcreditcards.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password_hash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @JsonCreator
    public static Role forValue(String value) {
        return Role.valueOf(value.toUpperCase());
    }
    private boolean active = true;
    private String lastName;
    private String firstName;
    private String middleName;
}
