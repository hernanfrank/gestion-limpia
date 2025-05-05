package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Entity
@Table(name = "config")
@Getter @Setter @AllArgsConstructor
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

    @NotNull(message = "El tiempo entre alertas no puede estar vacío. Para desactivarlas deje este campo en 0.")
    private Integer timeoutAlertaRabastecimiento;

    public Config() {
        this.nombreLavanderia = "Burbujas";
        this.logoLavanderia = new byte[0];
        this.entregaPedidosAutomatica = false;
        this.timeoutAlertaRabastecimiento = 5;
    }
}
