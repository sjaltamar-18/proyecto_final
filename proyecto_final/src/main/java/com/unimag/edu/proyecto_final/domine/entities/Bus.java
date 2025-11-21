package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "buses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 7,unique = true)
    private String plate;
    private Integer capacity;
    private LocalDate soatExp;
    private LocalDate revisionExp;

    @Column(columnDefinition = "TEXT")
    private String amenities;
    @Enumerated(EnumType.STRING)
    private StatusBus status;

    @OneToMany(mappedBy = "bus")
    private Set<Trip> trips;
}
