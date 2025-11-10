package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    @ManyToOne()
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;
    private LocalDate date;
    private LocalDateTime departureAt;
    private LocalDateTime arrivalAt;
    @Enumerated(EnumType.STRING)
    private StatusTrip statusTrip;

    @OneToMany(mappedBy = "trip")
    private Set<Ticket> tickets;

    @OneToMany(mappedBy = "trip")
    private Set<SeatHold> seatHolds;

}
