package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.repositories.IConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("ConfigServiceImpl")
public class ConfigServiceImpl implements IConfigService{
    private final IConfigRepository configRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ConfigServiceImpl(IConfigRepository configRepository, BCryptPasswordEncoder passwordEncoder) {
        this.configRepository = configRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Config findById(Long id) {
        return this.configRepository.findById(id).orElse(null);
    }

    @Override
    public boolean verificarClaveAcceso(String claveIngresada) {
        Config config = configRepository.findById(1L).orElse(null);
        if (config == null) {
            throw new IllegalArgumentException("Configuración de administración no encontrada");
        }

        return passwordEncoder.matches(claveIngresada, config.getClaveAcceso());
    }

    @Override
    public void cambiarClaveAcceso(String claveNueva) {
        Config config = configRepository.findById(1L).orElse(null);
        if (config == null) {
            throw new IllegalArgumentException("Configuración de administración no encontrada");
        }
        config.setClaveAcceso(claveNueva, passwordEncoder);
        this.save(config);
    }

    @Override
    public void save(Config config) {
        this.configRepository.save(config);
    }
}
