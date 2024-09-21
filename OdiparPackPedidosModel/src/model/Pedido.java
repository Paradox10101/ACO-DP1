/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author Yahyr
 */
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
