package com.github.arkadiusz97.online.voting.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Entity(name="online_voting_role")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Role {

    public Role(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "role")
    private Set<UserRole> usersRoles;
}
