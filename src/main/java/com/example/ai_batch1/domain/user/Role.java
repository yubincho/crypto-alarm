package com.example.ai_batch1.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;


@NoArgsConstructor
@Setter
@Getter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // role name


    public Role(String name) {
        this.name = name;
    }


    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> users = new HashSet<>();
}
