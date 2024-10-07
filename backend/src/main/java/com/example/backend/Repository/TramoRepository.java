package com.example.backend.Repository;

import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Tramo;
import com.example.backend.models.Vehiculo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {

    List<Tramo> findByPlanTransporte(PlanTransporte planTransporte);

    @Modifying
    @Transactional
    @Query("UPDATE Tramo t SET t.transitado = true WHERE t.fechaInicio >= :fechaInicio AND t.fechaFin <= :fechaFin")
    void actualizarTransitados(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);


    @Query("SELECT t FROM Tramo t WHERE :fechaActual BETWEEN t.fechaInicio AND t.fechaFin AND t.vehiculo.id_vehiculo = :idVehiculo")
    Optional<Tramo> findTramoByFechaAndVehiculo(@Param("fechaActual") LocalDateTime fechaActual, @Param("idVehiculo") Long idVehiculo);

    @Query("SELECT t FROM Tramo t WHERE t.fechaFin <= :fechaActual AND t.vehiculo.id_vehiculo = :idVehiculo ORDER BY t.fechaFin DESC LIMIT 1")
    Optional<Tramo> findLastTramoByFechaAndVehiculo(@Param("fechaActual") LocalDateTime fechaActual, @Param("idVehiculo") Long idVehiculo);

    @Query("SELECT t FROM Tramo t WHERE t.vehiculo.id_vehiculo = :idVehiculo ORDER BY t.fechaFin DESC")
    Optional<Tramo> findLastTramoPedidoByVehiculo(@Param("idVehiculo") Long idVehiculo);


    @Query("SELECT t FROM Tramo t WHERE t.fechaInicio >= :fechaInicio " + "AND t.fechaFin <= :fechaLimite " + "AND t.vehiculo = :vehiculo")
    List<Tramo> findTramoBetweenFechasAndVehiculo(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaLimite") LocalDateTime fechaLimite,
            @Param("vehiculo") Vehiculo vehiculo);

}
