package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.DynamicPricing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fare-Rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "route_id")
    private Route route;
    @ManyToOne()
    @JoinColumn(name = "from_stop_id")
    private Stop fromStop;
    @ManyToOne()
    @JoinColumn(name = "to_stop_id")
    private Stop toStop;
    private Double basePrice;
    @Column(columnDefinition = "TEXT")
    private String discountPrice;
    @Enumerated(EnumType.STRING)
    private DynamicPricing dynamicPricing;
}