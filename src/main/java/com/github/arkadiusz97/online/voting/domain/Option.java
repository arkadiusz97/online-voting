package com.github.arkadiusz97.online.voting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Option {

    public Option(String description, Voting voting) {
        this.description = description;
        this.voting = voting;
    }

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String description;

    @ManyToOne
    @JoinColumn(name="voting_id")
    private Voting voting;
}
