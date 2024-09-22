
package com.example.backend.models;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;

@Entity
@Table(name="EstadoPedido")
public enum EstadoPedido {
    
    @Enumerated(EnumType.STRING)
    Registrado,
    
    @Enumerated(EnumType.STRING)
    EnTransito,
    
    @Enumerated(EnumType.STRING)
    EnOficina,
    
    @Enumerated(EnumType.STRING)
    Entregado
}
