package com.example.backend.Repository;

import com.example.backend.models.Bloqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BloqueoRepository extends JpaRepository<Bloqueo, Long> {
    @Query("SELECT b FROM Bloqueo b " +
            "WHERE (b.fechaInicio <= :fechaFin AND b.fechaFin >= :fechaInicio) " +
            "OR (:fechaInicio <= b.fechaInicio AND :fechaFin >= b.fechaFin)")
    List<Bloqueo> encontrarBloqueosEntreFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
