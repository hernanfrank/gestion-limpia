package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "config")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "*El nombre de la lavandería no puede estar vacío.")
    private String nombreLavanderia;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private byte[] logoLavanderia;

    private Boolean entregaPedidosAutomatica;

    @NotNull(message = "*El tiempo entre alertas no puede estar vacío. Para desactivarlas deje este campo en 0.")
    private Integer timeoutAlertaRabastecimiento;

    @NotEmpty(message = "*El email de acceso no puede estar vacío.")
    @Column(length=50)
    @Email(regexp = ".+[@].+[\\.].+", message = "*El email de acceso debe seguir el formato mi@email.com")
    private String emailAcceso;

    @NotEmpty(message = "*La clave de acceso no puede estar vacía.")
    @Size(min = 8, message = "*La clave debe tener al menos 8 caracteres.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
            message = "*La clave debe contener al menos una letra mayúscula, una letra minúscula y un número.")
    @Column(length = 60)
    private String claveAcceso; // BCrypt ya usa una salt internamente

    public void setClaveAcceso(String claveAcceso, BCryptPasswordEncoder passwordEncoder) {
        this.claveAcceso = passwordEncoder.encode(claveAcceso);
    }
}
