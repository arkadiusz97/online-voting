package com.github.arkadiusz97.online.voting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@Entity(name="online_voting_user")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class User {

    public User(String email, String password, Date created) {
        this.email = email;
        this.password = password;
        this.created = created;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Date created;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> usersRoles;
}
