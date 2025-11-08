package com.unimag.edu.proyecto_final.domine.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne()
    @JoinColumn(name = "driver_id",nullable = false)
    private User driverUser;
    @ManyToOne()
    @JoinColumn(name = "dispatcher_id",nullable = false)
    private User dispatcher;
    @ManyToOne()
    @JoinColumn(name = "trip_id")
    private Trip trip;
    private String ChecklistOk;
    private LocalDate assignedDate;
}
