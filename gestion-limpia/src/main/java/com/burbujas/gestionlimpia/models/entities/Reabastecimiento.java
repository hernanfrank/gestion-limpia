package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "reabastecimiento")
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

    @Min(value = 0, message = "La cantidad de reabastecimiento no puede ser 0")
    private Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    private Proveedor proveedor;

    public Reabastecimiento() {
        this.fecha = new Date();
        this.cantidad = 100;
    }

    public Reabastecimiento(Producto producto, Date fecha, Integer cantidad, Proveedor proveedor) {
        this.producto = producto;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.proveedor = proveedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}
