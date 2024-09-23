
package com.example.backend.models;

import java.time.LocalDateTime;

public class Pedido {
    private Long id_pedido;
    private Long fid_almacen;
    private Long fid_oficinaDest;
    private LocalDateTime fechaEntregaReal;
    private LocalDateTime fechaEntregaEstimada;
    private EstadoPedido estado;
    private int cantidadPaquetes;
    private String codigoSeguridad;

}
