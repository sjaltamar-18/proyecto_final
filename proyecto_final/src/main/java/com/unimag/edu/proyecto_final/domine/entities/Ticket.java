package com.unimag.edu.proyecto_final.domine.entities;

import jakarta.persistence.*;
import lombok.*;

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
    private User user;
    private SeatHold seatNumber;
    private Stop fromStop;
    private Stop toStop;
    private Double price;
    private PaymentMethod paymentMethod;
    private Status status;
    // private qr qr;todo opcional, igualmente lo pongo por si lo hacemos, sino lo dejamos asi :)



}
