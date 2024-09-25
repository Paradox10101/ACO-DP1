
package com.example.backend.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.*;

@Entity
@Table(name = "Vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long id_vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_plan_transporte")
    private Long fid_plan_transporte;

    @OneToOne
    @JoinColumn(name = "fid_almacen")
    private Long fid_Almacen;

    @ManyToOne
    @JoinColumn(name = "id_almacen")
    private Long fid_ubicacionActual;

    @ManyToOne
    @JoinColumn(name = "fid_tipoVehiculo")
    private Long fid_tipoVehiculo;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "fecha_llegada")
    private LocalDateTime fechaLlegada;

    @Column(name = "capacidad_maxima")
    private int capacidadMaxima;

    @Column(name = "codigo")
    private String codigo;

    //@Column(name = "estado")
    @Transient
    private EstadoVehiculo estado;

    @Transient
    private ArrayList<Mantenimiento> mantenimientos;

    @Transient
    private ArrayList<Averia> averias;

    @Column(name = "distancia_total")
    private float distanciaTotal;

    public Vehiculo(Long id_vehiculo, Long fid_plan_transporte, Long fidAlmacen, LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada, int capacidadMaxima, EstadoVehiculo estado, float distanciaTotal) {
        this.id_vehiculo = id_vehiculo;
        this.fid_plan_transporte = fid_plan_transporte;
        this.fid_Almacen = fidAlmacen;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.capacidadMaxima = capacidadMaxima;
        this.estado = estado;
        this.distanciaTotal = distanciaTotal;
    }

    public Vehiculo() {

    }

    public Long getId_vehiculo() {
        return id_vehiculo;
    }

    public void setId_vehiculo(Long id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public Long getFid_plan_transporte() {
        return fid_plan_transporte;
    }

    public void setFid_plan_transporte(Long id_plan_transporte) {
        this.fid_plan_transporte = id_plan_transporte;
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

    public ArrayList<Mantenimiento> getMantenimientos() {
        return mantenimientos;
    }

    public void setMantenimientos(ArrayList<Mantenimiento> mantenimientos) {
        this.mantenimientos = mantenimientos;
    }

    public ArrayList<Averia> getAverias() {
        return averias;
    }

    public void setAverias(ArrayList<Averia> averias) {
        this.averias = averias;
    }

    public float getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(float distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }

    public static ArrayList<Vehiculo> cargarVehiculosAlmacenesDesdeArchivo(String rutaArchivo,  List<Almacen> almacenes, ArrayList<Vehiculo> vehiculos, List<Oficina> oficinas, List<Ubicacion> ubicaciones, ArrayList<TipoVehiculo> tiposVehiculo) {
        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                Ubicacion ubicacion = new Ubicacion();

                for(int i=0; i<3 ;i++){
                    linea = lector.readLine();
                    TipoVehiculo tipoVehiculo = new TipoVehiculo();
                    String[] valores = linea.split(" ");
                    String categoria = valores[0].trim();
                    int capacidad = Integer.parseInt(valores[1].trim());
                    tipoVehiculo.setNombre(categoria);
                    tipoVehiculo.setCapacidadMaxima(capacidad);
                    tiposVehiculo.add(tipoVehiculo);
                }
                for(int i=0; i<3 ;i++){
                    Almacen almacen = new Almacen();
                    linea = lector.readLine();
                    String provinciaSel = linea.trim().toUpperCase();
                    Optional<Ubicacion> ubicacionSeleccionada = ubicaciones.stream().filter(ubicacionS -> ubicacionS.getProvincia().equals(provinciaSel)).findFirst();
                    if(ubicacionSeleccionada.isPresent()){
                        Long id_ubicacion = ubicacionSeleccionada.get().getId_ubicacion();
                        almacen.setFid_ubicacion(id_ubicacion);
                        linea = lector.readLine();
                        String[] codigosVehiculos = linea.split(",");
                        almacen.setCantidadVehiculos(codigosVehiculos.length);
                        almacenes.add(almacen);
                        for(String codigoVehiculo : codigosVehiculos){
                            Vehiculo vehiculo = new Vehiculo();
                            String codigoCorregido = codigoVehiculo.trim();

                            for(TipoVehiculo tipoVehiculo : tiposVehiculo){
                                if(tipoVehiculo.getNombre().equals(String.valueOf(codigoCorregido.charAt(0)))){
                                    vehiculo.setFid_tipoVehiculo(tipoVehiculo.getId_tipoVehiculo());
                                    vehiculo.setDistanciaTotal(0);
                                    vehiculo.setCodigo(codigoCorregido);
                                    vehiculos.add(vehiculo);
                                    break;
                                }
                            }

                        }

                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return vehiculos;
    }

    public Long getFid_Almacen() {
        return fid_Almacen;
    }

    public void setFid_Almacen(Long fid_Almacen) {
        this.fid_Almacen = fid_Almacen;
    }

    public Long getFid_ubicacionActual() {
        return fid_ubicacionActual;
    }

    public void setFid_ubicacionActual(Long fid_ubicacionActual) {
        this.fid_ubicacionActual = fid_ubicacionActual;
    }

    public Long getFid_tipoVehiculo() {
        return fid_tipoVehiculo;
    }

    public void setFid_tipoVehiculo(Long fid_tipoVehiculo) {
        this.fid_tipoVehiculo = fid_tipoVehiculo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
