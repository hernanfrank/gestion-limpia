package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "historial_estado_pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HistorialEstadoPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private EstadoPedido estado;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotEmpty
    @Column(name = "fecha_hora_cambio_estado")
    private Timestamp fechaHoraCambioEstado;


    @PrePersist
    public void prePersist() {
        // asigno la fecha y hora del cambio de estado
        if(this.fechaHoraCambioEstado == null) {
            this.fechaHoraCambioEstado = new Timestamp(System.currentTimeMillis());
        }
    }

}
