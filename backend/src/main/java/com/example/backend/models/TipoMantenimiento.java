
package com.example.backend.models;
import jakarta.persistence.Table;

@Table(name="TipoMantenimiento")
public enum TipoMantenimiento {
    Preventivo, Correctivo
}
