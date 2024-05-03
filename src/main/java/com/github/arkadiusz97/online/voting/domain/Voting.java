package com.github.arkadiusz97.online.voting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Voting {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String description;

    @NotNull
    private Date endDate;

    @NotNull
    private Date createdDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name="created_by_id")
    private User createdBy;
}
