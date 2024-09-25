
package com.example.backend.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(name = "codigo")
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "fid_plan_transporte")
    private PlanTransporte planTransporte;

    @OneToOne
    @JoinColumn(name = "fid_almacen")
    private Almacen almacen;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacion")
    private Ubicacion ubicacionActual;

    @OneToOne
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

    public Vehiculo(Long id_vehiculo, PlanTransporte planTransporte, Almacen almacen, LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada, int capacidadMaxima, EstadoVehiculo estado, float distanciaTotal) {
        this.id_vehiculo = id_vehiculo;
        this.planTransporte = planTransporte;
        this.almacen = almacen;
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

    public PlanTransporte getPlanTransporte() {
        return planTransporte;
    }

    public void setPlanTransporte(PlanTransporte planTransporte) {
        this.planTransporte = planTransporte;
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
                        almacen.setUbicacion(ubicacion);
                        linea = lector.readLine();
                        String[] codigosVehiculos = linea.split(",");
                        almacen.setCantidadVehiculos(codigosVehiculos.length);
                        almacenes.add(almacen);
                        for(String codigoVehiculo : codigosVehiculos){
                            Vehiculo vehiculo = new Vehiculo();
                            String codigoCorregido = codigoVehiculo.trim();

                            for(TipoVehiculo tipoVehiculo : tiposVehiculo){
                                if(tipoVehiculo.getNombre().equals(String.valueOf(codigoCorregido.charAt(0)))){
                                    vehiculo.setTipoVehiculo(tipoVehiculo);
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

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public Ubicacion getUbicacionActual() {
        return ubicacionActual;
    }

    public void setFid_ubicacionActual(Ubicacion ubicacionActual) {
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
}
