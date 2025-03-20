package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "logging")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String logic;
    private String description;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Log(String logic, String description) {
        this.logic = logic;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}
