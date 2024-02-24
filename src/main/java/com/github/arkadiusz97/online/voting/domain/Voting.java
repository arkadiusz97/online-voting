package com.github.arkadiusz97.online.voting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Voting {
    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private Date endDate;
    private Date createdDate;
    private Boolean isFinished;

    @ManyToOne
    @JoinColumn(name="created_by_id")
    private User createdBy;
}
