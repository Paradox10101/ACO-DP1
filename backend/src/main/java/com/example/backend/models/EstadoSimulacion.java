
package com.example.backend.models;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;

public enum EstadoSimulacion {
    Comienzo,
    Terminado,
    Pausado
}
