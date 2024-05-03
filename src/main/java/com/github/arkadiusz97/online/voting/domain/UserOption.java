package com.github.arkadiusz97.online.voting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class UserOption {

    public UserOption(User user, Option option) {
        this.user = user;
        this.option = option;
    }

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name="option_id")
    private Option option;
}
