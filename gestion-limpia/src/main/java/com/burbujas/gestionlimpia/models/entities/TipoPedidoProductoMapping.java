package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tipo_pedido_producto_mapping")
@SQLDelete(sql = "UPDATE tipo_pedido_producto_mapping SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @NoArgsConstructor
public class TipoPedidoProductoMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "El tipo de pedido relacionado al producto no puede ser vacío")
    private TipoPedido tipoPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "El producto relacionado al tipo de pedido no puede ser vacío")
    private Producto producto;

    @DecimalMin(value = "0.0", message = "La cantidad de producto usada por pedido no puede ser negativa")
    private Double cantidadUsada;

    private boolean eliminado = false;
}
