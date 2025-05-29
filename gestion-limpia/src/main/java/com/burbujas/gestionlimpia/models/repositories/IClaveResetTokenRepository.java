package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.ClaveResetToken;
import com.burbujas.gestionlimpia.models.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IClaveResetTokenRepository extends JpaRepository<ClaveResetToken, Long> {
    Optional<ClaveResetToken> findByToken(String token);

    void deleteByToken(String token);
}
