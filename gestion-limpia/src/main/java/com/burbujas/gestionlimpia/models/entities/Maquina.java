package com.burbujas.gestionlimpia.models.entities;

import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMaquina;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "maquinas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Maquina implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El número de máquina no puede ser vacío")
    private Integer numero;

    @NotEmpty(message = "El tipo de máquina no puede ser vacío")
    @Enumerated(EnumType.STRING)
    private TipoMaquina tipo;

    @OneToMany(mappedBy = "maquinaNueva", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialMaquinaPedido> historialMaquinaPedido;

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
