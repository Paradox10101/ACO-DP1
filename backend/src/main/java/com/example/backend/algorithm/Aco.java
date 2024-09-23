package com.example.backend.algorithm;

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

import org.springframework.stereotype.Service;

import com.example.backend.models.Oficina;
import com.example.backend.models.Pedido;
import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Tramo;
import com.example.backend.models.Vehiculo;

@Service
public class Aco {
    private List<Pedido> pedidos;
    // private List<Pedido> originalPedidos;
    // private List<Pedido> pedidosNuevos;
    private List<Vehiculo> vehiculos;

    private List<Tramo> tramos;
    private Pedido pedido;
    private Map<Long, List<Tramo>> grafoTramos;
    private Map<Long, Double> feromonas;
    private double tasaEvaporacion = 0.02;
    private double feromonaInicial = 0.1;
    private double alpha = 1.0;
    private double beta = 1.5;
    private int numeroHormigas = 55;
    private int numeroIteraciones = 55;
    private Random random = new Random();

    /*
     * private double q0 = 0.3;
     * private double Q = 1;
     * private int cantidadPdidosInicial = 0;
     * private boolean inicio;
     * private List<Bloqueo> bloqueosActivos;
     * private double MIN_DISTANCE = 0.00000000009;
     */

    public Aco() {

    }

    private void inicializarGrafoTramos(LocalDateTime fechaMaximaEntregaGMT) {
        grafoTramos = new HashMap<>();
        for (Tramo tramo : tramos) {
            // Verifica que el tramo esté disponible antes de la fecha máxima de entrega
            if (tramo.getFechaFin().isBefore(fechaMaximaEntregaGMT)
                    || tramo.getFechaFin().isEqual(fechaMaximaEntregaGMT)) {
                grafoTramos.computeIfAbsent(tramo.getOrigen(), k -> new ArrayList<>()).add(tramo);
            }
        }
    }

    private void inicializarFeromonas() {
        feromonas = new HashMap<>();
        for (Tramo tramo : tramos) {
            Long key = tramo.getId_tramo();
            feromonas.put(key, feromonaInicial);
        }
    }

    // Se esta manejando que 1 tramo para 1 vehiculo
    // debemos hacer que se guarde o se cuente las distancias de cada tramo que va
    // pasar el vehiculo y guardarlo en la tabla intermedia ----> Se actualiza solo en averias o cuando termina el recorrido
    // Se calcula cuando se crea la ruta optima elegida <------
    private List<Tramo> construirRuta(Pedido pedido, int simulacion, LocalDateTime fechaMaximaEntregaGMT) {
        List<Tramo> ruta = new ArrayList<>(); // Rutas es un conjunto de tramos
        Set<Long> ubicacionesVisitadas = new HashSet<>(); // Para evitar ciclos
        Long idUbicacionActual = pedido.getFid_almacen();
        Long idUbicacionDestinoFinal = pedido.getFid_oficinaDest();
        LocalDateTime[] ultimaFechaHoraLlegada = { pedido.getFechaEntregaEstimada() };

        while (!idUbicacionActual.equals(idUbicacionDestinoFinal)) {
            ubicacionesVisitadas.add(idUbicacionActual);
            List<Tramo> tramosPosibles = grafoTramos.getOrDefault(idUbicacionActual, new ArrayList<>());

            tramosPosibles = tramosPosibles.stream()
                    .filter(t -> !ubicacionesVisitadas.contains(t.getDestino()))
                    .filter(t -> t.getFechaInicio().isAfter(ultimaFechaHoraLlegada[0]))
                    .filter(t -> t.getFechaFin().isBefore(fechaMaximaEntregaGMT)) // llega antes de la fecha máxima de entrega
                    .filter(t -> t.getCapacidadActual() >= pedido.getCantidadPaquetes()) // Que el tramo tenga capacidad para el pedido
                    .filter(t -> Duration.between(ultimaFechaHoraLlegada[0], t.getFechaInicio()).toMinutes() >= 5) // Que el tramo inicie al menos 5 min después de la llegada
                    .collect(Collectors.toList());

            if (tramosPosibles.isEmpty()) { // La hormiga no encuentra tramos disponibles
                ruta.clear();
                break;
            }
            // Long idUbicacionDestinoFinalLong = Long.valueOf(idUbicacionDestinoFinal); //
            // Si es un primitivo Long
            Tramo tramoDirecto = tramosPosibles.stream()
                    .filter(t -> t.getDestino().equals(Long.valueOf(idUbicacionDestinoFinal)))
                    .min(Comparator.comparing(Tramo::getFechaFin))
                    .orElse(null);

            Tramo siguienteTramo;
            if (tramoDirecto != null) {
                siguienteTramo = tramoDirecto; // Seleccionar el tramo directo si existe
            } else {
                siguienteTramo = elegirTramo(tramosPosibles, ultimaFechaHoraLlegada[0]); // Selección habitual
            }

            if (siguienteTramo == null) {
                ruta.clear(); // Limpia la ruta si no se puede elegir un tramo adecuado
                break;
            }

            ruta.add(siguienteTramo);
            idUbicacionActual = siguienteTramo.getDestino();
            LocalDateTime nuevaFechaHoraLlegada = siguienteTramo.getFechaFin();

            ultimaFechaHoraLlegada[0] = nuevaFechaHoraLlegada;
        }

        if (!idUbicacionActual.equals(idUbicacionDestinoFinal)) {
            ruta.clear(); // Asegura que la ruta finaliza en el destino correcto
        }

        return ruta;
    }

    // Método para calcular la distancia total de la ruta
    // verificar como se calcula la distancia y tambien el tiempo "estimado" de llegada
    private float calcularDistanciaTotal(List<Tramo> tramos) {
        float distanciaTotal = 0;
        for (Tramo tramo : tramos) {
            distanciaTotal += tramo.getDistancia();
        }
        return distanciaTotal;
    }

    private Tramo elegirTramo(List<Tramo> tramosPosibles, LocalDateTime fechaHoraLlegadaAnterior) {
 
        Map<Tramo, Double> probabilidades = new HashMap<>();
        double total = 0.0;       
        // Recorremos los tramos posibles y calculamos la probabilidad utilizando feromonas y heurística
        for (Tramo tramo : tramosPosibles) {
            double feromona = feromonas.getOrDefault(tramo.getId_tramo(), 0.0);
            double heuristica = calcularHeuristica(tramo, fechaHoraLlegadaAnterior); 
            double valor = Math.pow(feromona, alpha) * Math.pow(heuristica, beta);
            probabilidades.put(tramo, valor);
            total += valor;
        }
        // Normalizamos las probabilidades
        for (Tramo tramo : tramosPosibles) {
            probabilidades.put(tramo, probabilidades.get(tramo)/total);
        }
        // Seleccionamos un tramo basándonos en las probabilidades
        double r = random.nextDouble(); 
        double sum = 0.0;
        
        for (Map.Entry<Tramo, Double> entry : probabilidades.entrySet()) {
            sum += entry.getValue();
            if (sum >= r) return entry.getKey();
        }
        
        return null;  
    }

    private double calcularHeuristica(Tramo tramo, LocalDateTime fechaHoraLlegadaAnterior) {
        LocalDateTime fechaHoraSalida = tramo.getFechaInicio();
        LocalDateTime fechaHoraLlegada = tramo.getFechaFin();
        
        // Calcular el tiempo de espera en minutos entre la llegada del último vuelo y la salida de este vuelo
        long tiempoEspera = fechaHoraLlegadaAnterior.until(fechaHoraSalida, java.time.temporal.ChronoUnit.MINUTES);
        
        double tiempoEsperaHoras = (double) tiempoEspera/60.0;
        
        // Calcular la duracion del vuelo (Deberia ser considerando GMT)
        long duracionVuelo = fechaHoraSalida.until(fechaHoraLlegada, java.time.temporal.ChronoUnit.MINUTES);
        double duracionVueloHoras = duracionVuelo/60.0;
        
        // Factor de tiempo de espera: Penalizar tiempos de espera largos
        double factorTiempoEspera = (tiempoEsperaHoras > 0) ? 1.0 / (tiempoEsperaHoras+duracionVueloHoras) : 0;  // Evita la división por cero
   
        double ponderacionTiempoEspera = 1.0;
        
        return (ponderacionTiempoEspera * factorTiempoEspera);
    }

    public PlanTransporte ejecutar(List<Oficina> oficinas, List<Tramo> tramos, Pedido pedidoIngresado, int simulacion) {
        this.tramos = tramos;
        this.pedido = pedidoIngresado;
        boolean solutionFound = false;
        LocalDateTime fechaMaximaEntrega = calcularFechaMaximaEntregaDestino(pedidoIngresado); // Calcula la fecha máxima de entrega en destino
        inicializarGrafoTramos(fechaMaximaEntrega);
        inicializarFeromonas();
        
        System.out.println("El pedido actual es: " + pedidoIngresado.getId_pedido() + " " + pedidoIngresado.getFechaEntregaEstimada() + "  " +
                pedido.getFid_almacen() + "  ->  " + pedido.getFid_oficinaDest() + "  " + "ciudades ORIGEN - DESTINO");
                // Asegúrate de ajustar la forma en la que obtienes las ciudades de origen y destino según la lógica de OdiparPack
        for (int i = 1; i <= numeroIteraciones; i++) {
            if(solutionFound)break;
            List<PlanTransporte> rutasEncontradas = new ArrayList<>(); // PlanTransporte = pedido y lista de tramos
            for (int j = 1; j <= numeroHormigas; j++) {
                List<Tramo> ruta = construirRuta(pedido, simulacion, fechaMaximaEntrega); // Ruta = lista de tramos
                
                if (!ruta.isEmpty()) {
                    PlanTransporte planTransporte = new PlanTransporte();
                    planTransporte.setPedido(pedido);
                    planTransporte.setTramos(ruta);
                    rutasEncontradas.add(planTransporte);

                    // Devolver la primera ruta válida encontrada
                    for (Tramo tramo : ruta) {
                        System.out.println("Tramo de " + tramo.getOrigen() + " hacia " + tramo.getDestino()
                                + "  ->  " + tramo.getFechaInicio() + " - " + tramo.getFechaFin());
                    }
                    solutionFound = true;
                    break; // Devolver la primera ruta válida y cortar el bucle
                }
            }
            
            if (!rutasEncontradas.isEmpty()) {
                actualizarFeromonasRuta(rutasEncontradas); // Evapora y actualiza con las rutas encontradas por todas las hormigas
            }
            else{
                System.out.println("No se encontró una ruta válida después de todas las iteraciones");
                return null;
            }
        }
        return planTransporte;
        
    }


}
