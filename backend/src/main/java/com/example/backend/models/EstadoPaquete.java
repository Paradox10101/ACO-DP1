
package com.example.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


public enum EstadoPaquete {
    EnAlmacen,
    EnTransito,
    EnOficina,
    Entregado
}
