package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
@Entity //estas cosas siempre se debe colocar
public class Cliente {
    @Id
    @GeneratedValue
    private int id_cliente;
    
    @Column(nullable = false)
    private String codigo;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String email;
}
