package com.burbujas.gestionlimpia.models.entities;

import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "historial_estados_pedidos")
@SQLDelete(sql = "UPDATE historial_estados_pedidos SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HistorialEstadoPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Pedido pedido;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoPedido estadoAnterior;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoPedido estadoNuevo;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Column(name = "fecha_hora_cambio_estado")
    private Timestamp fechaHoraCambioEstado;

    private boolean eliminado = false;

    @PrePersist
    public void prePersist() {
        // asigno la fecha y hora del cambio de estado
        if(this.fechaHoraCambioEstado == null) {
            this.fechaHoraCambioEstado = new Timestamp(System.currentTimeMillis());
        }
    }

    public HistorialEstadoPedido(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido estadoNuevo, Timestamp fechaHoraCambioEstado) {
        this.pedido = pedido;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraCambioEstado = fechaHoraCambioEstado;
    }
}
