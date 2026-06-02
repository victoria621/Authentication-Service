package com.innowise.authenticationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotBlank
    @Column(name = "login")
    private String login;
    @NotBlank
    @Column(name = "password_hash")
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    @Column(name = "active")
    private boolean active;

    public User() {}

    public User(Long id, boolean active, Role role,  String passwordHash, String login) {
        this.id = id;
        this.active = active;
        this.role = role;
        this.passwordHash = passwordHash;
        this.login = login;
    }

}