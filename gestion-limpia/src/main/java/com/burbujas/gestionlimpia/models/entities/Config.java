package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "config")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre de la lavandería no puede estar vacío.")
    private String nombreLavanderia;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private byte[] logoLavanderia;

    private Boolean entregaPedidosAutomatica;

}
