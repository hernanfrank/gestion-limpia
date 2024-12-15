package com.burbujas.gestionlimpia.models.entities;


import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMovimientoCaja;
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
@Table(name = "movimientos_caja")
@Getter @Setter @AllArgsConstructor
public class MovimientoCaja implements Serializable {

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Pedido pedido;// el movimiento puede ser un ingreso asociado a un pedido,
    @ManyToOne(fetch = FetchType.LAZY)
    private Cliente cliente;// una nota de cŕedito/débito a un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    private Proveedor proveedor;// una nota de cŕedito/débito a un proveedor
    //o un movimiento suelto (un gasto de reparación por ejemplo)
    @ManyToOne(fetch = FetchType.LAZY)
    private Reabastecimiento reabastecimiento; // para poder hacer consultas del tipo "Cuanta plata se gastó en reabastecimientos de jabón liquido"

    @NotNull(message = "Debe especificar un tipo para el movimiento de caja")
    @Enumerated(EnumType.STRING)
    private TipoMovimientoCaja tipoMovimientoCaja;

    @NotNull(message = "Debe especificar la caja para el movimiento")
    @Enumerated(EnumType.STRING)
    private TipoCaja tipoCaja;

    public MovimientoCaja() {
        this.fecha = new Date();
        this.monto = 1.0D;
        this.pedido = null;
        this.cliente = null;
        this.proveedor = null;
        this.reabastecimiento = null;
    }
}
