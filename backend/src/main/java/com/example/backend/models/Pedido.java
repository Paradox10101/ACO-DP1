
package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Pedido {
    private int id_pedido;
    private int fid_almacen;
    private int fid_oficinaDest;
    private LocalDateTime fechaEntregaReal;
    private LocalDateTime fechaEntregaEstimada;
    private EstadoPedido estado;
    private int cantidadPaquetes;
    private String codigoSeguridad;

    public Pedido(int id_pedido, int fid_almacen, int fid_oficinaDest, LocalDateTime fechaEntregaReal,
            LocalDateTime fechaEntregaEstimada, EstadoPedido estado, int cantidadPaquetes, String codigoSeguridad) {
        this.id_pedido = id_pedido;
        this.fid_almacen = fid_almacen;
        this.fid_oficinaDest = fid_oficinaDest;
        this.fechaEntregaReal = fechaEntregaReal;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
        this.estado = estado;
        this.cantidadPaquetes = cantidadPaquetes;
        this.codigoSeguridad = codigoSeguridad;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public int getFid_almacen() {
        return fid_almacen;
    }

    public void setFid_almacen(int fid_almacen) {
        this.fid_almacen = fid_almacen;
    }

    public int getFid_oficinaDest() {
        return fid_oficinaDest;
    }

    public void setFid_oficinaDest(int fid_oficinaDest) {
        this.fid_oficinaDest = fid_oficinaDest;
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

    public static ArrayList<Pedido> cargarPedidosDesdeArchivo(String rutaArchivo) {
        ArrayList<Pedido> pedidos = new ArrayList<>();

        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
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
                    String ubigeoOrigen = (tramoString.split("=>"))[0].trim();
                    String ubigeoDestino = (tramoString.split("=>"))[1].trim();

                    int cantidadPaquetes = Integer.parseInt(valores[2].trim());

                    String codigoCliente = valores[3].trim();

                    System.out.println(codigoCliente);
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return pedidos;
    }
}
