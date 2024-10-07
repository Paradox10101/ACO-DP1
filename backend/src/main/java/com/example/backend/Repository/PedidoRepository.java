package com.example.backend.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Pedido;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {


    @Query("SELECT p FROM Pedido p WHERE p.fechaEntregaEstimada BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaEntregaEstimada DESC, p.cantidadPaquetes DESC")
    List<Pedido> findPedidoBetweenFechaInicioFechaFin(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
