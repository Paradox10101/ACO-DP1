
package com.example.backend.models;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;

@Entity
@Table(name="EstadoSimulacion")
public enum EstadoSimulacion {
    
    @Enumerated(EnumType.STRING)
    Comienzo,
    @Enumerated(EnumType.STRING)
    Terminado,
    @Enumerated(EnumType.STRING)
    Pausado
}
