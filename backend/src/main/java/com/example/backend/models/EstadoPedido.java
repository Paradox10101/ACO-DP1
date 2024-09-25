
package com.example.backend.models;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;

public enum EstadoPedido {
    Registrado,
    EnTransito,
    EnOficina,
    Entregado
}
