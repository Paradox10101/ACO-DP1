package com.example.backend.Repository;

import com.example.backend.models.Mantenimiento;
import com.example.backend.models.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    @Query("SELECT m FROM Mantenimiento m WHERE :fechaActual BETWEEN m.fechaInicio AND m.fechaFin AND m.vehiculo.id_vehiculo = :idVehiculo")
    Optional<Mantenimiento> findMantenimientoByFechaAndVehiculo(@Param("fechaActual") LocalDateTime fechaActual, @Param("idVehiculo") Long idVehiculo);
}
