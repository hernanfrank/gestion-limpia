package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "reabastecimientos")
@Getter @Setter @AllArgsConstructor
public class Reabastecimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "fecha")
    private Date fecha;

    @NotNull(message = "La cantidad de producto no puede estar vacía")
    private Double cantidadProducto;

    @NotNull(message = "El precio no puede estar vacío")
    private Double precio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Proveedor proveedor;

    public Reabastecimiento() {
        this.fecha = new Date();
        this.precio = 0.D;
    }


}
