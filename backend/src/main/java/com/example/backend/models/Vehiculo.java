
package com.example.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import java.util.*;
@Entity
@Table(name = "Vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long id_vehiculo;

    @Column(name = "codigo")
    private String codigo;


    @ManyToOne
    @JoinColumn(name = "fid_almacen")
    private Almacen almacen;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacion")
    private Ubicacion ubicacionActual;

    @ManyToOne
    @JoinColumn(name = "fid_tipoVehiculo", nullable=false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "fecha_llegada")
    private LocalDateTime fechaLlegada;

    @Column(name = "capacidad_maxima", nullable=false)
    private int capacidadMaxima;

    @Enumerated(EnumType.STRING)
    private EstadoVehiculo estado;
    

    @Column(name = "distancia_total")
    private float distanciaTotal;

    @Column(name = "capacidad_utilizada")
    private int capacidadUtilizada;

    @OneToMany(mappedBy = "vehiculo")
    private List<Averia> averias; // Lista para almacenar las averías del vehículo

    @Column(name = "disponible")
    private boolean disponible;

    public Vehiculo() {
        this.disponible = true;
        this.averias = new ArrayList<>();
    }

    public Vehiculo(Long id_vehiculo, PlanTransporte planTransporte, Almacen almacen, LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada, int capacidadMaxima, EstadoVehiculo estado, float distanciaTotal) {
        this.id_vehiculo = id_vehiculo;
        this.almacen = almacen;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.capacidadMaxima = capacidadMaxima;
        this.estado = estado;
        this.distanciaTotal = distanciaTotal;
    }

    public Vehiculo(String codigo, Almacen almacen, Ubicacion ubicacionActual, TipoVehiculo tipoVehiculo, int capacidadMaxima, EstadoVehiculo estado, float distanciaTotal) {
        this.codigo = codigo;
        this.almacen = almacen;
        this.ubicacionActual = ubicacionActual;
        this.tipoVehiculo = tipoVehiculo;
        this.capacidadMaxima = capacidadMaxima;
        this.estado = estado;
        this.distanciaTotal = distanciaTotal;
    }

    public Vehiculo(Long id_vehiculo, String codigo, PlanTransporte planTransporte, Almacen almacen, Ubicacion ubicacionActual, TipoVehiculo tipoVehiculo, LocalDateTime fechaSalida, LocalDateTime fechaLlegada, int capacidadMaxima, EstadoVehiculo estado, float distanciaTotal) {
        this.id_vehiculo = id_vehiculo;
        this.codigo = codigo;

        this.almacen = almacen;
        this.ubicacionActual = ubicacionActual;
        this.tipoVehiculo = tipoVehiculo;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.capacidadMaxima = capacidadMaxima;
        this.estado = estado;
        this.distanciaTotal = distanciaTotal;
    }

    public Vehiculo(String codigo, Almacen almacen, Ubicacion ubicacionActual, TipoVehiculo tipoVehiculo, LocalDateTime fechaSalida, LocalDateTime fechaLlegada, int capacidadMaxima, EstadoVehiculo estado, float distanciaTotal, int capacidadUtilizada) {
        this.codigo = codigo;
        this.almacen = almacen;
        this.ubicacionActual = ubicacionActual;
        this.tipoVehiculo = tipoVehiculo;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.capacidadMaxima = capacidadMaxima;
        this.estado = estado;
        this.distanciaTotal = distanciaTotal;
        this.capacidadUtilizada = capacidadUtilizada;
    }

    public Long getId_vehiculo() {
        return id_vehiculo;
    }

    public void setId_vehiculo(Long id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }


    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public LocalDateTime getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(LocalDateTime fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public float getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(float distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }


    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public Ubicacion getUbicacionActual() {
        return ubicacionActual;
    }

    public void setUbicacionActual(Ubicacion ubicacionActual) {
        this.ubicacionActual = ubicacionActual;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCapacidadUtilizada() {
        return capacidadUtilizada;
    }

    public void setCapacidadUtilizada(int capacidadUtilizada) {
        this.capacidadUtilizada = capacidadUtilizada;
    }

    // Método para agregar una avería al vehículo
    public void registrarAveria(TipoAveria tipoAveria, LocalDateTime fechaInicio) {
        //Averia nuevaAveria = new Averia(tipoAveria, fechaInicio, fechaInicio.plusDays(2), this);
        LocalDateTime fechaFin;

        switch (tipoAveria) {
            case T1:
                // Avería tipo 1: Retardo de unas horas
                fechaFin = fechaInicio.plusHours(4); // Asume un retardo de 4 horas
                break;
            case T2:
                // Avería tipo 2: Inoperativo durante un turno (8 horas)
                fechaFin = fechaInicio.plusHours(8);
                this.disponible = false; // El vehículo no estará disponible
                break;
            case T3:
                // Avería tipo 3: Inoperativo durante varios días (2 o 3 días)
                fechaFin = fechaInicio.plusDays(2); // Ajustar según el tipo de daño
                this.disponible = false; // El vehículo no estará disponible
                break;
            default:
                fechaFin = fechaInicio;
        }

        Averia nuevaAveria = new Averia(tipoAveria, fechaInicio, fechaFin, this);
        this.averias.add(nuevaAveria);
    }

    // Método para verificar si el vehículo está disponible
    public boolean verificarDisponibilidad(LocalDateTime fechaActual) {
        for (Averia averia : averias) {
            if (fechaActual.isAfter(averia.getFechaInicio()) && fechaActual.isBefore(averia.getFechaFin())) {
                return false;
            }
        }

        // Si no tiene averías activas, se marca como disponible
        this.disponible = true;
        return this.disponible;
    }    

    public boolean isDisponible() {
        return disponible;
    }

    public List<Averia> getAverias() {
        return averias;
    }
}
