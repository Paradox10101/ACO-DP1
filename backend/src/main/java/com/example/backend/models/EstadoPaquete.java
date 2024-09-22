
package com.example.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public enum EstadoPaquete {
    @Enumerated(EnumType.STRING)
    EnAlmacen,
    
    @Enumerated(EnumType.STRING)
    EnTransito,
    
    @Enumerated(EnumType.STRING)
    EnOficina,
    
    @Enumerated(EnumType.STRING)
    Entregado
}
