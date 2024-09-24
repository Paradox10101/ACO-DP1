
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.algorithm.Aco;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


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

    @Autowired
    private Aco aco;

    public PlanTransporte() {

    }

    //Algo implementado en el service <----
    public PlanTransporte crearRuta(Pedido pedido){
        //List<Oficina> aeropuertos = aeropuertoCache.getAeropuertos();  obtener oficinas
        //List<Tramo> tramos = new ArrayList<>();
        List<Oficina> oficinas = new ArrayList<>();
        List<Tramo> tramos = new ArrayList<>();
        List<Tramo> rutaOptima = new ArrayList<>();
        PlanTransporte planOptimo =  aco.ejecutar(oficinas, tramos, pedido, 0);

        EstadoPedido estado;

        if(rutaOptima != null){
            pedido.setEstado(estado.Registrado);
            planOptimo.setId_pedido(pedido.getId_pedido());

            List<Tramo> tramosRuta = planOptimo.getTramos();
            actualizarCambiosEnvio(vuelosRuta, envio);

            //planViajeRepository.save(rutaOptima);
            //rutas.add(rutaOptima);
        }

        return planOptimo;
    }

    public void actualizarCambiosEnvio(List<Tramo>tramosRuta, Pedido pedido){
        for (int i=0; i<tramosRuta.size(); i++){
            Tramo tramo = tramosRuta.get(i);

        }
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
