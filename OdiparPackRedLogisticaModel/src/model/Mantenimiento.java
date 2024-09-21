/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Time;
import java.time.LocalDateTime;

/**
 *
 * @author Yahyr
 */
public class Mantenimiento {
    private int id_mantenimiento;
    private TipoMantenimiento tipo;
    private LocalDateTime fechaProgramada;
    private Time duracion;
}
