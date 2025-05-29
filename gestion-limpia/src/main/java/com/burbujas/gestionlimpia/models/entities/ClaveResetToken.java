package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clave_reset_tokens")
@Getter @Setter @NoArgsConstructor
public class ClaveResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @OneToOne(targetEntity = Config.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "config_id")
    private Config config;

    public ClaveResetToken(String token, Config config) {
        this.token = token;
        this.config = config;
        this.expiryDate = LocalDateTime.now().plusMinutes(15); // Token válido por 15 minutos
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
