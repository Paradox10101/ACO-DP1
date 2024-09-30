package com.example.backend.algorithm;

import com.example.backend.Repository.AlmacenRepository;
import com.example.backend.Repository.OficinaRepository;
import com.example.backend.Repository.UbicacionRepository;
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

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private AlmacenRepository almacenRepository;


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
    private double alpha = 1.0;
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
                Ubicacion siguienteUbicacion = seleccionarSiguienteUbicacion(ubicacionActual, ubicacionesVisitadas);
                if(siguienteUbicacion == null) return null;
                double tiempoTranscurrido = tiempos.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo());
                int horas = (int)tiempoTranscurrido;
                int minutos = (int)((tiempoTranscurrido - horas)*60);
                fechaFin = fechaInicio.plusHours(horas+2).plusMinutes(minutos);

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
                fechaInicio = fechaFin;
                tramos.add(tramo);
                ubicacionesVisitadas.add(ubicacionActual);
            }
            return tramos;
        }

    public ArrayList<Tramo> generarTramos(Ubicacion ubicacionOrigen,Ubicacion ubicacionDestino, LocalDateTime fechaInicioPedido, LocalDateTime fechaLimite, Vehiculo vehiculo) {
        ArrayList<Tramo> tramos = new ArrayList<>();
        Set<Ubicacion> ubicacionesVisitadas = new HashSet<>();
        //LocalDateTime fechaInicio = fechaInicioPedido;
        //LocalDateTime fechaFin;
        Ubicacion ubicacionActual = ubicacionOrigen;
        ubicacionesVisitadas.add(ubicacionActual);
        double duracionActual = 0.0;
        double duracionLimite = (Duration.between(fechaInicioPedido,fechaLimite).getSeconds()/3600.0);
        while(true) {
            if(ubicacionActual.getUbigeo().equals(ubicacionDestino.getUbigeo()))break;
            Ubicacion siguienteUbicacion = seleccionarSiguienteUbicacion(ubicacionActual, ubicacionesVisitadas);
            if(siguienteUbicacion == null) return null;
            double tiempoTranscurrido = tiempos.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo());
            duracionActual += tiempoTranscurrido;
            //int horas = (int)tiempoTranscurrido;
            //int minutos = (int)((tiempoTranscurrido - horas)*60);
            //fechaFin = fechaInicio.plusHours(horas+2).plusMinutes(minutos);
            if(duracionActual > duracionLimite){
                return null;
            }
            
            Tramo tramo = new Tramo(ubicacionActual, siguienteUbicacion);
            tramo.setVelocidad((float)(velocidadesTramos.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo()) + vehiculo.getTipoVehiculo().getVelocidad()));
            tramo.setDistancia((float)calcularDistanciaEntreUbicaciones(ubicacionActual, siguienteUbicacion));
            tramo.setDuracion((float)tiempoTranscurrido);
            ubicacionActual = siguienteUbicacion;
            //tramo.setFechaInicio(fechaInicio);
            //tramo.setFechaFin(fechaFin);
            //tramo.setVehiculo(vehiculo);

            //fechaInicio = fechaFin;
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

    public double calcularDistanciaEntreUbicaciones(Ubicacion origen, Ubicacion destino) {

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

    public double calcularDistanciaTramo(Tramo tramo) {
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

    private Vehiculo seleccionarVehiculo(int cantidadPaquetes){
        for(List<Vehiculo> vehiculos : oficinaVehiculos.values()){
            for(Vehiculo vehiculo : vehiculos){
                if((vehiculo.getCapacidadMaxima() - vehiculo.getCapacidadUtilizada()) - cantidadPaquetes >=0)
                    return vehiculo;
            }
        }
        return null;
    }




    private double calcularTiempoEntreUbicaciones(Ubicacion origen, Ubicacion destino){
        double velocidadEntreUbicaciones = obtenerVelocidadEntreUbicaciones(origen, destino);
        double distancia = calcularDistanciaEntreUbicaciones(origen, destino);
        if(velocidadEntreUbicaciones==0)
            System.out.println("VELOCIDAD 0");

        return distancia / (velocidadEntreUbicaciones);
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
            HashMap<String, ArrayList<Ubicacion>> caminos, Pedido pedidoIngresado,
            List<Region> regiones, List<Ubicacion> ubicaciones, List<Vehiculo> vehiculos, List<Almacen> almacenes, int cantidadSolicitada) {
        PlanTransporte planTransporteFinal = new PlanTransporte();
        ArrayList<Ubicacion> ubicacionesAlmacenes= new ArrayList<>();
        ArrayList<Tramo> mejorSolucion = new ArrayList<>();
        for(Almacen almacen: almacenes){
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

        for(int iteracion = 0 ; iteracion < numeroIteraciones ; iteracion++){
            for(int hormiga = 0; hormiga < numeroHormigas; hormiga++){
                ArrayList<Tramo> solucion = generarTramosDestinoAlmacen(pedidoIngresado.getOficinaDestino().getUbicacion(), pedidoIngresado.getFechaRegistro(), fechaMaximaEntrega);
                if (solucion != null && !solucion.isEmpty()) {
                    double costo = calcularCostoSolucion(solucion);
                    if(costo < mejorCosto){
                        mejorCosto = costo;
                        mejorSolucion = new ArrayList<>(solucion);
                    }
                    actualizarFeromonasRuta(solucion, costo);
                }
            }
            evaporarFeromonas();
        }
    //mejorSolucion = new ArrayList<>(Collections.reverse(Arrays.asList(mejorSolucion)));
    Collections.reverse(mejorSolucion);
    int index = 0;
    LocalDateTime fechaInicio = pedidoIngresado.getFechaRegistro();
    LocalDateTime fechaFin = null;
    for (Tramo tramo : mejorSolucion) {
        tramo.setFechaInicio(fechaInicio);
        fechaFin = fechaInicio.plusHours(2 + (long)tramo.getDuracion()).plusMinutes((long)(tramo.getDuracion() - (int)tramo.getDuracion())*60);
        tramo.setFechaFin(fechaFin);
        fechaInicio = fechaFin;
        index++;
    }
    this.tramos.addAll(mejorSolucion);


    System.out.println("CANTIDAD DE PAQUETES:  " + pedidoIngresado.getCantidadPaquetes());

        System.out.println("--------------------------------------------------");
        //System.out.println("Pedido gestionado por un vehiculo de tipo " + vehiculo.getTipoVehiculo().getNombre());
        System.out.println("Pedido gestionado por un vehiculo con capacidad maxima " + pedidoIngresado.getCantidadPaquetes());
        System.out.println("Listado de Tramos Registrados:");
        for (Tramo tramo : mejorSolucion) {
            //System.out.println("ID Tramo: " + tramo.getId_tramo());
            System.out.println("--------------------------------------------------");
            System.out.println("Ubicación Origen - ID: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionOrigen().getUbigeo());
            System.out.println("Ubicación Destino - ID: " + tramo.getubicacionDestino().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionDestino().getUbigeo());
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

    ArrayList<Vehiculo> obtenerVehiculos(int cantidadPaquetes){
        ArrayList<Vehiculo> vehiculosSeleccionados = new ArrayList<>();
        final int[] cantidadPorDespachar = {cantidadPaquetes};
        while(cantidadPorDespachar[0] > 0){
            Optional<Vehiculo> vehiculoDespacho = vehiculos.stream().filter(vehiculoDS ->  vehiculoDS.getTipoVehiculo().getCapacidadMaxima() >= cantidadPorDespachar[0]).min(Comparator.comparingDouble(Vehiculo::getCapacidadMaxima));
            if(vehiculoDespacho.isPresent()){
                vehiculosSeleccionados.add(vehiculoDespacho.get());
                cantidadPorDespachar[0]-=vehiculoDespacho.get().getCapacidadMaxima();
            }
            else{
                Optional<Vehiculo> vehiculoMayorCapacidad = vehiculos.stream()
                        .max(Comparator.comparingDouble(Vehiculo::getCapacidadMaxima));
                if(vehiculoMayorCapacidad.isPresent()){
                    vehiculosSeleccionados.add(vehiculoMayorCapacidad.get());
                    cantidadPorDespachar[0]-=vehiculoMayorCapacidad.get().getCapacidadMaxima();
                }
            }
        }

        return vehiculosSeleccionados;
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

    double calcularCostoSolucion(ArrayList<Tramo>tramos){
        double costo = 0.0;
        for(Tramo tramo : tramos){
            costo += tiempos.get(tramo.getubicacionOrigen().getUbigeo()).get(tramo.getubicacionDestino().getUbigeo());
        }
        return costo;
    }



    private double obtenerVelocidadEntreUbicaciones(Ubicacion origen, Ubicacion destino){
        float velocidad;
        Region regionOrigen = origen.getRegion();
        Region regionDestino = destino.getRegion();
        if(regionOrigen == null || regionDestino == null){
            return 0;
        }
        switch(regionOrigen.getNombre()){
            case"COSTA":
                switch(regionDestino.getNombre()){
                    case "COSTA":
                        velocidad=70;
                        break;
                    case "SIERRA":
                        velocidad=50;
                        break;
                    case "SELVA":
                        velocidad=(50+55)/2;//ES UNA DISTANCIA HARDCODEADA SACADA DE UN PROMEDIO
                        break;
                    default:
                        velocidad=0;
                        break;
                }
                break;
            case"SIERRA":
                switch(regionDestino.getNombre()){
                    case "COSTA":
                        velocidad=50;
                        break;
                    case "SIERRA":
                        velocidad=60;
                        break;
                    case "SELVA":
                        velocidad=55;
                        break;
                    default:
                        velocidad=0;
                        break;
                }
                break;
            case"SELVA":
                switch(regionDestino.getNombre()){
                    case "COSTA":
                        velocidad=(50+55)/2;//ES UNA DISTANCIA HARDCODEADA SACADA DE UN PROMEDIO
                        break;
                    case "SIERRA":
                        velocidad=55;
                        break;
                    case "SELVA":
                        velocidad=65;
                        break;
                    default:
                        velocidad=0;
                        break;
                }
                break;
            default:
                velocidad=0;
                break;
        }

        return  velocidad;
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

    private void actualizarFeromonasRuta(List<Tramo> tramos, double mejorTiempo) {
        double feromonasDeposito = 1.0/mejorTiempo;
        for(Tramo tramo : tramos){
            String ubigeoOrigen = tramo.getubicacionOrigen().getUbigeo();
            String ubigeoDestino = tramo.getubicacionDestino().getUbigeo();
            feromonas.get(ubigeoOrigen).put(ubigeoDestino,feromonas.get(ubigeoOrigen).get(ubigeoDestino)+feromonasDeposito);
        }
    }

    private void evaporarFeromonas(){

        for (String ubigeoOrigen : feromonas.keySet()) {
            for (String ubigeoDestino : feromonas.get(ubigeoOrigen).keySet()) {
                feromonas.get(ubigeoOrigen).put(ubigeoDestino, feromonas.get(ubigeoOrigen).get(ubigeoDestino) * (1 - tasaEvaporacion));
            }
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
/*
    private void evaporarFeromonas() {
        for (Map.Entry<Long, Double> entry : feromonas.entrySet()) {
            Long key = entry.getKey();
            Double value = entry.getValue();
            feromonas.put(key, (1 - tasaEvaporacion) * value);
        }
    }

 */

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
