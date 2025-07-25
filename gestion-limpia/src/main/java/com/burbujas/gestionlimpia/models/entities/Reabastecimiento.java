package com.burbujas.gestionlimpia.models.entities;

import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "reabastecimientos")
@SQLDelete(sql = "UPDATE reabastecimientos SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @AllArgsConstructor @ToString
public class Reabastecimiento implements Serializable {

    @Id
    @ToString.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Producto producto;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "fecha")
    private Date fecha;

    @NotNull(message = "La cantidad de producto no puede estar vacía")
    @DecimalMin(value = "0.01", message = "La cantidad de producto no puede ser inferior a 0.01L")
    private Double cantidadProducto;

    @NotNull(message = "El precio no puede estar vacío")
    @DecimalMin(value = "0.0", message = "El precio no puede ser inferior a $0")
    private Double precio;

    @NotNull(message = "Debe especificar el proveedor del reabastecimiento")
    @ManyToOne(fetch = FetchType.LAZY)
    private Proveedor proveedor;

    @NotNull(message = "Debe especificar la caja de donde se realiza el pago del reabastecimiento")
    @Enumerated(EnumType.STRING)
    private TipoCaja tipoCaja;

    private boolean eliminado = false;

    public Reabastecimiento() {
        this.fecha = new Date();
        this.precio = 0.D;
    }


}
