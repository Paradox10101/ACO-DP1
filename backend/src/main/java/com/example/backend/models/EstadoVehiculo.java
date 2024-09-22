
package com.example.backend.models;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;

@Entity
@Table(name="EstadoVehiculo")
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
