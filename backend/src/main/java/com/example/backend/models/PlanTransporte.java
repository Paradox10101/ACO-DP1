
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.algorithm.Aco;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


@Entity
@Table(name = "PlanTransporte")
public class PlanTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planTransporte")
    private Long id_planTransporte;

    @ManyToOne
    @JoinColumn(name = "fid_pedido")
    private Pedido pedido;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActalizacion;

    @OneToOne
    @JoinColumn(name = "fid_ubicacion_origen")
    private Ubicacion ubicacionOrigen;

    @OneToOne
    @JoinColumn(name = "fid_ubicacion_destino")
    private Ubicacion ubicacionDestino;


    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @Transient
    private Aco aco = new Aco();

    public PlanTransporte() {

    }

    public PlanTransporte(Long id_planTransporte, Pedido pedido, LocalDateTime fechaCreacion,
            LocalDateTime fechaActalizacion, Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino) {
        this.id_planTransporte = id_planTransporte;
        this.pedido = pedido;
        this.fechaCreacion = fechaCreacion;
        this.fechaActalizacion = fechaActalizacion;
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
    }

    public Long getId_planTransporte() {
        return id_planTransporte;
    }

    public void setId_planTransporte(Long id_planTransporte) {
        this.id_planTransporte = id_planTransporte;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
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
            planOptimo.setPedido(pedido);
            planOptimo.setEstado(EstadoPedido.Registrado);
            
            //Falta hallar tramos por plan de transporte
            //List<Tramo> tramosRuta = planOptimo.getTra();
            //actualizarCambiosEnvio(tramosRuta, pedido, oficinas);
            return planOptimo; // Retorna el plan de transporte encontrado
            //planViajeRepository.save(rutaOptima);
            //rutas.add(rutaOptima);
        }else{
            PlanTransporte rutaInvalida = new PlanTransporte(); // Crear una nueva instancia de PlanViaje
            rutaInvalida.setPedido(pedido); // Asignar el envío a la nueva instancia
            //rutas.add(rutaInvalida);
            System.out.println("No se encontró una ruta válida para el envío: " + pedido.getId_pedido());
            return null;
        }

        //return planOptimo;
    }

    public void actualizarCambiosEnvio(List<Tramo> tramosRuta, Pedido pedido,List<Oficina> oficinas) {
        // Obtener la oficina de destino
        Oficina oficinaDestino = buscarOficinaPorId(pedido.getOficinaDestino().getId_oficina(), oficinas);

        for (int i = 0; i < tramosRuta.size(); i++) {
            Tramo tramo = tramosRuta.get(i);

            // Verificar si estamos en el primer tramo
            if (i == 0) {
                // Primer tramo, origen es siempre un almacén
                System.out.println(
                        "Almacén de origen: " + pedido.getAlmacen().getId_almacen() + " hasta " + tramo.getubicacionDestino().getIdUbicacion());
            } else {
                // Los tramos siguientes son entre oficinas
                Tramo tramoAnterior = tramosRuta.get(i - 1);
                System.out.println(
                        "De: " + tramoAnterior.getubicacionOrigen().getIdUbicacion() + " a " + tramo.getubicacionDestino().getIdUbicacion());
            }
        }

        // Al llegar al último tramo, verificar si se entrega correctamente a la oficina
        // de destino
        Tramo ultimoTramo = tramosRuta.get(tramosRuta.size() - 1);
        if (ultimoTramo.getubicacionOrigen().getIdUbicacion().equals(oficinaDestino.getUbicacion().getIdUbicacion())) {
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



}
