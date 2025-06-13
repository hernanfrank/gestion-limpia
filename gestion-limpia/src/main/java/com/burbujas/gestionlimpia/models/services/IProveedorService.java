package com.burbujas.gestionlimpia.models.services;

import java.util.List;

public interface IProveedorService {

    List<Object[]> countAllReabastecimientosGroupByProveedor();

    List<Object[]> sumAllReabastecimientosGroupByProveedor();

}
