package com.burbujas.gestionlimpia.models.repositories;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface IClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT cliente FROM Cliente cliente WHERE cliente.nombreApellido LIKE %?1% ")
    List<Cliente> findByNombreApellidoLike(String nombreApellido);

    // usamos una nativeQuery para que ignore la restricción de eliminado <> true
    @Query(value = "SELECT * FROM clientes c WHERE c.eliminado = TRUE ORDER BY c.id DESC", nativeQuery = true)
    List<Cliente> findAllEliminados();

    List<Cliente> findByDni(String dni);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.pedidos f WHERE c.id=?1")
    // Busca todos los clientes junto con sus facturas, sino no lo haría por tener FetchType.LAZY
    public Cliente fetchByIdWithPedidos(Long id);

    @Query("SELECT COUNT(cliente.id) FROM Cliente cliente JOIN Pedido pedido ON pedido.cliente = cliente WHERE pedido.fechaHoraEntrega BETWEEN :fechaDesde AND :fechaHasta")
    Integer countAllByFechaAfterAndFechaBefore(@Param("fechaDesde") Timestamp fechaDesde, @Param("fechaHasta") Timestamp fechaHasta);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE clientes c
            SET c.eliminado = false
            WHERE c.id = :id
       """, nativeQuery = true)
    void restaurarCliente(@Param("id") Long id);
}
