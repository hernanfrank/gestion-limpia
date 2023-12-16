package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "pedidos")
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
    @NotEmpty(message = "El cliente asociado al pedido no puede estar vacío")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotEmpty(message = "El tipo de pedido no puede estar vacío")
    private TipoPedido tipo;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialEstadoPedido> historialEstados;

    public Pedido() {
        this.historialEstados = new ArrayList<HistorialEstadoPedido>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PrePersist
    public void prePersist() {
        // asigno la fecha de ingreso
        this.fechaHoraIngreso = new Timestamp(System.currentTimeMillis());

        //asigno la fecha de entrega calculada según el tipo de pedido (fecha ingreso + duracion del pedido en dias)
        Calendar fechaEntregaCalculada = Calendar.getInstance();
        fechaEntregaCalculada.setTime(this.fechaHoraIngreso);
        fechaEntregaCalculada.add(Calendar.DAY_OF_MONTH, this.tipo.getDuracionEstimada().getDays());
        this.fechaHoraEntregaEstimada = new Timestamp(fechaEntregaCalculada.getTimeInMillis());
    }

    public Timestamp getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(Timestamp fecha_hora_ingreso) {
        this.fechaHoraIngreso = fecha_hora_ingreso;
    }

    public Timestamp getFechaHoraEntregaEstimada() {
        return fechaHoraEntregaEstimada;
    }

    public void setFechaHoraEntregaEstimada(Timestamp fecha_entrega_estimada) {
        this.fechaHoraEntregaEstimada = fecha_entrega_estimada;
    }

    @XmlTransient
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoPedido getTipo() {
        return tipo;
    }

    public void setTipo(TipoPedido tipo) {
        this.tipo = tipo;
    }

    public List<HistorialEstadoPedido> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(List<HistorialEstadoPedido> historialEstados) {
        this.historialEstados = historialEstados;
    }

}
