package com.example.backend.algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.backend.Repository.AlmacenRepository;
import com.example.backend.Repository.OficinaRepository;
import com.example.backend.Repository.UbicacionRepository;
import com.example.backend.models.Almacen;
import com.example.backend.models.Oficina;
import com.example.backend.models.Pedido;
import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Region;
import com.example.backend.models.Tramo;
import com.example.backend.models.Ubicacion;
import com.example.backend.models.Vehiculo;

@Service
public class Aco {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private AlmacenRepository almacenRepository;

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

    private void inicializarGrafoTramos(LocalDateTime fechaMaximaEntrega) {
        grafoTramos = new HashMap<>();
        for (Tramo tramo : tramos) {
            // Verifica que el tramo esté disponible antes de la fecha máxima de entrega
            if (tramo.getFechaFin().isBefore(fechaMaximaEntrega)
                    || tramo.getFechaFin().isEqual(fechaMaximaEntrega)) {
                grafoTramos.computeIfAbsent(tramo.getubicacionOrigen().getId_ubicacion(), k -> new ArrayList<>())
                        .add(tramo);
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
    // pasar el vehiculo y guardarlo en la tabla intermedia ----> Se actualiza solo
    // en averias o cuando termina el recorrido
    // Se calcula cuando se crea la ruta optima elegida <------
    private List<Tramo> construirRuta(Pedido pedido, int simulacion, LocalDateTime fechaMaximaEntrega) {
        //Eleccion del mejor almacen
        List<Tramo> ruta = new ArrayList<>(); // Rutas es un conjunto de tramos
        Set<Long> ubicacionesVisitadas = new HashSet<>(); // Para evitar ciclos
        Long idUbicacionActual = pedido.getAlmacen().getId_almacen();
        Long idUbicacionDestinoFinal = pedido.getOficinaDestino().getId_oficina();
        LocalDateTime[] ultimaFechaHoraLlegada = { pedido.getFechaEntregaEstimada() };
        
        System.out.println("Ejecucion pre-construir ruta");
        //List<Tramo> tramosPosibles = grafoTramos.getOrDefault(idUbicacionActual, new ArrayList<>());
        
        while (!idUbicacionActual.equals(idUbicacionDestinoFinal)) {
            ubicacionesVisitadas.add(idUbicacionActual);
            List<Tramo> tramosPosibles = grafoTramos.getOrDefault(idUbicacionActual, new ArrayList<>());

            if (tramosPosibles.isEmpty()) { // La hormiga no encuentra tramos disponibles
                //ruta.clear();
                System.out.println("-----------------RUTAS ES VACIO DESDE EL INICIO ---------------------------------");
                break;
            }

            tramosPosibles = tramosPosibles.stream()
                    .filter(t -> !ubicacionesVisitadas.contains(t.getubicacionDestino().getId_ubicacion()))
                    /*.filter(t -> t.getFechaInicio().isAfter(ultimaFechaHoraLlegada[0]))*/
                    /*.filter(t -> t.getFechaFin().isBefore(fechaMaximaEntrega))*/ // llega antes de la fecha máxima de
                                                                                  // entrega
                    /*.filter(t -> t.getCapacidadActual() >= pedido.getCantidadPaquetes())*/ // Que el tramo tenga capacidad
                                                                                         // para el pedido
                    /*.filter(t -> Duration.between(ultimaFechaHoraLlegada[0], t.getFechaInicio()).toMinutes() >= 320)*/
                    .collect(Collectors.toList());

            if (tramosPosibles.isEmpty()) { // La hormiga no encuentra tramos disponibles
                ruta.clear();
                System.out.println("-----------------RUTAS NO ENCONTRADAS---------------------------------");
                break;
            }
            // Long idUbicacionDestinoFinalLong = Long.valueOf(idUbicacionDestinoFinal); //
            // Si es un primitivo Long
            Tramo tramoDirecto = tramosPosibles.stream()
                    .filter(t -> t.getubicacionDestino().getId_ubicacion()
                            .equals(Long.valueOf(idUbicacionDestinoFinal)))
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
            idUbicacionActual = siguienteTramo.getubicacionDestino().getId_ubicacion();
            LocalDateTime nuevaFechaHoraLlegada = siguienteTramo.getFechaFin();

            ultimaFechaHoraLlegada[0] = nuevaFechaHoraLlegada;
        }

        if (!idUbicacionActual.equals(idUbicacionDestinoFinal)) {
            ruta.clear(); // Asegura que la ruta finaliza en el destino correcto
        }

        return ruta;
    }

    // Método para calcular la distancia total de la ruta
    // verificar como se calcula la distancia y tambien el tiempo "estimado" de
    // llegada
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
        // Recorremos los tramos posibles y calculamos la probabilidad utilizando
        // feromonas y heurística
        for (Tramo tramo : tramosPosibles) {
            double feromona = feromonas.getOrDefault(tramo.getId_tramo(), 0.0);// Intensidad de uso de ese tramo por
                                                                               // hormigas
            double heuristica = calcularHeuristica(tramo, fechaHoraLlegadaAnterior);
            double valor = Math.pow(feromona, alpha) * Math.pow(heuristica, beta);
            probabilidades.put(tramo, valor);
            total += valor;
        }
        // Normalizamos las probabilidades
        for (Tramo tramo : tramosPosibles) {
            probabilidades.put(tramo, probabilidades.get(tramo) / total);
        }
        // Seleccionamos un tramo basándonos en las probabilidades
        double r = random.nextDouble();
        double sum = 0.0;

        for (Map.Entry<Tramo, Double> entry : probabilidades.entrySet()) {
            sum += entry.getValue();
            if (sum >= r)
                return entry.getKey();
        }

        return null;
    }

    private double calcularHeuristica(Tramo tramo, LocalDateTime fechaHoraLlegadaAnterior) {
        LocalDateTime fechaHoraSalida = tramo.getFechaInicio();
        LocalDateTime fechaHoraLlegada = tramo.getFechaFin();

        // Calcular el tiempo de espera en minutos entre la llegada del último tramo y
        // la salida de este tramo
        long tiempoEspera = fechaHoraLlegadaAnterior.until(fechaHoraSalida, java.time.temporal.ChronoUnit.MINUTES);

        double tiempoEsperaHoras = (double) tiempoEspera / 60.0;

        double ponderacionTiempoEspera = 1.0;

        return (ponderacionTiempoEspera);
    }

    public ArrayList<Tramo> generarTramosDesdeCaminos(HashMap<String, ArrayList<Ubicacion>> caminos, List<Ubicacion> todasLasUbicaciones) {
        ArrayList<Tramo> tramos = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Ubicacion>> entry : caminos.entrySet()) {
            String ubigeoOrigen = entry.getKey();
            ArrayList<Ubicacion> ubicacionesDestino = entry.getValue();

            // Encuentra la ubicación de origen en la lista completa de ubicaciones
            Optional<Ubicacion> ubicacionOrigen = todasLasUbicaciones.stream()
                    .filter(ubicacion -> ubicacion.getUbigeo().equals(ubigeoOrigen))
                    .findFirst();

            // Si existe la ubicación de origen, procede
            if (ubicacionOrigen.isPresent()) {
                for (Ubicacion ubicacionDestino : ubicacionesDestino) {
                    // Crea un nuevo tramo para cada destino desde la ubicación origen
                    Tramo tramo = new Tramo(
                            ubicacionOrigen.get(),
                            ubicacionDestino,
                            false, // bloqueado, valor inicial
                            0.0f, // distancia inicial
                            0.0f, // velocidad inicial
                            null // PlanTransporte, valor inicial nulo
                    );
                    // Calcular la distancia utilizando la función que ya hemos definido
                    double distancia = calcularDistanciaEntreTramos(tramo);
                    tramo.setDistancia((float) distancia); // Asignar la distancia calculada

                    tramos.add(tramo);
                }
            } else {
                System.out.println("Ubicación de origen no encontrada para el ubigeo: " + ubigeoOrigen);
            }
        }

        return tramos;
    }

    public double calcularDistanciaEntreTramos(Tramo tramo) {
        Ubicacion origen = tramo.getubicacionOrigen();
        Ubicacion destino = tramo.getubicacionDestino();

        // Convertir latitud y longitud de grados a radianes
        double latOrigen = Math.toRadians(origen.getLatitud());
        double lonOrigen = Math.toRadians(origen.getLongitud());
        double latDestino = Math.toRadians(destino.getLatitud());
        double lonDestino = Math.toRadians(destino.getLongitud());

        // Diferencias entre las latitudes y longitudes
        double deltaLat = latDestino - latOrigen;
        double deltaLon = lonDestino - lonOrigen;

        // Fórmula Haversine
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(latOrigen) * Math.cos(latDestino) * Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Radio de la Tierra en kilómetros
        final double radioTierra = 6371.0;

        // Distancia en kilómetros
        double distancia = radioTierra * c;

        return distancia;
    }

    private double calcularDistanciaEntrePuntos(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA = 6371; // Radio de la Tierra en km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA * c; // Retorna la distancia en kilómetros
    }

    private double calcularDistanciaMinima(Ubicacion actual, Ubicacion destino,
            HashMap<String, ArrayList<Ubicacion>> caminos, double distanciaAcumulada, Set<String> visitados) {
        // Si hemos llegado al destino, devolvemos la distancia acumulada
        if (actual.getUbigeo().equals(destino.getUbigeo())) {
            return distanciaAcumulada;
        }

        // Marcamos la ubicación actual como visitada para evitar ciclos
        visitados.add(actual.getUbigeo());

        // Obtenemos los posibles destinos desde la ubicación actual
        ArrayList<Ubicacion> ubicacionesSiguientes = caminos.get(actual.getUbigeo());
        if (ubicacionesSiguientes == null || ubicacionesSiguientes.isEmpty()) {
            return Double.MAX_VALUE; // Si no hay caminos, devolvemos una distancia infinita
        }

        double distanciaMinima = Double.MAX_VALUE;

        // Iterar sobre los posibles destinos
        for (Ubicacion siguiente : ubicacionesSiguientes) {
            if (!visitados.contains(siguiente.getUbigeo())) {
                // Calcular la distancia desde la ubicación actual hasta la siguiente
                double distanciaTramo = calcularDistanciaEntrePuntos(actual.getLatitud(), actual.getLongitud(),
                        siguiente.getLatitud(), siguiente.getLongitud());
                // Recursivamente encontrar la distancia mínima desde la siguiente ubicación
                // hasta el destino
                double distanciaTotal = calcularDistanciaMinima(siguiente, destino, caminos,
                        distanciaAcumulada + distanciaTramo, new HashSet<>(visitados));

                // Actualizar la distancia mínima encontrada
                if (distanciaTotal < distanciaMinima) {
                    distanciaMinima = distanciaTotal;
                }
            }
        }

        return distanciaMinima;
    }

    public PlanTransporte ejecutar(List<Oficina> oficinas,
            HashMap<String, ArrayList<Ubicacion>> caminos, Pedido pedidoIngresado, int simulacion,
            List<Region> regiones, List<Ubicacion> ubicaciones) {
        
        
        boolean solutionFound = false;
        LocalDateTime fechaMaximaEntrega = calcularFechaMaximaEntregaDestino(pedidoIngresado, oficinas, regiones); // Calcula la fecha máxima de entrega en destino
        
        List<Tramo> tramos = generarTramosDesdeCaminos(caminos, ubicaciones);//caminos.get(pedidoIngresado.getOficinaDestino().getUbicacion().getUbigeo()); // Simulación de búsqueda de tramos
        this.tramos = tramos;
        System.out.println("Listado de Tramos Registrados:");
        System.out.println("--------------------------------------------------");
        
        for (Tramo tramo : tramos) {
            System.out.println("ID Tramo: " + tramo.getId_tramo());
            System.out.println("Ubicación Origen - ID: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionOrigen().getUbigeo());
            System.out.println("Ubicación Destino - ID: " + tramo.getubicacionDestino().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionDestino().getUbigeo());
            System.out.println("Distancia: " + tramo.getDistancia() + " km");
            System.out.println("Bloqueado: " + (tramo.isBloqueado() ? "Sí" : "No"));
            System.out.println("--------------------------------------------------");
        }

        inicializarGrafoTramos(fechaMaximaEntrega);
        inicializarFeromonas();

        System.out.println("intento de ejecutar");
        System.out.println("-----------------ELECCION DEL MEJOR ALMACEN---------------------------------");
        //Se calcula el Almacen de origen
        //Se basa en la distancia entre el primer, luego el segundo y el tercer almacen y la oficina de destino
        //Se calcula la distancia menor entre cada almacen y se elige el que tenga la menor distancia
        // Iterar sobre todos los almacenes
        double distanciaMinima = Double.MAX_VALUE;
        Almacen almacenMasCercano = null;
        List<Almacen> almacenes = almacenRepository.findAll();
        /*for (Almacen almacen : almacenes) {
            // Obtener la distancia más corta desde este almacén hasta el destino
            double distancia = calcularDistanciaMinima(almacen.getUbicacion(), 
                    pedidoIngresado.getOficinaDestino().getUbicacion(), caminos, 0, new HashSet<>());
            
            // Si encontramos una distancia más corta, actualizamos el almacén más cercano
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                almacenMasCercano = almacen;
            }
        }*/


        pedidoIngresado.setAlmacen(almacenes.get(0)); // Asignamos el almacén más cercano al pedido

        PlanTransporte planTransporteFinal = null;

        this.pedido = pedidoIngresado;
        System.out.println("El pedido actual es: " + pedidoIngresado.getId_pedido() + " "
                + pedidoIngresado.getFechaEntregaEstimada() + "  " +
                pedido.getAlmacen().getId_almacen() + "  ->  " + pedido.getOficinaDestino().getId_oficina() + "  "
                + "ciudades ORIGEN - DESTINO");

        // Asegúrate de ajustar la forma en la que obtienes las ciudades de origen y
        // destino según la lógica de OdiparPack
        for (int i = 1; i <= numeroIteraciones; i++) {
            if (solutionFound)
                break;
            List<PlanTransporte> rutasEncontradas = new ArrayList<>(); // PlanTransporte = pedido y lista de tramos
            for (int j = 1; j <= numeroHormigas; j++) {
                List<Tramo> ruta = construirRuta(pedido, simulacion, fechaMaximaEntrega); // Ruta = lista de tramos

                if (!ruta.isEmpty()) {
                    PlanTransporte planTransporte = new PlanTransporte();
                    planTransporte.setPedido(pedido);
                    // planTransporte.setTramos(ruta);
                    rutasEncontradas.add(planTransporte);

                    // Devolver la primera ruta válida encontrada
                    for (Tramo tramo : ruta) {
                        System.out.println("Tramo de " + tramo.getubicacionOrigen().getId_ubicacion() + " hacia "
                                + tramo.getubicacionDestino().getId_ubicacion()
                                + "  ->  " + tramo.getFechaInicio() + " - " + tramo.getFechaFin());
                    }
                    planTransporteFinal = planTransporte;
                    solutionFound = true;
                    break; // Devolver la primera ruta válida y cortar el bucle
                }
            }

            if (!rutasEncontradas.isEmpty()) {
                actualizarFeromonasRuta(rutasEncontradas); // Evapora y actualiza con las rutas encontradas por todas
                                                           // las hormigas
            } else {
                System.out.println("No se encontró una ruta válida después de todas las iteraciones");
                return null;
            }
        }
        return planTransporteFinal;
    }

    private LocalDateTime calcularFechaMaximaEntregaDestino(Pedido pedido, List<Oficina> oficinas,
            List<Region> regiones) {

        /*
         * List<Oficina> oficinas = Arrays.asList(
         * new Oficina(1L,1L, 101,200),
         * new Oficina(2L,2L, 102,200),
         * new Oficina(3L, 3L, 103,200)
         * );
         * 
         * List<Region> regiones = Arrays.asList(
         * new Region(101L, "Costa", 2),
         * new Region(102L, "Sierra", 4),
         * new Region(103L, "Selva", 5)
         * );
         * 
         * // Buscar la oficina de destino usando el ID proporcionado en el pedido
         * Oficina oficinaDestino = oficinas.stream()
         * .filter(oficina ->
         * oficina.getId_oficina().equals(pedido.getFid_oficinaDest()))
         * .findFirst()
         * .orElseThrow(() -> new IllegalArgumentException("Oficina no encontrada"));
         * 
         * // Buscar la región asociada a la oficina de destino
         * Region regionDestino = regiones.stream()
         * .filter(region ->
         * region.getIdRegion().equals(oficinaDestino.getFid_ubicacion()))
         * .findFirst()
         * .orElseThrow(() -> new IllegalArgumentException("Región no encontrada"));
         * LocalDateTime fechaMaximaEntregaGMT =
         * pedido.getFechaEntregaEstimada().plusDays(regionDestino.getDiasLimite()).with
         * (LocalTime.MAX);
         */

        /*
         * if(regionDestino.getNombre() == "Costa"){
         * return pedido.getFechaEntregaEstimada().plusDays(1).with(LocalTime.MAX);
         * }
         * else{
         * if(regionDestino.getNombre() == "Selva"){
         * return pedido.getFechaEntregaEstimada().plusDays(3).with(LocalTime.MAX);
         * }
         * else{
         * //sierra
         * return pedido.getFechaEntregaEstimada().plusDays(2).with(LocalTime.MAX);
         * }
         * }
         */

        Oficina oficinaDestino = oficinaRepository.findById(pedido.getOficinaDestino().getId_oficina()).get(); // Simulación de búsqueda de oficina
        Ubicacion ubicacionDestino = ubicacionRepository.findById(oficinaDestino.getUbicacion().getId_ubicacion()).get(); // Simulación de búsqueda de ubicación
        Region regionDestino = obtenerRegionPorId(ubicacionDestino.getRegion().getIdRegion(), regiones);

        /*LocalDateTime fechaMaximaEntrega = pedido.getFechaEntregaEstimada().plusDays(regionDestino.getDiasLimite())
                .with(LocalTime.MAX);*/
        LocalDateTime fechaMaximaEntrega = pedido.getFechaEntregaEstimada().plusDays(regionDestino.getDiasLimite());
                //.with(LocalTime.MAX);
        
        return fechaMaximaEntrega;
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

    private Ubicacion obtenerUbicacionPorId(Long idUbicacion) {
        // Simula la obtención de la ubicación según su ID
        // Falta obtener region por ID
        // return new Ubicacion(idUbicacion, "Ubigeo 001", "Ciudad Prueba", 1L); // 1L
        // es el ID de la región, cambia según tus datos
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

            // Obtencion de tramos por plan
            /*
             * for (Tramo tramo : plan.getTramos()) {
             * costoTotal += calcularDuracionTramo(tramo); // Calcula solo la duración de
             * los tramos
             * }
             */

            // Obtencion de tramos por plan
            /*
             * for (Tramo tramo : plan.getTramos()) {
             * Long key = tramo.getId_tramo();
             * feromonas.put(key, feromonas.get(key) + (1 / costoTotal));
             * }
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

    public HashMap<String, ArrayList<Ubicacion>> cargarCaminosDesdeArchivo(String rutaArchivo,
            ArrayList<Ubicacion> ubicaciones) {
        HashMap<String, ArrayList<Ubicacion>> caminos = new HashMap<>();
        for (Ubicacion ubicacion : ubicaciones) {
            caminos.put(ubicacion.getUbigeo(), new ArrayList<>());
        }
        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    String[] valores = linea.split(" => ");
                    String ubigeoOrigen = valores[0].trim();
                    String ubigeoDestino = valores[1].trim();
                    Optional<Ubicacion> ubicacionDestino = ubicaciones.stream()
                            .filter(ubicacionSel -> ubicacionSel.getUbigeo().equals(ubigeoDestino)).findFirst();
                    // caminos.get(ubigeoOrigen).add(ubicacionDestino.get());
                    if (ubicacionDestino.isPresent()) {
                        caminos.get(ubigeoOrigen).add(ubicacionDestino.get());
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return caminos;
    }
}
