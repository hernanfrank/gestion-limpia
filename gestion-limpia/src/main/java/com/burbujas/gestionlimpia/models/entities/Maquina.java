package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maquinas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Maquina implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El tipo de máquina no puede ser vacío")
    private String tipo;

    @NotEmpty(message = "El número de máquina no puede ser vacío")
    private Integer numero;

    @OneToMany(mappedBy = "maquina", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialMaquinaPedido> historialMaquinaPedido;

}
