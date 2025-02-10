package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IConfigRepository extends JpaRepository<Config, Long> {
    public Optional<Config> findById(Long id);
}
