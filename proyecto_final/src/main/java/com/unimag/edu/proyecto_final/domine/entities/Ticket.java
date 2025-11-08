package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.PaymentMethod;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import jakarta.persistence.*;
import lombok.*;

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
    private Integer seatNumber;
    @ManyToOne()
    @JoinColumn(name = "from_stop_id")
    private Stop fromStop;
    @ManyToOne()
    @JoinColumn(name = "to_stop_id")
    private Stop toStop;
    private Double price;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private StatusTicket statusTicket;
    // private qr qr;todo opcional, igualmente lo pongo por si lo hacemos, sino lo dejamos asi :)
    @OneToMany(mappedBy = "ticket")
    private Set<Baggage> baggages;



}
