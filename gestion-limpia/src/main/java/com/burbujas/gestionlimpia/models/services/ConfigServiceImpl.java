package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.repositories.IConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("ConfigServiceImpl")
public class ConfigServiceImpl implements IConfigService{
    private final IConfigRepository configRepository;

    public ConfigServiceImpl(IConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public Config findById(Long id) {
        return this.configRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Config config) {
        this.configRepository.save(config);
    }
}
