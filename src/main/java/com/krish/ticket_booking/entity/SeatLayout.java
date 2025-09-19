package com.krish.ticket_booking.entity;

import com.krish.ticket_booking.entity.enums.LayoutType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "seat_layouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private LayoutType layoutType; // STANDARD, IMAX, 4DX, etc.

    @OneToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @OneToMany(mappedBy = "seatLayout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatRow> rows = new ArrayList<>();
}
