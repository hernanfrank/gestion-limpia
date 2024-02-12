package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
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

    public Cliente(){
        this.pedidos = new ArrayList<Pedido>();
    }

    public Cliente(String dni, String nombreApellido, String direccion, String telefono) {
        this.dni = dni;
        this.nombreApellido = nombreApellido;
        this.direccion = direccion;
        this.telefono = telefono;
        this.pedidos = new ArrayList<Pedido>();
    }

    public void addPedido(Pedido pedido){
        this.pedidos.add(pedido);
    }

    public void setPedidos(List<Pedido> pedidos){
        this.pedidos = pedidos;
    }

    public List<Pedido> getPedidos(){
        return pedidos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombreApellido() {
        return nombreApellido;
    }

    public void setNombreApellido(String nombre_apellido) {
        this.nombreApellido = nombre_apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
