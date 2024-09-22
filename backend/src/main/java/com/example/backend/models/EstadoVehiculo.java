
package com.example.backend.models;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;

@Entity
public enum EstadoVehiculo {
    @Enumerated(EnumType.STRING)
    Disponible,
    
    @Enumerated(EnumType.STRING)
    EnRuta,
    
    @Enumerated(EnumType.STRING)
    EnMantenimiento,
    
    @Enumerated(EnumType.STRING)
    Averiado
}
