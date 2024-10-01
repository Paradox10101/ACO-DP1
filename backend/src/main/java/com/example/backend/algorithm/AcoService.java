package com.example.backend.algorithm;

//import com.example.backend.Repository.AlmacenRepository;
//import com.example.backend.Repository.OficinaRepository;
import com.example.backend.Repository.PedidoRepository;
//import com.example.backend.Repository.UbicacionRepository;
import com.example.backend.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AcoService {

    /*@Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private AlmacenRepository almacenRepository;*/

    @Autowired
    private PedidoRepository pedidoRepository;

    private List<Tramo> tramos = new ArrayList<>();
    private List<Oficina> oficinas;
    private List<Ubicacion> ubicaciones;
    private List<Vehiculo> vehiculos;
    private List<Almacen> almacenes;
    private Map<String, ArrayList<Ubicacion>> caminos;
    private Map<String, Map<String, Double>> feromonas;
    private Map<String, Map<String, Double>> velocidadesTramos;
    private Map<String, List<Vehiculo>> oficinaVehiculos = new HashMap<>();
    private Map<String, Map<String, Double>> tiempos;

    private int numeroHormigas = 150;
    private int numeroIteraciones = 150;
    private double tasaEvaporacion = 0.5;
    private double feromonaInicial = 1.0;
    private double alpha = 1.5;
    private double beta = 2.0;
    private Random random = new Random();

    @Autowired
    private SystemMetricsAutoConfiguration systemMetricsAutoConfiguration;


    public AcoService() {

    }

    private void inicializarFeromonas(){
        feromonas = new HashMap<>();

        for (String ubigeoOrigen : caminos.keySet()) {
            feromonas.put(ubigeoOrigen, new HashMap<>());
            for (Ubicacion ubicacionDestino : caminos.get(ubigeoOrigen)) {
                feromonas.get(ubigeoOrigen).put(ubicacionDestino.getUbigeo(), feromonaInicial);
            }
        }
    }


    private void inicializarVelocidadesTramos(){
        velocidadesTramos = new HashMap<>();
        for(String ubigeoOrigen: caminos.keySet()) {
            velocidadesTramos.put(ubigeoOrigen, new HashMap<>());
            Optional<Ubicacion> ubicacionSeleccionada = ubicaciones.stream()
                    .filter(ubicacionS -> ubicacionS.getUbigeo().equals(ubigeoOrigen))
                    .findFirst();

            if(ubicacionSeleccionada.isPresent()){
                for (Ubicacion ubicacionDestino : caminos.get(ubigeoOrigen)) {
                    velocidadesTramos.get(ubigeoOrigen).put(ubicacionDestino.getUbigeo(),obtenerVelocidadEntreUbicaciones((ubicacionSeleccionada.get()), ubicacionDestino));
                }
            }
        }
    }

    private void inicializarTiempos(){
        tiempos = new HashMap<>();
        for (String ubigeoOrigen : caminos.keySet()) {
            tiempos.put(ubigeoOrigen, new HashMap<>());
            Optional<Ubicacion> ubicacionSeleccionada = ubicaciones.stream()
                    .filter(ubicacionS -> ubicacionS.getUbigeo().equals(ubigeoOrigen))
                    .findFirst();
            if (ubicacionSeleccionada.isPresent()) {
                for (Ubicacion ubicacionDestino : caminos.get(ubigeoOrigen)) {
                    tiempos.get(ubicacionSeleccionada.get().getUbigeo()).put(ubicacionDestino.getUbigeo(), calcularTiempoEntreUbicaciones(ubicacionSeleccionada.get(),ubicacionDestino));
                }
            }
        }

    }

    private void inicializarOficinasVehiculos(){
        oficinaVehiculos = new HashMap<>();
        for(Oficina oficina: oficinas) {
            oficinaVehiculos.put(oficina.getUbicacion().getUbigeo(), vehiculos.stream()
                .filter(vehiculoS -> vehiculoS.getUbicacionActual().getUbigeo().equals(oficina.getUbicacion().getUbigeo()))
                .collect(Collectors.toCollection(ArrayList::new)));
        }
    }



    public ArrayList<Tramo> generarTramosDestinoAlmacen(Ubicacion ubicacionDestino, LocalDateTime fechaInicioPedido, LocalDateTime fechaLimite) {
        ArrayList<Tramo> tramos = new ArrayList<>();
        ArrayList<String> ubigeosAlmacenes = new ArrayList<>();
        for(Almacen almacen: almacenes){
            ubigeosAlmacenes.add(almacen.getUbicacion().getUbigeo());
        }
        Set<Ubicacion> ubicacionesVisitadas = new HashSet<>();
        LocalDateTime fechaInicio = fechaInicioPedido;
        LocalDateTime fechaFin;
        Ubicacion ubicacionActual = ubicacionDestino;
        ubicacionesVisitadas.add(ubicacionActual);

        while(true) {
            if(ubigeosAlmacenes.contains(ubicacionActual.getUbigeo())){
                break;
            }
            // Verificar si existe un tramo directo desde la ubicación actual a un almacén
            Optional<Ubicacion> tramoDirecto = caminos.get(ubicacionActual.getUbigeo()).stream()
                                    .filter(ubicacion -> ubigeosAlmacenes.contains(ubicacion.getUbigeo()))
                                    .findFirst();

            if (tramoDirecto.isPresent()) {
                Ubicacion siguienteUbicacion = tramoDirecto.get();
                double tiempoTranscurrido = tiempos.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo());
                int horas = (int) tiempoTranscurrido;
                int minutos = (int) ((tiempoTranscurrido - horas) * 60);
                fechaFin = fechaInicio.plusHours(horas + 2).plusMinutes(minutos);
    
                if (fechaFin.isAfter(fechaLimite)) {
                    return null; // Retornar null si el tramo directo excede la fecha límite
                }
    
                Tramo tramo = new Tramo(ubicacionActual, siguienteUbicacion);
                tramo.setVelocidad((float) (1.0 * velocidadesTramos.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo())));
                tramo.setDistancia((float) calcularDistanciaEntreUbicaciones(ubicacionActual, siguienteUbicacion));
                tramo.setDuracion((float) tiempoTranscurrido);
    
                tramos.add(tramo);
                break; // Terminar la búsqueda porque ya encontramos un tramo directo al almacén
            }

            Ubicacion siguienteUbicacion = seleccionarSiguienteUbicacion(ubicacionActual, ubicacionesVisitadas);
            if(siguienteUbicacion == null) return null;
            double tiempoTranscurrido = tiempos.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo());
            int horas = (int)tiempoTranscurrido;
            int minutos = (int)((tiempoTranscurrido - horas)*60);
            fechaFin = fechaInicio.plusHours(horas).plusMinutes(minutos);

            if(fechaFin.isAfter(fechaLimite)){
                return null;
            }

            Tramo tramo = new Tramo(ubicacionActual, siguienteUbicacion);
            tramo.setVelocidad((float)(1.0*velocidadesTramos.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo())));
            tramo.setDistancia((float)calcularDistanciaEntreUbicaciones(ubicacionActual, siguienteUbicacion));
            tramo.setDuracion((float) tiempoTranscurrido);
            //tramo.setFechaInicio(fechaInicio);
            //tramo.setFechaFin(fechaFin);
            ubicacionActual = siguienteUbicacion;
            fechaInicio = fechaFin.plusHours(2);
            tramos.add(tramo);
            ubicacionesVisitadas.add(ubicacionActual);
        }
        return tramos;
    }



    private Ubicacion seleccionarSiguienteUbicacion(Ubicacion ubicacionActual, Set<Ubicacion> ubicacionesVisitadas){
        Map<String, Double> tiemposPorUbicacion = tiempos.get(ubicacionActual.getUbigeo());
        Map<String, Double> probabilidades = new HashMap<>();
        double total = 0.0;

        for(String ubigeoUbicacionDestino : tiemposPorUbicacion.keySet()) {
            Optional<Ubicacion>ubicacionDestino = ubicaciones.stream().filter(ubicacionS -> ubicacionS.getUbigeo().equals(ubigeoUbicacionDestino)).findFirst();
            if(ubicacionDestino.isPresent() && !ubicacionesVisitadas.contains(ubicacionDestino.get())) {
                double tiempo = tiemposPorUbicacion.get(ubicacionDestino.get().getUbigeo());
                double probabilidad = Math.pow(feromonas.get(ubicacionActual.getUbigeo()).get(ubicacionDestino.get().getUbigeo()), alpha) *
                        Math.pow(1.0 / tiempo, beta);
                probabilidades.put(ubicacionDestino.get().getUbigeo(), probabilidad);
                total += probabilidad;

            }
        }

        double randValue = random.nextDouble() * total;
        double valorAcumulado = 0.0;

        for (Map.Entry<String, Double> entry  : probabilidades.entrySet()){
            valorAcumulado += entry.getValue();
            if (valorAcumulado >= randValue) {
                Optional<Ubicacion>ubicacionSeleccionada = ubicaciones.stream().filter(ubicacionSel -> ubicacionSel.getUbigeo().equals(entry.getKey())).findFirst();
                if(ubicacionSeleccionada.isPresent()){
                    return ubicacionSeleccionada.get();
                }
                else
                    return null;
            }
        }
        return null;
    }

    public double calcularDistanciaEntreUbicaciones (Ubicacion origen, Ubicacion destino){

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

    public double calcularDistanciaTramo (Tramo tramo){
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

    private Vehiculo seleccionarVehiculo ( int cantidadPaquetes){
        for (List<Vehiculo> vehiculos : oficinaVehiculos.values()) {
            for (Vehiculo vehiculo : vehiculos) {
                if ((vehiculo.getCapacidadMaxima() - vehiculo.getCapacidadUtilizada()) - cantidadPaquetes >= 0)
                    return vehiculo;
            }
        }
        return null;
    }


    private double calcularTiempoEntreUbicaciones (Ubicacion origen, Ubicacion destino){
        double velocidadEntreUbicaciones = obtenerVelocidadEntreUbicaciones(origen, destino);

        double distancia = calcularDistanciaEntreUbicaciones(origen, destino);
        if (velocidadEntreUbicaciones == 0)
            System.out.println("VELOCIDAD 0");

        return distancia / (velocidadEntreUbicaciones);
    }

    private double calcularDistanciaEntrePuntos ( double lat1, double lon1, double lat2, double lon2){
        final int RADIO_TIERRA = 6371; // Radio de la Tierra en km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA * c; // Retorna la distancia en kilómetros
    }

    private double calcularDistanciaMinima (Ubicacion actual, Ubicacion destino,
            HashMap < String, ArrayList < Ubicacion >> caminos,double distanciaAcumulada, Set<
    String > visitados){
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

    private void simularTiempo (LocalDateTime fechaInicio, LocalDateTime fechaFin,int escala){
        long duracionEnMinutos = java.time.Duration.between(fechaInicio, fechaFin).toMillis();

        // Ajusta la duración según la escala
        long tiempoEnMilisegundos = (duracionEnMinutos) / escala;

        // Imprime información sobre el tramo actual
        System.out.println("Simulando tramo de " + fechaInicio + " a " + fechaFin + " con escala x" + escala);

        try {
            Thread.sleep(tiempoEnMilisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public PlanTransporte ejecutar (List < Oficina > oficinas,
            HashMap < String, ArrayList < Ubicacion >> caminos, Pedido pedidoIngresado,
            List < Region > regiones, List < Ubicacion > ubicaciones, List < Vehiculo > vehiculos, List < Almacen > almacenes,
            int cantidadSolicitada){

        PlanTransporte planTransporteFinal = new PlanTransporte();
        ArrayList<Ubicacion> ubicacionesAlmacenes = new ArrayList<>();
        ArrayList<Tramo> mejorSolucion = new ArrayList<>();
        for (Almacen almacen : almacenes) {
            ubicacionesAlmacenes.add(almacen.getUbicacion());
        }
        double mejorCosto = Double.MAX_VALUE;
        this.caminos = caminos;
        this.ubicaciones = ubicaciones;
        this.vehiculos = vehiculos;
        this.oficinas = oficinas;
        this.almacenes = almacenes;

        inicializarFeromonas();
        inicializarVelocidadesTramos();
        inicializarOficinasVehiculos();
        inicializarTiempos();

        LocalDateTime fechaMaximaEntrega = pedidoIngresado.getFechaRegistro().plusDays(pedidoIngresado.getOficinaDestino().getUbicacion().getRegion().getDiasLimite());

        for (int iteracion = 0; iteracion < numeroIteraciones; iteracion++) {
            for (int hormiga = 0; hormiga < numeroHormigas; hormiga++) {
                ArrayList<Tramo> solucion = generarTramosDestinoAlmacen(pedidoIngresado.getOficinaDestino().getUbicacion(), pedidoIngresado.getFechaRegistro(), fechaMaximaEntrega);
                if (solucion != null && !solucion.isEmpty()) {
                    double costo = calcularCostoSolucion(solucion);
                    if (costo < mejorCosto) {
                        mejorCosto = costo;
                        mejorSolucion = new ArrayList<>(solucion);
                    }
                    actualizarFeromonasRuta(solucion, costo);
                }
            }
            evaporarFeromonas();
        }

        Collections.reverse(mejorSolucion);
        for(Tramo tramo : mejorSolucion) {
            Ubicacion aux = tramo.getubicacionOrigen();
            tramo.setubicacionOrigen(tramo.getubicacionDestino());
            tramo.setubicacionDestino(aux);
        }


        LocalDateTime fechaInicio = pedidoIngresado.getFechaRegistro();
        LocalDateTime fechaFin = null;
        for (Tramo tramo : mejorSolucion) {
            tramo.setFechaInicio(fechaInicio);
            fechaFin = fechaInicio.plusHours(2 + (long) tramo.getDuracion()).plusMinutes((long)((tramo.getDuracion() - (int) tramo.getDuracion()) * 60));
            tramo.setFechaFin(fechaFin);
            fechaInicio = fechaFin;

        }

        this.tramos.addAll(mejorSolucion);
        Vehiculo vehiculoSeleccionado = obtenerVehiculo(cantidadSolicitada, mejorSolucion.get(0).getubicacionOrigen().getUbigeo());
        if(vehiculoSeleccionado==null) {
            return planTransporteFinal;
        }

        planTransporteFinal.setVehiculo(vehiculoSeleccionado);
        for(Tramo tramos: mejorSolucion){
            tramos.setVehiculo(vehiculoSeleccionado);
            tramos.setCantidadPaquetes(cantidadSolicitada);
        }



        System.out.println("CANTIDAD DE PAQUETES:  " + pedidoIngresado.getCantidadPaquetes());
        System.out.println("--------------------------------------------------");
        System.out.println("Pedido gestionado por un vehiculo de tipo " + planTransporteFinal.getVehiculo().getTipoVehiculo().getNombre());
        System.out.println("Cantidad de paquetes transportados " + planTransporteFinal.getVehiculo().getCapacidadUtilizada());
        System.out.println("Pedido gestionado por un vehiculo con capacidad maxima " + planTransporteFinal.getVehiculo().getCapacidadMaxima());
        System.out.println("Listado de Tramos Registrados:");
        for (Tramo tramo : mejorSolucion) {
            
            // Verificar la posibilidad de una avería durante el trayecto del tramo
            if (Math.random() < 0.9) { // Supongamos una probabilidad del 10% de avería para cada tramo
                TipoAveria tipoAveria = TipoAveria.values()[new Random().nextInt(TipoAveria.values().length)];
                vehiculoSeleccionado.registrarAveria(tipoAveria, LocalDateTime.now());
                System.out.println("Vehículo " + vehiculoSeleccionado.getCodigo() + " ha sufrido una avería de tipo: " + tipoAveria);
                
                // Verificar si el vehículo está inoperativo y si debe cambiar el tramo
                if (!vehiculoSeleccionado.verificarDisponibilidad(tramo.getFechaFin())) {
                    System.out.println("Vehículo inoperativo. Tramo interrumpido.");
                    break; // Interrumpir el proceso si el vehículo no puede continuar
                }
            }
            tramo.setVehiculo(vehiculoSeleccionado);
            tramo.setCantidadPaquetes(cantidadSolicitada);

            System.out.println("--------------------------------------------------");
            System.out.println("Ubicación Origen - ID: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionOrigen().getUbigeo());
            System.out.println("Region Origen: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionOrigen().getRegion().getNombre());
            System.out.println("Ubicación Destino - ID: " + tramo.getubicacionDestino().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionDestino().getUbigeo());
            System.out.println("Region Destino: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionDestino().getRegion().getNombre());
            System.out.println("Distancia: " + tramo.getDistancia() + " km");
            System.out.println("Velocidad: " + tramo.getVelocidad() + " km/h");
            System.out.println("Fecha Inicio Recorrido: " + tramo.getFechaInicio().getDayOfMonth() + "/" + tramo.getFechaInicio().getMonthValue() + "/" + tramo.getFechaInicio().getYear() + " " + tramo.getFechaInicio().getHour() + "h:" + tramo.getFechaInicio().getMinute() + "m");
            System.out.println("Fecha Fin Recorrido: " + tramo.getFechaFin().getDayOfMonth() + "/" + tramo.getFechaFin().getMonthValue() + "/" + tramo.getFechaFin().getYear() + " " + tramo.getFechaFin().getHour() + "h:" + tramo.getFechaFin().getMinute() + "m");
            //System.out.println("--------------------------------------------------");
            //System.out.println("Bloqueado: " + (tramo.isBloqueado() ? "Sí" : "No"));
        }
        System.out.println("--------------------------------------------------");
        System.out.println();
        System.out.println();
        System.out.println();

        return planTransporteFinal;
    }


    Vehiculo obtenerVehiculo(int cantidadPaquetes, String ubigeoAlmacen){
        final int[] cantidadPorDespachar = {cantidadPaquetes};
        Optional<Almacen> almacenSeleccionado = almacenes.stream().filter(almacenS -> almacenS.getUbicacion().getUbigeo().equals(ubigeoAlmacen)).findFirst();
        if(!almacenSeleccionado.isPresent())return null;
        
        Optional<Vehiculo> vehiculoDespacho = vehiculos.stream()
                .filter(vehiculoDS ->  vehiculoDS.getTipoVehiculo().getCapacidadMaxima() >= cantidadPorDespachar[0] 
                && vehiculoDS.getUbicacionActual().getUbigeo().equals(ubigeoAlmacen)
                && vehiculoDS.isDisponible()
                    && vehiculoDS.verificarDisponibilidad(LocalDateTime.now())) // Verifica si está disponible y libre de averías)
                .min(Comparator.comparing(vehiculoDS -> vehiculoDS.getTipoVehiculo().getCapacidadMaxima()));
        
        if(vehiculoDespacho.isPresent()){
            vehiculoDespacho.get().setEstado(EstadoVehiculo.EnRuta);
            vehiculoDespacho.get().setAlmacen(null);
            vehiculoDespacho.get().setCapacidadUtilizada(cantidadPaquetes);
            almacenSeleccionado.get().setCantidadVehiculos(almacenSeleccionado.get().getCantidadVehiculos()-1);
            return vehiculoDespacho.get();
        }
        else{
            Optional<Vehiculo> vehiculoMayorCapacidad = vehiculos.stream()
                    .filter(vehiculoDS ->  vehiculoDS.getTipoVehiculo().getCapacidadMaxima() < cantidadPorDespachar[0] 
                    && vehiculoDS.getUbicacionActual().getUbigeo().equals(ubigeoAlmacen)
                    && vehiculoDS.isDisponible()
                        && vehiculoDS.verificarDisponibilidad(LocalDateTime.now())) //Aqui se verifica si los vehiculos estan disponibles y si es que es para el momento actual
                    .max(Comparator.comparing(vehiculoDS -> vehiculoDS.getTipoVehiculo().getCapacidadMaxima()));
            
            if(vehiculoMayorCapacidad.isPresent()){
                vehiculoMayorCapacidad.get().setEstado(EstadoVehiculo.EnRuta);
                vehiculoMayorCapacidad.get().setAlmacen(null);
                vehiculoMayorCapacidad.get().setCapacidadUtilizada(vehiculoMayorCapacidad.get().getCapacidadMaxima());
                almacenSeleccionado.get().setCantidadVehiculos(almacenSeleccionado.get().getCantidadVehiculos()-1);
                return vehiculoMayorCapacidad.get();
            }
        }
        return null;
    }

    /*
    Almacen seleccionarAlmacenOrigen(Vehiculo vehiculo){
        double totalFeromonas = 0.0;
        for (Almacen almacen : almacenes) {
            totalFeromonas += feromonas.get(vehiculo).get(almacen.getUbicacion().getUbigeo()).get("inicial");
        }
        double random = Math.random() * totalFeromonas;
        double acumulado = 0.0;

        for (Almacen almacen : almacenes) {
            acumulado += feromonas.get(vehiculo).get(almacen.getUbicacion().getUbigeo()).get("inicial");
            if (acumulado >= random) {
                return almacen;
            }
        }
        return null;
    }
    */

    double calcularCostoSolucion (ArrayList < Tramo > tramos) {
        double costo = 0.0;
        for (Tramo tramo : tramos) {
            costo += tramo.getDuracion() + 2;
        }
        return costo;
    }


    private double obtenerVelocidadEntreUbicaciones (Ubicacion origen, Ubicacion destino){
        float velocidad;
        Region regionOrigen = origen.getRegion();
        Region regionDestino = destino.getRegion();
        if (regionOrigen == null || regionDestino == null) {
            return 0;
        }
        switch (regionOrigen.getNombre()) {
            case "COSTA":
                switch (regionDestino.getNombre()) {
                    case "COSTA":
                        velocidad = 70;
                        break;
                    case "SIERRA":
                        velocidad = 50;
                        break;
                    case "SELVA":
                        velocidad = (50 + 55) / 2;//ES UNA DISTANCIA HARDCODEADA SACADA DE UN PROMEDIO
                        break;
                    default:
                        velocidad = 0;
                        break;
                }
                break;
            case "SIERRA":
                switch (regionDestino.getNombre()) {
                    case "COSTA":
                        velocidad = 50;
                        break;
                    case "SIERRA":
                        velocidad = 60;
                        break;
                    case "SELVA":
                        velocidad = 55;
                        break;
                    default:
                        velocidad = 0;
                        break;
                }
                break;
            case "SELVA":
                switch (regionDestino.getNombre()) {
                    case "COSTA":
                        velocidad = (50 + 55) / 2;//ES UNA DISTANCIA HARDCODEADA SACADA DE UN PROMEDIO
                        break;
                    case "SIERRA":
                        velocidad = 55;
                        break;
                    case "SELVA":
                        velocidad = 65;
                        break;
                    default:
                        velocidad = 0;
                        break;
                }
                break;
            default:
                velocidad = 0;
                break;
        }

        return velocidad;
    }


    private void actualizarFeromonasRuta (List < Tramo > tramos,double mejorTiempo){
        double feromonasDeposito = 1.0 / mejorTiempo;
        for (Tramo tramo : tramos) {
            String ubigeoOrigen = tramo.getubicacionOrigen().getUbigeo();
            String ubigeoDestino = tramo.getubicacionDestino().getUbigeo();
            feromonas.get(ubigeoOrigen).put(ubigeoDestino, feromonas.get(ubigeoOrigen).get(ubigeoDestino) + feromonasDeposito);
        }
    }

    private void evaporarFeromonas () {

        for (String ubigeoOrigen : feromonas.keySet()) {
            for (String ubigeoDestino : feromonas.get(ubigeoOrigen).keySet()) {
                feromonas.get(ubigeoOrigen).put(ubigeoDestino, feromonas.get(ubigeoOrigen).get(ubigeoDestino) * (1 - tasaEvaporacion));
            }
        }

    }

    private double calcularDuracionTramo (Tramo tramo){
        LocalDateTime fechaHoraSalida = tramo.getFechaInicio();
        LocalDateTime fechaHoraLlegada = tramo.getFechaFin();

        // Calcular la duración del tramo en minutos
        long duracionEnMinutos = fechaHoraSalida.until(fechaHoraLlegada, java.time.temporal.ChronoUnit.MINUTES);

        // Convertir la duración a horas
        double duracionEnHoras = (double) duracionEnMinutos / 60.0;

        return duracionEnHoras;
    }

    public HashMap<String, ArrayList<Ubicacion>> cargarCaminosDesdeArchivo (String rutaArchivo,
            ArrayList < Ubicacion > ubicaciones){
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