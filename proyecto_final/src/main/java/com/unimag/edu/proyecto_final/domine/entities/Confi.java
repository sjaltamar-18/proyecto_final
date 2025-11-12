package com.unimag.edu.proyecto_final.domine.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Confi {
    @Id
    private Long id;
    private String key;

    @Column(nullable = false)
    private String value;
}
