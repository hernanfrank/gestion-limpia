package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "historial_productos_pedidos")
@SQLDelete(sql = "UPDATE historial_productos_pedidos SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HistorialProductoPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Pedido pedido;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_hora_uso")
    private Timestamp fechaHoraUso;

    @NotNull
    private double cantidadUsada;

    private boolean eliminado = false;

    @PrePersist
    public void prePersist(){
        if(this.fechaHoraUso == null) {
            this.fechaHoraUso = new Timestamp(System.currentTimeMillis());
        }
    }

}
