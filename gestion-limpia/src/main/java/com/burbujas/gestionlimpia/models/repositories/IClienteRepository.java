package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT cliente FROM Cliente cliente WHERE cliente.nombreApellido LIKE %?1% ")
    List<Cliente> findByNombreApellidoLike(String nombreApellido);

    List<Cliente> findByDni(String dni);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.pedidos f WHERE c.id=?1")
    // Busca todos los clientes junto con sus facturas, sino no lo haría por tener FetchType.LAZY
    public Cliente fetchByIdWithPedidos(Long id);

    Page<Cliente> findAll(Pageable pageRequest);
}
