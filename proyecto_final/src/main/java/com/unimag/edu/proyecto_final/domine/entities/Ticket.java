package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.PaymentMethod;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    @ManyToOne()
    @JoinColumn(name = "trip_id",nullable = false)
    private Trip trip;
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User passenger;
    private String seatNumber;
    @ManyToOne()
    @JoinColumn(name = "from_stop_id")
    private Stop fromStop;
    @ManyToOne()
    @JoinColumn(name = "to_stop_id")
    private Stop toStop;
    private Double price;
    private BigDecimal refundAmount;
    private LocalDateTime cancelledAt;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private StatusTicket statusTicket;
    private String qrCode;
    @OneToMany(mappedBy = "ticket")
    private Set<Baggage> baggages;
    @Column(nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = java.time.LocalDateTime.now();
    }



}