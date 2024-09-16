package com.burbujas.gestionlimpia.models.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "gastos")
@Getter @Setter @AllArgsConstructor
public class Gasto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "fecha")
    private Date fecha;

    @NotEmpty(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El monto no puede estar vacío")
    @DecimalMin(value = "1.0", message = "El monto no puede ser inferior a $1")
    private Double monto;

    public Gasto() {
        this.fecha = new Date();
        this.monto = 1.0D;
    }
}
