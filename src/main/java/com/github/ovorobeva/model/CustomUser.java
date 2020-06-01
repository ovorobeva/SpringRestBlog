package com.github.ovorobeva.model;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "Users")
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true)
    private long id;

    @Column(name = "Username",unique = true)
    @NotNull
    private String username;

    @Column(name = "Password")
    @NotNull
    private String password;

    @Column(name = "Role", updatable = false)
    @NotNull
    private String role;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Blog> blogs;

}
