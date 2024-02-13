package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProveedorRepository extends JpaRepository<Proveedor, Long> {
}
