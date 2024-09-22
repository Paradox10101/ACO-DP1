
package com.example.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


@Entity
@Table(name="EstadoPaquete")
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
