package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Config;


public interface IConfigService {
    public Config findById(Long id);
}
