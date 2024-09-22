/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.backend.models;

import java.sql.Time;
import java.time.LocalDateTime;

public class Mantenimiento {
    private int id_mantenimiento;
    private TipoMantenimiento tipo;
    private LocalDateTime fechaProgramada;
    private Time duracion;
}
