package com.example.backend.algorithm;

import java.time.LocalTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.models.Bloqueo;
import com.example.backend.models.Pedido;
import com.example.backend.models.Ruta;
import com.example.backend.models.Vehiculo;



@Service
public class Aco {
    private List<Pedido> pedidos;
    //private List<Pedido> originalPedidos;
    //private List<Pedido> pedidosNuevos;
    private List<Vehiculo> vehiculos;

    private List<Ruta> rutas;
    private Pedido pedido;
    private Map<Long, List<Ruta>> grafoRutas;// Cambiar -> Vuelo
    private Map<Long, Double> feromonas;
    private double tasaEvaporacion = 0.02;
    private double feromonaInicial = 0.1;
    private double alpha = 1.0;
    private double beta = 1.5;
    private int numeroHormigas = 55;
    private int numeroIteraciones = 55;
    private Random random = new Random();

    /*private double q0 = 0.3;
    private double Q = 1;
    private int cantidadPdidosInicial = 0;
    private boolean inicio;
    private List<Bloqueo> bloqueosActivos;
    private double MIN_DISTANCE = 0.00000000009;*/

    public Aco() {
        
    }

    private void inicializarGrafo() {
        grafoRutas = new HashMap<>();
        for (Pedido pedido : pedidos) {
            List<Ruta> rutas = new ArrayList<>();
            for (Pedido pedido2 : pedidos) {
                if (pedido.getId() != pedido2.getId()) {
                    rutas.add(new Ruta(pedido, pedido2));
                }
            }
            grafoRutas.put(pedido.getId(), rutas);
        }
    }

    
}
