package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.repositories.IProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImpl implements IProveedorService{

    private final IProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(IProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<Object[]> countAllReabastecimientosGroupByProveedor() {
        return proveedorRepository.countAllReabastecimientos();
    }

    @Override
    public List<Object[]> sumAllReabastecimientosGroupByProveedor() {
        return proveedorRepository.sumAllCostoReabastecimientos();
    }
}
