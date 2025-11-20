package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat-Holds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatHold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "trip_id",nullable = false)
    private Trip trip;
    private String seatNumber;
    private LocalDateTime expirationDate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private StatusSeatHold status;
    @ManyToOne
    @JoinColumn(name = "from_stop_id")
    private Stop fromStop;

    @ManyToOne
    @JoinColumn(name = "to_stop_id")
    private Stop toStop;

}
