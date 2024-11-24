package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "proveedores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre del proveedor no puede estar vacío")
    private String nombre;

    @NotEmpty(message = "El DNI no puede estar vacío")
    @Size(min = 11, max = 11, message = "El CUIT debe tener 11 caracteres")
    private String cuit;

    @Size(max = 250, message = "La dirección no puede ser mayor a 250 caracteres")
    private String direccion;

    // REGEX que evalúa teléfonos argentinos
    // Referencia: https://www.regextester.com/107303
    //@Pattern(regexp="(^(?:(?:00)?549?)?0?(?:11|[2368]\\\\d)(?:(?=\\\\d{0,2}15)\\\\d{2})??\\\\d{8}$)", message = "El número de teléfono debe seguir el formato (Código de área)(Número)")
    @Pattern(regexp = "^$|(?:(?:00)?549?)?0?(?:11|[2368]\\d)(?:(?=\\d{0,2}15)\\d{2})??\\d{8}$", message = "El número de teléfono debe seguir el formato (Código de área)(Número)")
    private String telefono;


    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reabastecimiento> reabastecimientos;

}
