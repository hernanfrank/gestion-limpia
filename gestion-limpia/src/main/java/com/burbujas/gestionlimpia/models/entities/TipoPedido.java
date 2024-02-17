package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "tipo_pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TipoPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "La descripcion del tipo de pedido no puede estar vacía")
    private String descripcion;

    @NotEmpty(message = "El precio del tipo de pedido no puede estar vacía")
    private Double precio;

    @NotEmpty(message = "La duración estimada del tipo de pedido no puede estar vacía")
    private Integer duracionEstimada; // en dias

    @OneToMany(mappedBy = "tipo", fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

}
