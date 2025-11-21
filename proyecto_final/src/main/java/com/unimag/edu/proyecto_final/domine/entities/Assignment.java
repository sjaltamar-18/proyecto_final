package com.unimag.edu.proyecto_final.domine.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "driver_id",nullable = false)
    private User driver;
    @ManyToOne()
    @JoinColumn(name = "dispatcher_id",nullable = false)
    private User dispatcher;
    @ManyToOne()
    @JoinColumn(name = "trip_id")
    private Trip trip;
    private Boolean checklistOk;
    private LocalDateTime assignedDate;
}