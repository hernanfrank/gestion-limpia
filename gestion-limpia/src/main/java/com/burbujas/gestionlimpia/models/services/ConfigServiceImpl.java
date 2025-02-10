package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.repositories.IConfigRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class ConfigServiceImpl implements IConfigService{
    private final IConfigRepository configRepository;

    public ConfigServiceImpl(IConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public Config findById(Long id) {
        return this.configRepository.findById(id).orElse(null);
    }
}
