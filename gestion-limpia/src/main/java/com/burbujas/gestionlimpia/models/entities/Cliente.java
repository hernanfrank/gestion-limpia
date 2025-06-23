package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "clientes")
@SQLDelete(sql = "UPDATE clientes SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class Cliente implements Serializable {

    @Id
    @ToString.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^$|\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @NotEmpty(message = "El nombre y apellido del cliente no pueden estar vacíos")
    @Size(min = 3, max = 250, message = "El nombre y apellido debe tener entre 3 y 250 caracteres")
    private String nombreApellido;

    @Size(max = 250, message = "La dirección no puede ser mayor a 250 caracteres")
    private String direccion;

    // REGEX que evalúa teléfonos argentinos
    // Referencia: https://www.regextester.com/107303
    //@Pattern(regexp="(^(?:(?:00)?549?)?0?(?:11|[2368]\\\\d)(?:(?=\\\\d{0,2}15)\\\\d{2})??\\\\d{8}$)", message = "El número de teléfono debe seguir el formato (Código de área)(Número)")
    @Pattern(regexp = "^$|(?:(?:00)?549?)?0?(?:11|[2368]\\d)(?:(?=\\d{0,2}15)\\d{2})??\\d{8}$", message = "El número de teléfono debe seguir el formato (Código de área)(Número)")
    private String telefono;

    @ToString.Exclude
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    private boolean eliminado = false;

    public void addPedido(Pedido pedido){
        this.pedidos.add(pedido);
    }

}
