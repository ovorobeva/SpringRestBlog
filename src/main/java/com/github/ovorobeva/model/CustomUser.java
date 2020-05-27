package com.github.ovorobeva.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "Users")
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "Username")
    @NotNull
    private String username;

    @Column(name = "Password")
    @NotNull
    private String password;

    @Column(name = "Position")
    @NotNull
    private String position;

    @Column(name = "Role")
    @NotNull
    private String role;

}
