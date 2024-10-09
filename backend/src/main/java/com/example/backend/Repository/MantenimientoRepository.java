package com.example.backend.Repository;

import com.example.backend.models.Mantenimiento;
import com.example.backend.models.TipoMantenimiento;
import com.example.backend.models.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
        @Query("SELECT m FROM Mantenimiento m WHERE :fechaActual BETWEEN m.fechaInicio AND m.fechaFin AND m.vehiculo.id_vehiculo = :idVehiculo")
        Optional<Mantenimiento> findMantenimientoByFechaAndVehiculo(@Param("fechaActual") LocalDateTime fechaActual, @Param("idVehiculo") Long idVehiculo);

        @Query("SELECT m FROM Mantenimiento m WHERE :fechaActual = m.fechaProgramada AND m.vehiculo.id_vehiculo = :idVehiculo AND m.tipo = :tipoMantenimiento")
        List<Mantenimiento> findMantenimientoProgramadoByFechaAndVehiculoAndTipoMantenimiento(
                @Param("fechaActual") LocalDate fechaActual,
                @Param("idVehiculo") Long idVehiculo,
                @Param("tipoMantenimiento") TipoMantenimiento tipoMantenimiento);

        @Query("SELECT m FROM Mantenimiento m WHERE :fechaActual BETWEEN m.fechaInicio AND m.fechaFin AND m.vehiculo.id_vehiculo = :idVehiculo AND m.tipo = :tipoMantenimiento ORDER BY m.fechaFin DESC LIMIT 1")
        Optional <Mantenimiento> findMantenimientoRecurrenteByFechaAndVehiculoAndTipoMantenimiento(@Param("fechaActual") LocalDateTime fechaActual,
                                                                                                @Param("idVehiculo") Long idVehiculo,
                                                                                                @Param("tipoMantenimiento") TipoMantenimiento tipoMantenimiento);

        @Query("SELECT m FROM Mantenimiento m WHERE m.tipo = 'Recurrente' AND m.vehiculo.id_vehiculo IN :vehiculoIds AND m.fechaInicio <= :fechaFin")
        List<Mantenimiento> findMantenimientosRecurrentesPorVehiculos(@Param("fechaFin") LocalDateTime fechaFin, @Param("vehiculoIds") List<Long> vehiculoIds);

        @Query("SELECT m FROM Mantenimiento m WHERE m.tipo = 'Preventivo' AND m.vehiculo.id_vehiculo IN :vehiculoIds AND m.fechaInicio <= :fechaFin")
        List<Mantenimiento> findMantenimientosPreventivosPorVehiculos(@Param("fechaFin") LocalDateTime fechaFin, @Param("vehiculoIds") List<Long> vehiculoIds);

    @Query("SELECT m FROM Mantenimiento m WHERE :fechaActual BETWEEN m.fechaInicio AND m.fechaFin AND m.vehiculo.id_vehiculo = :idVehiculo AND m.tipo = :tipoMantenimiento ORDER BY m.fechaFin desc ")
    List<Mantenimiento> findMantenimientoByFechaAndVehiculoAndMantenimientoPreventivo(@Param("fechaActual") LocalDateTime fechaActual,
                                                                                          @Param("idVehiculo") Long idVehiculo,
                                                                                          @Param("tipoMantenimiento") TipoMantenimiento tipoMantenimiento);


    }


