/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.backend.models;

import java.time.LocalDateTime;

public class Bloqueo {
    private int id_bloqueo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Tramo tramoAfectado;
}
