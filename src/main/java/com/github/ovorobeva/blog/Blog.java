package com.github.ovorobeva.blog;


import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "Blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "Title")
    @NotNull(message = "Title cannot be null")
    private String title;

    @Column (name = "Content")
    @NotNull(message = "Content cannot be null")
    private String content;

}
