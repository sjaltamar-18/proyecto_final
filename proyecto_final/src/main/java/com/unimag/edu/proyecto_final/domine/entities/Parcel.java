package com.unimag.edu.proyecto_final.domine.entities;

import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusParcel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String senderName;
    private Integer senderPhone;
    private String receiverName;
    private Integer receiverPhone;
    @ManyToOne()
    @JoinColumn(name = "from_stop_id")
    private Stop fromStop;
    @ManyToOne()
    @JoinColumn(name = "to_stop_id")
    private Stop toStop;
    private BigDecimal price;
    private StatusParcel statusParcel;
}
