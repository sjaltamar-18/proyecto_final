package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    // Phone, email(unico), status, Name, PasswordHash

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String passwordHash;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private StatusTicket status ;

    @OneToMany( mappedBy = "user")
    private Set<Ticket> tickets;
    @OneToMany(mappedBy = "user")
    private Set<SeatHold> seatHolds;

    @OneToMany(mappedBy = "driver")
    private Set<Assignment> driverAssignments;

    @OneToMany(mappedBy = "dispatcher")
    private Set<Assignment> dispatcherAssignments;


}
