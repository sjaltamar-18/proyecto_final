package com.unimag.edu.proyecto_final.domine.entities;

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

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private  String name;
    private String email;
    private Status status;
    private Integer phone;

    @OneToMany( mappedBy = "user")
    private Set<Ticket> tickets;


}
