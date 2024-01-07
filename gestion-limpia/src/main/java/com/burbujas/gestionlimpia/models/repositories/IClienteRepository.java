package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("select cliente from Cliente cliente WHERE cliente.nombreApellido LIKE %?1% ")
    List<Cliente> findByNombreApellidoLike(String nombreApellido);

    List<Cliente> findByDni(String dni);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.pedidos f WHERE c.id=?1")
    // Busca todos los clientes junto con sus facturas, sino no lo haría por tener FetchType.LAZY
    public Cliente fetchByIdWithPedidos(Long id);
}
