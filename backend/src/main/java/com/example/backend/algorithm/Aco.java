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
import com.example.backend.models.Tramo;
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

    /*private void inicializarGrafoVuelos(LocalDateTime fechaMaximaEntregaGMT) {
        grafoRutas = new HashMap<>();
        for (Ruta ruta : rutas) {
            if (ruta.getFechaHoraLlegada().isBefore(fechaMaximaEntregaGMT)
                    || ruta.getFechaHoraLlegada().isEqual(fechaMaximaEntregaGMT)) {
                grafoRutas.computeIfAbsent(ruta.getAeropuertoOrigen().getId(), k -> new ArrayList<>()).add(ruta);
            }
        }
    }
    
    private void inicializarGrafoRutas() {
        grafoRutas = new HashMap<>();
        for (Pedido pedido : pedidos) {
            List<Ruta> rutas = new ArrayList<>();
            for (Pedido pedido2 : pedidos) {
                if (pedido.getId_pedido() != pedido2.getId_pedido()) {
                    rutas.add(new Ruta(pedido, pedido2));
                }
            }
            grafoRutas.put(pedido.getId_pedido(), rutas);
        }
    }


    //el "id" se guarda como "long" --> Para que no haya problemas con el "put" de la feromona de "HashMap"
    private void inicializarFeromonas() {
        feromonas = new HashMap<>();
        for (Tramo tramo : tramos) {
            Long key = ruta.getId_ruta();
            feromonas.put(key, feromonaInicial);//solo acepta long y double
        }
    }*/
    private void inicializarGrafoRutas(LocalDateTime fechaMaximaEntrega) {
        grafoRutas = new HashMap<>();
        for (Ruta ruta : rutas) { // 'rutas' es la lista de todas las rutas disponibles
            if (ruta.getFechaFin().isBefore(fechaMaximaEntrega) || ruta.getFechaFin().isEqual(fechaMaximaEntrega)) {
                Long origenId = ruta.getOrigen();
                grafoRutas.computeIfAbsent(origenId, k -> new ArrayList<>()).add(ruta);
            }
        }
    }

    private void inicializarFeromonas() {
        feromonas = new HashMap<>();
        for (Ruta ruta : rutas) { // Inicializamos la feromona para cada ruta
            Long key = ruta.getId_ruta();
            feromonas.put(key, feromonaInicial); // 'feromonaInicial' es el valor inicial que ya has definido
        }
    }

    private List<Ruta> construirRuta(Pedido pedido, int simulacion, LocalDateTime fechaMaximaEntrega) {
        List<Ruta> rutasConstruidas = new ArrayList<>();
        Set<Long> ubicacionesVisitadas = new HashSet<>(); // Para evitar ciclos
        Long idUbicacionActual = pedido.getUbicacionOrigen().getId(); // Reemplazar Aeropuerto por Ubicacion
        Long idUbicacionDestinoFinal = pedido.getUbicacionDestino().getId(); // Reemplazar Aeropuerto por Ubicacion
        LocalDateTime[] ultimaFechaHoraLlegada = { pedido.getFechaHoraRecepcion() }; // Ajustar a Pedido

        // Reemplazar lógica de vuelos con lógica de rutas
        while (!idUbicacionActual.equals(idUbicacionDestinoFinal)) {
            ubicacionesVisitadas.add(idUbicacionActual);
            List<Ruta> rutasPosibles = grafoRutas.getOrDefault(idUbicacionActual, new ArrayList<>());

            // Filtrar las rutas posibles
            rutasPosibles = rutasPosibles.stream()
                    .filter(r -> !ubicacionesVisitadas.contains(r.getDestino()))
                    .filter(r -> r.getFechaInicio().isAfter(ultimaFechaHoraLlegada[0]))
                    .filter(r -> r.getFechaFin().isBefore(fechaMaximaEntrega)) // Asegurar entrega antes del tiempo máximo
                    .filter(r -> r.getCapacidadActual() >= pedido.getNumeroPaquetes()) // Verificar capacidad para el pedido
                    .filter(r -> Duration.between(ultimaFechaHoraLlegada[0], r.getFechaInicio()).toMinutes() >= 5) // Tiempo mínimo de espera de 5 minutos --> para que el camion salga a la entrega
                    .filter(r -> (simulacion == 0
                            ? verificarCapacidadAlmacen(r.getDestino(), r.getFechaFin(), pedido.getNumeroPaquetes())
                            : verificarCapacidadAlmacenSimulacion(r.getDestino(), r.getFechaFin(),pedido.getNumeroPaquetes())))
                    .collect(Collectors.toList());

            if (rutasPosibles.isEmpty()) { // Si no hay rutas posibles, la hormiga no puede continuar
                rutasConstruidas.clear();
                break;
            }

            // Intentar encontrar una ruta directa
            Ruta rutaDirecta = rutasPosibles.stream()
                    .filter(r -> r.getDestino().equals(idUbicacionDestinoFinal))
                    .min(Comparator.comparing(Ruta::getFechaFin))
                    .orElse(null);

            Ruta siguienteRuta;
            if (rutaDirecta != null) {
                siguienteRuta = rutaDirecta; // Seleccionar la ruta directa si existe
            } else {
                siguienteRuta = elegirRuta(rutasPosibles, ultimaFechaHoraLlegada[0]); // Selección habitual
            }

            if (siguienteRuta == null) {
                rutasConstruidas.clear(); // Limpia la ruta si no se puede elegir un tramo adecuado
                break;
            }

            rutasConstruidas.add(siguienteRuta);
            idUbicacionActual = siguienteRuta.getDestino(); // Actualizar la ubicación actual a la nueva
            LocalDateTime nuevaFechaHoraLlegada = siguienteRuta.getFechaFin();

            ultimaFechaHoraLlegada[0] = nuevaFechaHoraLlegada;
        }

        // Verificar que se llegó al destino final
        if (!idUbicacionActual.equals(idUbicacionDestinoFinal)) {
            rutasConstruidas.clear(); // Asegura que la ruta finaliza en el destino correcto
        }

        return rutasConstruidas;
    }
    
    // Método para calcular la distancia total de la ruta
    private float calcularDistanciaTotal(List<Tramo> tramos) {
        float distanciaTotal = 0;
        for (Tramo tramo : tramos) {
            distanciaTotal += tramo.getDistancia();
        }
        return distanciaTotal;
    }




}
