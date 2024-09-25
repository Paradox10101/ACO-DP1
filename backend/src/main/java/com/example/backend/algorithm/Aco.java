package com.example.backend.algorithm;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Transient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.backend.models.Oficina;
import com.example.backend.models.Pedido;
import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Region;
import com.example.backend.models.Tramo;
import com.example.backend.models.Ubicacion;
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
                grafoTramos.computeIfAbsent(tramo.getubicacionOrigen().getIdUbicacion(), k -> new ArrayList<>()).add(tramo);
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
        Long idUbicacionActual = pedido.getAlmacen().getId_almacen();
        Long idUbicacionDestinoFinal = pedido.getOficinaDestino().getId_oficina();
        LocalDateTime[] ultimaFechaHoraLlegada = { pedido.getFechaEntregaEstimada() };

        while (!idUbicacionActual.equals(idUbicacionDestinoFinal)) {
            ubicacionesVisitadas.add(idUbicacionActual);
            List<Tramo> tramosPosibles = grafoTramos.getOrDefault(idUbicacionActual, new ArrayList<>());

            tramosPosibles = tramosPosibles.stream()
                    .filter(t -> !ubicacionesVisitadas.contains(t.getubicacionDestino().getIdUbicacion()))
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
                    .filter(t -> t.getubicacionDestino().getIdUbicacion().equals(Long.valueOf(idUbicacionDestinoFinal)))
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
            idUbicacionActual = siguienteTramo.getubicacionDestino().getIdUbicacion();
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
        
        // Calcular el tiempo de espera en minutos entre la llegada del último tramo y la salida de este tramo
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

    public PlanTransporte ejecutar(List<Oficina> oficinas, List<Tramo> tramos, Pedido pedidoIngresado, int simulacion, List<Region> regiones) {
        this.tramos = tramos;
        this.pedido = pedidoIngresado;
        boolean solutionFound = false;
        LocalDateTime fechaMaximaEntrega = calcularFechaMaximaEntregaDestino(pedidoIngresado, oficinas,regiones); // Calcula la fecha máxima de entrega en destino
        inicializarGrafoTramos(fechaMaximaEntrega);
        inicializarFeromonas();
        
        PlanTransporte planTransporteFinal = null;

        System.out.println("El pedido actual es: " + pedidoIngresado.getId_pedido() + " " + pedidoIngresado.getFechaEntregaEstimada() + "  " +
                pedido.getAlmacen().getId_almacen() + "  ->  " + pedido.getOficinaDestino().getId_oficina() + "  " + "ciudades ORIGEN - DESTINO");
                // Asegúrate de ajustar la forma en la que obtienes las ciudades de origen y destino según la lógica de OdiparPack
        for (int i = 1; i <= numeroIteraciones; i++) {
            if(solutionFound)break;
            List<PlanTransporte> rutasEncontradas = new ArrayList<>(); // PlanTransporte = pedido y lista de tramos
            for (int j = 1; j <= numeroHormigas; j++) {
                List<Tramo> ruta = construirRuta(pedido, simulacion, fechaMaximaEntrega); // Ruta = lista de tramos
                
                if (!ruta.isEmpty()) {
                    PlanTransporte planTransporte = new PlanTransporte();
                    planTransporte.setPedido(pedido);
                    //planTransporte.setTramos(ruta);
                    rutasEncontradas.add(planTransporte);

                    // Devolver la primera ruta válida encontrada
                    for (Tramo tramo : ruta) {
                        System.out.println("Tramo de " + tramo.getubicacionOrigen().getIdUbicacion() + " hacia " + tramo.getubicacionDestino().getIdUbicacion()
                                + "  ->  " + tramo.getFechaInicio() + " - " + tramo.getFechaFin());
                    }
                    planTransporteFinal = planTransporte;
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
        return planTransporteFinal;        
    }

    private LocalDateTime calcularFechaMaximaEntregaDestino(Pedido pedido,List<Oficina> oficinas,List<Region> regiones) {
        // LOGICA ANTIGUA 23:59:59 DEL DIA DE DESTINO
        /*
         * LocalDateTime fechaHoraRecepcionEnDestino =
         * envio.getFechaHoraRecepcion().plusHours(envio.getAeropuertoDestino().
         * getCiudad().getGmt()); // La hora en el lugar de destino
         * int diasAdicionales = envio.getTipo() ? 2 : 1; // 2 días si es
         * intercontinental, 1 día en caso contrario
         * LocalDateTime fechaMaximaEntregaEnDestino =
         * fechaHoraRecepcionEnDestino.plusDays(diasAdicionales).with(LocalTime.MAX);
         * // De nuevo se convierte a GMT
         * LocalDateTime fechaMaximaEntregaGMT =
         * fechaMaximaEntregaEnDestino.minusHours(envio.getAeropuertoDestino().getCiudad
         * ().getGmt());
         * 
         * return fechaMaximaEntregaGMT;
         */
        // LOGICA NUEVA
        //Oficina oficinaDestino = oficinaDestino.findbyId(pedido.getFid_oficinaDest());
        //Region regionDestino = regionDestino.findbyId(oficinaDestino.getFid_ubicacion());
// Simulación de datos de Oficinas y Regiones (Hard-coded)

        //Long a = 1L,b,c,d;
        /*List<Oficina> oficinas = Arrays.asList(
            new Oficina(1L,1L, 101,200), 
            new Oficina(2L,2L, 102,200),
            new Oficina(3L, 3L, 103,200)
        );
        
        List<Region> regiones = Arrays.asList(
            new Region(101L, "Costa", 2), 
            new Region(102L, "Sierra", 4),
            new Region(103L, "Selva", 5)
        );

        // Buscar la oficina de destino usando el ID proporcionado en el pedido
        Oficina oficinaDestino = oficinas.stream()
                .filter(oficina -> oficina.getId_oficina().equals(pedido.getFid_oficinaDest()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Oficina no encontrada"));
        
        // Buscar la región asociada a la oficina de destino
        Region regionDestino = regiones.stream()
                .filter(region -> region.getIdRegion().equals(oficinaDestino.getFid_ubicacion()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Región no encontrada"));



        LocalDateTime fechaMaximaEntregaGMT = pedido.getFechaEntregaEstimada().plusDays(regionDestino.getDiasLimite()).with(LocalTime.MAX);
        */

        /*if(regionDestino.getNombre() == "Costa"){
            return pedido.getFechaEntregaEstimada().plusDays(1).with(LocalTime.MAX);
        }
        else{
            if(regionDestino.getNombre() == "Selva"){
                return pedido.getFechaEntregaEstimada().plusDays(3).with(LocalTime.MAX);
            }
            else{
                //sierra
                return pedido.getFechaEntregaEstimada().plusDays(2).with(LocalTime.MAX);
            }
            

        }*/

        /*LocalDateTime fechaHoraRecepcionEnOrigen = pedido.getFechaEntregaEstimada()
                .plusHours(.getGmt()); // La hora en el lugar de origen
        int diasAdicionales = pedido.getTipo(); // 2 días si es intercontinental, 1 día en caso contrario
        LocalDateTime fechaMaximaEntregaEnDestino = fechaHoraRecepcionEnOrigen.plusDays(diasAdicionales);
        // De nuevo se convierte a GMT
        LocalDateTime fechaMaximaEntregaGMT = fechaMaximaEntregaEnDestino
                .minusHours(pedido.getAeropuertoDestino().getCiudad().getGmt());*/

        // Aquí simulas la búsqueda de la oficina y la región sin usar una BD real
        Oficina oficinaDestino = obtenerOficinaPorId(pedido.getOficinaDestino().getId_oficina(), oficinas); // Simulación de búsqueda de oficina
        Ubicacion ubicacionDestino = obtenerUbicacionPorId(oficinaDestino.getUbicacion().getIdUbicacion()); // Simulación de búsqueda
                                                                                               // de ubicación
        Region regionDestino = obtenerRegionPorId(ubicacionDestino.getRegion().getIdRegion(), regiones);

        LocalDateTime fechaMaximaEntregaGMT = pedido.getFechaEntregaEstimada().plusDays(regionDestino.getDiasLimite())
                .with(LocalTime.MAX);
        return fechaMaximaEntregaGMT;
    }

    // Métodos simulados para obtener la oficina y la ubicación (para pruebas):
    private Oficina obtenerOficinaPorId(Long idOficina, List<Oficina> oficinas) {
        for (Oficina oficina : oficinas) {
            if (oficina.getId_oficina().equals(idOficina)) {
                return oficina;
            }
        }
        return null; // Devuelve null si no encuentra la oficina
    }

        ///ESTE ES EL UNICO QUE SE  ESTA CREANDO ASI POR PRUEBAS <------- REVISAR SI SE NECESITA
    private Ubicacion obtenerUbicacionPorId(Long idUbicacion) {
        // Simula la obtención de la ubicación según su ID
        //Falta obtener region por ID
        //return new Ubicacion(idUbicacion, "Ubigeo 001", "Ciudad Prueba", 1L); // 1L es el ID de la región, cambia según tus datos
        return null;
    }

    private Region obtenerRegionPorId(Long idRegion, List<Region> regiones) {
        for (Region region : regiones) {
            if (region.getIdRegion().equals(idRegion)) {
                return region;
            }
        }
        return null; // Devuelve null si no encuentra la oficina
    }

    private void actualizarFeromonasRuta(List<PlanTransporte> rutasEncontradas) {
        evaporarFeromonas();

        for (PlanTransporte plan : rutasEncontradas) {
            double costoTotal = 0.0;

            //Obtencion de tramos por plan
            /*
            for (Tramo tramo : plan.getTramos()) {
                costoTotal += calcularDuracionTramo(tramo); // Calcula solo la duración de los tramos
            }
            */

            //Obtencion de tramos por plan
            /*
            for (Tramo tramo : plan.getTramos()) {
                Long key = tramo.getId_tramo();
                feromonas.put(key, feromonas.get(key) + (1 / costoTotal));
            }
            */
        }
    }

    private double calcularDuracionTramo(Tramo tramo) {
        LocalDateTime fechaHoraSalida = tramo.getFechaInicio();
        LocalDateTime fechaHoraLlegada = tramo.getFechaFin();

        // Calcular la duración del tramo en minutos
        long duracionEnMinutos = fechaHoraSalida.until(fechaHoraLlegada, java.time.temporal.ChronoUnit.MINUTES);

        // Convertir la duración a horas
        double duracionEnHoras = (double) duracionEnMinutos / 60.0;

        return duracionEnHoras;
    }

    private void evaporarFeromonas() {
        for (Map.Entry<Long, Double> entry : feromonas.entrySet()) {
            Long key = entry.getKey();
            Double value = entry.getValue();
            feromonas.put(key, (1 - tasaEvaporacion) * value);
        }
    }
}
