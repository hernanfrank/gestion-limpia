package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Gasto;

import java.util.List;

public interface ICajaService {
    public List<Gasto> findAllGastosOrderByFechaDesc();

    public Gasto findGastoById(Long id);

    public void saveGasto(Gasto gasto);

    public void deleteGasto(Gasto gasto);
}
