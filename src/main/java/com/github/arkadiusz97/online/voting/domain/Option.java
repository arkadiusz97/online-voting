package com.github.arkadiusz97.online.voting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
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
