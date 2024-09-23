
package com.example.backend.models;

import java.time.LocalDateTime;

public class Pedido {
    private long id_pedido;
    private long fid_almacen;
    private long fid_oficinaDest;
    private LocalDateTime fechaEntregaReal;
    private LocalDateTime fechaEntregaEstimada;
    private EstadoPedido estado;
    private int cantidadPaquetes;
    private String codigoSeguridad;

}
