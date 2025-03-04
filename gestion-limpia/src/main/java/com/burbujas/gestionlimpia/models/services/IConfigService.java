package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Config;
import jakarta.validation.Valid;


public interface IConfigService {
    public Config findById(Long id);

    void save(@Valid Config config);
}
