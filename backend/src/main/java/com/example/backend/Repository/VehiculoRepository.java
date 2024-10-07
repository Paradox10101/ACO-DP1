package com.example.backend.Repository;

import com.example.backend.models.EstadoVehiculo;
import com.example.backend.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    @Query("SELECT v FROM Vehiculo v WHERE v.estado = :estadoDisponible AND v.capacidadUtilizada < v.tipoVehiculo.capacidadMaxima")
    List<Vehiculo> findVehiculosDisponiblesConCapacidadMenor(@Param("estadoDisponible") EstadoVehiculo estadoDisponible);

    @Query("SELECT v FROM Vehiculo v WHERE v.estado = :estado " +
            "AND v.capacidadUtilizada > 0 " +
            "AND (v.capacidadMaxima - v.capacidadUtilizada) > :cantidadSolicitada")
    List<Vehiculo> findVehiculoDisponibleConCapacidadParcialOcupada(
            @Param("cantidadSolicitada") int cantidadSolicitada,
            @Param("estado") EstadoVehiculo estado);
}
