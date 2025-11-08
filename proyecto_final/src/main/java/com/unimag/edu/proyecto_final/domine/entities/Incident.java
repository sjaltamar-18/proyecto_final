package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.loader.ast.internal.CacheEntityLoaderHelper;

import java.time.LocalDate;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType type;
    private Long entityId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type entityType;
    private String note;
    private LocalDate creationDate;




}
