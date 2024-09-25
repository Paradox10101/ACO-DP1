
package com.example.backend.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

@Entity
@Table(name = "Pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id_pedido;

    @ManyToOne
    @JoinColumn(name = "fid_almacen")
    private Almacen almacen;

    @OneToOne
    @JoinColumn(name = "fid_oficina_destino")
    private Oficina oficinaDestino;

    @OneToOne
    @JoinColumn(name = "fid_cliente")
    private Cliente cliente;

    @Column(name = "fechaEntregaReal", columnDefinition = "DATETIME")
    private LocalDateTime fechaEntregaReal;

    @Column(name = "fechaEntregaEstimada", columnDefinition = "DATETIME")
    private LocalDateTime fechaEntregaEstimada;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @Column(name = "cantidadPaquetes")
    private int cantidadPaquetes;

    @Column(name = "codigoSeguridad")
    private String codigoSeguridad;

    @Column(name = "fechaRegistro")
    private LocalDateTime fechaRegistro;
    

    public Pedido() {
        
    }


    public Pedido(Long id_pedido, Almacen almacen, Oficina oficinaDestino, LocalDateTime fechaEntregaReal,
            LocalDateTime fechaEntregaEstimada, EstadoPedido estado, int cantidadPaquetes, String codigoSeguridad) {
        this.almacen = almacen;
        this.oficinaDestino = oficinaDestino;
        this.fechaEntregaReal = fechaEntregaReal;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
        this.estado = estado;
        this.cantidadPaquetes = cantidadPaquetes;
        this.codigoSeguridad = codigoSeguridad;
    }

    public Long getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(Long id_pedido) {
        this.id_pedido = id_pedido;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public Oficina getOficinaDestino() {
        return oficinaDestino;
    }

    public void setOficinaDestino(Oficina oficinaDestino) {
        this.oficinaDestino = oficinaDestino;
    }

    public LocalDateTime getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(LocalDateTime fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public LocalDateTime getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(LocalDateTime fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public int getCantidadPaquetes() {
        return cantidadPaquetes;
    }

    public void setCantidadPaquetes(int cantidadPaquetes) {
        this.cantidadPaquetes = cantidadPaquetes;
    }

    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
        
    }

    

    public static ArrayList<Pedido> cargarPedidosDesdeArchivo(String rutaArchivo, List<Oficina> oficinas, List<Ubicacion> ubicaciones) { // esto va ir en otra parte <---
        ArrayList<Pedido> pedidos = new ArrayList<>();

        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    Pedido pedido = new Pedido();
                    Cliente cliente = new Cliente();

                    //Debe consultarse la lista de clientes previamente
                    String[] valores = linea.split(",");
                    String anhoMesString = (rutaArchivo.substring(rutaArchivo.length() - 10)).split(".txt")[0];
                    String anhoString = anhoMesString.substring(0, 4);
                    String mesString = anhoMesString.substring(anhoMesString.length() - 2);
                    String diaString = (valores[0].split(" "))[0];
                    String horaMinutoString = (valores[0].split(" "))[1];
                    String fechaHoraString = diaString + "/" + mesString + "/" + anhoString + " " + horaMinutoString;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraString, formatter);

                    String tramoString = valores[1].trim();
                    //String ubigeoOrigen = (tramoString.split("=>"))[0].trim();
                    String ubigeoDestino = (tramoString.split("=>"))[1].trim();
                    int cantidadPaquetes = Integer.parseInt(valores[2].trim());
                    String codigoCliente = valores[3].trim();
                    
                    Optional<Ubicacion> ubicacionDestinoSeleccionada = ubicaciones.stream().filter(
                            ubicacionS -> ubicacionS.getUbigeo().equals(ubigeoDestino)).findFirst();
                    if(ubicacionDestinoSeleccionada.isPresent()){
                        Optional<Oficina> oficinaSeleccionada = oficinas.stream().filter(
                        oficinaS -> oficinaS.getUbicacion().getIdUbicacion() == ubicacionDestinoSeleccionada.get().getIdUbicacion()).findFirst();
                        if(oficinaSeleccionada.isPresent()){
                            pedido.setOficinaDestino(oficinaSeleccionada.get());
                            pedido.setFechaRegistro(fechaHora);
                            pedido.setFechaEntregaEstimada(LocalDateTime.now());//solo para prueba
                            pedido.setCantidadPaquetes(cantidadPaquetes);
                            cliente.setCodigo(codigoCliente);
                            pedidos.add(pedido);
                        }

                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return pedidos;
    }
}
