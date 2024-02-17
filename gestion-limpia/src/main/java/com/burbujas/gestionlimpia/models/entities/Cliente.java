package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El DNI no puede estar vacío")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 caracteres")
    private String dni;

    @NotEmpty(message = "El nombre y apellido del cliente no pueden estar vacíos")
    @Size(min = 1, max = 250, message = "El nombre y apellido no puede ser mayor a 250 caracteres")
    private String nombreApellido;

    @Size(min = 1, max = 250, message = "La dirección no puede ser mayor a 250 caracteres")
    private String direccion;

    // REGEX que evalúa teléfonos argentinos
    // Referencia: https://www.regextester.com/107303
    //@Pattern(regexp="(^(?:(?:00)?549?)?0?(?:11|[2368]\\\\d)(?:(?=\\\\d{0,2}15)\\\\d{2})??\\\\d{8}$)", message = "El número de teléfono debe seguir el formato (Código de área)(Número)")
    @Pattern(regexp = "^(?:(?:00)?549?)?0?(?:11|[2368]\\d)(?:(?=\\d{0,2}15)\\d{2})??\\d{8}$", message = "El número de teléfono debe seguir el formato (Código de área)(Número)")
    private String telefono;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    public void addPedido(Pedido pedido){
        this.pedidos.add(pedido);
    }

}
