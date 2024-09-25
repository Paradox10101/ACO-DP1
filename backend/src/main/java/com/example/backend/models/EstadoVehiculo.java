
package com.example.backend.models;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;

public enum EstadoVehiculo {
    Disponible,
    EnRuta,
    EnMantenimiento,
    Averiado
}
