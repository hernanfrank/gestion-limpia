package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Pedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotEmpty
    @Column(name = "fecha_hora_ingreso")
    private Timestamp fechaHoraIngreso;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_hora_entrega_estimada")
    private Timestamp fechaHoraEntregaEstimada;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "El cliente asociado al pedido no puede estar vacío")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "El tipo de pedido no puede estar vacío")
    private TipoPedido tipo;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialEstadoPedido> historialEstadoPedido;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialMaquinaPedido> historialMaquinaPedido;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialProductoPedido> historialProductoPedido;

    @PrePersist
    public void prePersist() {
        // asigno la fecha de ingreso
        if (this.fechaHoraIngreso == null) {
            this.fechaHoraIngreso = new Timestamp(System.currentTimeMillis());
        }

        //asigno la fecha de entrega calculada según el tipo de pedido (fecha ingreso + duracion del pedido en dias)
        if(this.fechaHoraEntregaEstimada == null) {
            Calendar fechaEntregaCalculada = Calendar.getInstance();
            fechaEntregaCalculada.setTime(this.fechaHoraIngreso);
            fechaEntregaCalculada.add(Calendar.DAY_OF_MONTH, this.tipo.getDuracionEstimada());
            this.fechaHoraEntregaEstimada = new Timestamp(fechaEntregaCalculada.getTimeInMillis());
        }
    }

}
