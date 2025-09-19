package com.krish.ticket_booking.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "theaters")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    private String name;
    private String location;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager; // Theater Manager

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    private List<Screen> screens;

    private java.time.LocalDateTime createdAt;
}

