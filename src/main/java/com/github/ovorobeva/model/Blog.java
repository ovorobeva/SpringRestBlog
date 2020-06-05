package com.github.ovorobeva.model;


import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private CustomUser user;

}
