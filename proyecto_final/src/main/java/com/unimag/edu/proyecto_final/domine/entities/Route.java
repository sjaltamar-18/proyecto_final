package com.unimag.edu.proyecto_final.domine.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String routeCode;
    @Column(nullable = false)
    private String routeName;
    private String originName;
    private String destinationName;
    private Double distance;
    private Integer time;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Stop> stops = new HashSet<>();
    @OneToMany(mappedBy = "route")
    private Set<Trip> trips;
    @OneToMany(mappedBy = "route")
    private Set<FareRule> fareRules;

}
