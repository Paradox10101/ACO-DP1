
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.algorithm.Aco;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "PlanTransporte")
public class PlanTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planTransporte")
    private Long id_planTransporte;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Long fid_pedido;

    @OneToMany(mappedBy = "planTransporte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehiculo> vehiculos;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActalizacion;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Long fid_origen;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Long fid_destino;

    @OneToMany(mappedBy = "planTransporte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tramo> tramos;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @Autowired
    private Aco aco = new Aco();

    public PlanTransporte() {

    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    //Algo implementado en el service <----
    public PlanTransporte crearRuta(Pedido pedido, List<Almacen> almacenes, List<Oficina> oficinas, List<Tramo> tramos, 
            List<Region> regiones){
        //List<Oficina> aeropuertos = aeropuertoCache.getAeropuertos();  obtener oficinas
        //List<Tramo> tramos = new ArrayList<>();

        //List<Almacen> almacenes = new ArrayList<>();

        //List<Oficina> oficinas = new ArrayList<>();
        //List<Tramo> tramos = new ArrayList<>();
        //List<Tramo> rutas = new ArrayList<>();
        PlanTransporte planOptimo =  aco.ejecutar(oficinas, tramos, pedido, 0, regiones);

        if(planOptimo != null){
            pedido.setEstado(EstadoPedido.Registrado);
            planOptimo.setFid_pedido(pedido.getId_pedido());
            planOptimo.setEstado(EstadoPedido.Registrado);
            
            List<Tramo> tramosRuta = planOptimo.getTramos();
            actualizarCambiosEnvio(tramosRuta, pedido, oficinas);
            return planOptimo; // Retorna el plan de transporte encontrado
            //planViajeRepository.save(rutaOptima);
            //rutas.add(rutaOptima);
        }else{
            PlanTransporte rutaInvalida = new PlanTransporte(); // Crear una nueva instancia de PlanViaje
            rutaInvalida.setFid_pedido(pedido.getId_pedido()); // Asignar el envío a la nueva instancia
            //rutas.add(rutaInvalida);
            System.out.println("No se encontró una ruta válida para el envío: " + pedido.getId_pedido());
            return null;
        }

        //return planOptimo;
    }

    public void actualizarCambiosEnvio(List<Tramo> tramosRuta, Pedido pedido,List<Oficina> oficinas) {
        // Obtener la oficina de destino
        Oficina oficinaDestino = buscarOficinaPorId(pedido.getFid_oficinaDest(), oficinas);

        for (int i = 0; i < tramosRuta.size(); i++) {
            Tramo tramo = tramosRuta.get(i);

            // Verificar si estamos en el primer tramo
            if (i == 0) {
                // Primer tramo, origen es siempre un almacén
                System.out.println(
                        "Almacén de origen: " + pedido.getFid_almacen() + " hasta " + tramo.getFid_ubicacion_destino());
            } else {
                // Los tramos siguientes son entre oficinas
                Tramo tramoAnterior = tramosRuta.get(i - 1);
                System.out.println(
                        "De: " + tramoAnterior.getFid_ubicacion_destino() + " a " + tramo.getFid_ubicacion_destino());
            }
        }

        // Al llegar al último tramo, verificar si se entrega correctamente a la oficina
        // de destino
        Tramo ultimoTramo = tramosRuta.get(tramosRuta.size() - 1);
        if (ultimoTramo.getFid_ubicacion_destino().equals(oficinaDestino.getFid_ubicacion())) {
            System.out.println("Pedido entregado en la oficina destino " + oficinaDestino.getId_oficina());
        } else {
            System.out.println("Error: La entrega no coincide con la oficina destino esperada.");
        }
    }

    private Oficina buscarOficinaPorId(Long idOficina, List<Oficina> oficinas) {
        for (Oficina oficina : oficinas) {
            if (oficina.getId_oficina().equals(idOficina)) {
                return oficina;
            }
        }
        return null; // Devuelve null si no encuentra la oficina
    }


    public PlanTransporte(Long id_planTransporte, Long fid_pedido, ArrayList<Vehiculo> vehiculos, LocalDateTime fechaCreacion,
            LocalDateTime fechaActalizacion, Long fid_origen, Long fid_destino, ArrayList<Tramo> tramos) {
        this.id_planTransporte = id_planTransporte;
        this.fid_pedido = fid_pedido;
        this.vehiculos = vehiculos;
        this.fechaCreacion = fechaCreacion;
        this.fechaActalizacion = fechaActalizacion;
        this.fid_origen = fid_origen;
        this.fid_destino = fid_destino;
        this.tramos = tramos;
    }

    public Long getId_planTransporte() {
        return id_planTransporte;
    }

    public void setId_planTransporte(Long id_planTransporte) {
        this.id_planTransporte = id_planTransporte;
    }

    public Long getFid_pedido() {
        return fid_pedido;
    }

    public void setFid_pedido(Long fid_pedido) {
        this.fid_pedido = fid_pedido;
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(ArrayList<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActalizacion() {
        return fechaActalizacion;
    }

    public void setFechaActalizacion(LocalDateTime fechaActalizacion) {
        this.fechaActalizacion = fechaActalizacion;
    }

    public Long getFid_origen() {
        return fid_origen;
    }

    public void setFid_origen(Long fid_origen) {
        this.fid_origen = fid_origen;
    }

    public Long getFid_destino() {
        return fid_destino;
    }

    public void setFid_destino(Long fid_destino) {
        this.fid_destino = fid_destino;
    }

    public List<Tramo> getTramos() {
        return tramos;
    }

    public void setTramos(List<Tramo> tramos) {
        this.tramos = tramos;
    }

}
