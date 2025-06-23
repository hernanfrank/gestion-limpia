package com.burbujas.gestionlimpia.models.entities;

import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMaquina;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "maquinas")
@SQLDelete(sql = "UPDATE maquinas SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Maquina implements Serializable {
    @Id
    @ToString.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El número de máquina no puede ser vacío")
    private Integer numero;

    @NotEmpty(message = "El tipo de máquina no puede ser vacío")
    @Enumerated(EnumType.STRING)
    private TipoMaquina tipo;

    @OneToMany(mappedBy = "maquinaNueva", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialMaquinaPedido> historialMaquinaPedido;

    private boolean eliminado = false;

    public EstadoPedido getEstadoAsociado(){
        switch (this.tipo){
            case LAVADORA -> {
                return EstadoPedido.LAVADO;
            }
            case SECADORA -> {
                return EstadoPedido.SECADO;
            }
            case NINGUNO -> {
                return null;
            }
        }
        return null;
    }
}
