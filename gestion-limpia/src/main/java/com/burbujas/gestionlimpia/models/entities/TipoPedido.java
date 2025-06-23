package com.burbujas.gestionlimpia.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "tipos_pedido")
@SQLDelete(sql = "UPDATE tipos_pedido SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TipoPedido implements Serializable {
    @Id
    @ToString.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "La descripción no puede estar vacía.")
    private String descripcion;

    @NotNull(message = "La duración de lavado no puede ser 0")
    private Integer minutosDuracionLavado;

    @NotNull(message = "La duración de secado no puede ser 0")
    private Integer minutosDuracionSecado;

    @OneToMany(mappedBy = "tipoPedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TipoPedidoProductoMapping> tipoPedidoProductoMapping;

    private boolean eliminado = false;
}
