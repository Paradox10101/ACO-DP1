
package com.example.backend.models;

import java.time.LocalDateTime;

public class Pedido {
    private int id_pedido;
    private int fid_almacen;
    private int fid_oficinaDest;
    private LocalDateTime fechaEntregaReal;
    private LocalDateTime fechaEntregaEstimada;
    private EstadoPedido estado;
    private int cantidadPaquetes;
    private String codigoSeguridad;

}
