package com.example.backend.algorithm;

import com.example.backend.Repository.AlmacenRepository;
import com.example.backend.Repository.OficinaRepository;
import com.example.backend.Repository.UbicacionRepository;
import com.example.backend.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AcoService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    


    private List<Tramo> tramos;
    private List<Oficina> oficinas;
    private List<Ubicacion> ubicaciones;
    private List<Vehiculo> vehiculos;
    private Pedido pedido;
    private Map<String, ArrayList<Ubicacion>> caminos;
    private Map<String, Map<String, Double>> feromonas;
    private Map<String, Map<String, Double>> distancias;
    private Map<String, Map<String, Double>> velocidadesTramos;

    private int numeroHormigas = 15;
    private int numeroIteraciones = 100;
    private double tasaEvaporacion = 0.5;
    private double feromonaInicial = 1.0;
    private double alpha = 1.0;
    private double beta = 2.0;
    private Random random = new Random();


    public AcoService() {

    }

    public void inicializarFeromonas(){
        feromonas = new HashMap<>();
        for(String ubigeoOrigen: caminos.keySet()) {
            feromonas.put(ubigeoOrigen, new HashMap<>());
            for (Ubicacion ubicacionDestino : caminos.get(ubigeoOrigen)) {
                feromonas.get(ubigeoOrigen).put(ubicacionDestino.getUbigeo(), feromonaInicial);
            }
        }
    }

    public void inicializarDistancias(){
        distancias = new HashMap<>();
        for(String ubigeoOrigen: caminos.keySet()) {
            distancias.put(ubigeoOrigen, new HashMap<>());
            Optional<Ubicacion> ubicacionSeleccionada = ubicaciones.stream()
                    .filter(ubicacionS -> ubicacionS.getUbigeo().equals(ubigeoOrigen))
                    .findFirst();
            if(ubicacionSeleccionada.isPresent()){
                for (Ubicacion ubicacionDestino : caminos.get(ubigeoOrigen)) {
                    distancias.get(ubigeoOrigen).put(ubicacionDestino.getUbigeo(), calcularDistanciaEntreUbicaciones(ubicacionSeleccionada.get(), ubicacionDestino));
                }
            }
        }
    }

public void inicializarVelocidadesTramos(){
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
            //double feromona = feromonas.getOrDefault(tramo.getId_tramo(), 0.0);// Intensidad de uso de ese tramo por
                                                                               // hormigas
            double heuristica = calcularHeuristica(tramo, fechaHoraLlegadaAnterior);
            //double valor = Math.pow(feromona, alpha) * Math.pow(heuristica, beta);
            //probabilidades.put(tramo, valor);
            //total += valor;
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

    public ArrayList<Tramo> generarTramos(Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino, LocalDateTime fechaInicioPedido, LocalDateTime fechaLimite) {
        ArrayList<Tramo> tramos = new ArrayList<>();
        Set<Ubicacion> ubicacionesVisitadas = new HashSet<>();
        Ubicacion ubicacionActual = ubicacionOrigen;
        ubicacionesVisitadas.add(ubicacionActual);
        double distanciaEntreUbicaciones;
        double velocidadEntreUbicaciones;
        LocalDateTime fechaInicio = fechaInicioPedido;
        LocalDateTime fechaFin;
        while(!ubicacionActual.getUbigeo().equals(ubicacionDestino.getUbigeo()) && tramos.size() < caminos.keySet().size() - 1) {
            Ubicacion siguienteUbicacion = seleccionarSiguienteUbicacion(ubicacionActual, ubicacionesVisitadas);
            
            if(siguienteUbicacion == null) break;
            
            distanciaEntreUbicaciones = distancias.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo());
            velocidadEntreUbicaciones = velocidadesTramos.get(ubicacionActual.getUbigeo()).get(siguienteUbicacion.getUbigeo());
            Long horasTranscurridas = (long)(distanciaEntreUbicaciones/velocidadEntreUbicaciones);
            Long minutosTranscurridos = (long)(((distanciaEntreUbicaciones/velocidadEntreUbicaciones) - (int)(distanciaEntreUbicaciones/velocidadEntreUbicaciones)))*60;

            fechaFin = fechaInicio.plusHours(horasTranscurridas).plusMinutes(minutosTranscurridos);
            if(fechaFin.isAfter(fechaLimite)) break;
            
            Tramo tramo = new Tramo(ubicacionActual, siguienteUbicacion);
            tramo.setFechaInicio(fechaInicio);
            tramo.setFechaFin(fechaFin);
            
            ubicacionActual = siguienteUbicacion;
            
            fechaInicio = fechaFin;
            tramos.add(tramo);
            ubicacionesVisitadas.add(ubicacionActual);
        }

        return tramos;
    }

    private Ubicacion seleccionarSiguienteUbicacion(Ubicacion ubicacionActual, Set<Ubicacion> ubicacionesVisitadas){
        Map<Ubicacion, Double> probabilidades = new HashMap<>();
        double total = 0.0;

        for(Ubicacion destino : caminos.get(ubicacionActual.getUbigeo())) {
            if(!ubicacionesVisitadas.contains(destino)) {
                double probabilidad = Math.pow(feromonas.get(ubicacionActual.getUbigeo()).get(destino.getUbigeo()), alpha)*
                        Math.pow(distancias.get(ubicacionActual.getUbigeo()).get(destino.getUbigeo()), beta);
                probabilidades.put(destino, probabilidad);
                total += probabilidad;
            }

        }

        double randValue = random.nextDouble() * total;
        for (Ubicacion ubicacion : probabilidades.keySet()){
            randValue -= probabilidades.get(ubicacion);
            if(randValue<=0.0){
                return ubicacion;
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
            List<Region> regiones, List<Ubicacion> ubicaciones, List<Vehiculo> vehiculos) {
        ArrayList<Tramo> mejorSolucion = null;
        PlanTransporte planTransporteFinal = new PlanTransporte();
        double mejorCosto = Double.MAX_VALUE;
        this.oficinas = oficinas;
        this.caminos = caminos;
        this.ubicaciones = ubicaciones;
        this.vehiculos = vehiculos;
        boolean solutionFound = false;
        inicializarDistancias();
        inicializarFeromonas();
        inicializarVelocidadesTramos();
        LocalDateTime fechaMaximaEntrega = calcularFechaMaximaEntregaDestino(pedidoIngresado, oficinas, regiones); // Calcula la fecha máxima de entrega en destino
        //List<Tramo> tramos = generarTramosDesdeCaminos(caminos, ubicaciones);
        
        

        for(int iteracion = 0 ; iteracion < numeroIteraciones ; iteracion++){
            for(int hormiga = 0; hormiga < numeroHormigas; hormiga++){
                ArrayList<Tramo> solucion = generarTramos(vehiculos.get(0).getUbicacionActual(), pedidoIngresado.getOficinaDestino().getUbicacion(), LocalDateTime.now(), fechaMaximaEntrega);
                double costo = calcularCostoSolucion(solucion);
                if(costo < mejorCosto){
                    mejorCosto = costo;
                    mejorSolucion = new ArrayList<>(solucion);
                }
                actualizarFeromonasRuta(solucion, costo);
            }
            evaporarFeromonas();
        }
        this.tramos = mejorSolucion;
        System.out.println("Listado de Tramos Registrados:");
        System.out.println("--------------------------------------------------");

        for (Tramo tramo : this.tramos) {
            //System.out.println("ID Tramo: " + tramo.getId_tramo());
            System.out.println("Ubicación Origen - ID: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionOrigen().getUbigeo());
            System.out.println("Ubicación Destino - ID: " + tramo.getubicacionDestino().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionDestino().getUbigeo());
            System.out.println("Distancia: " + tramo.getDistancia() + " km");
            System.out.println("Fecha Inicio Recorrido: " + tramo.getFechaInicio().getDayOfMonth() + "/" + tramo.getFechaInicio().getMonthValue() + "/" + tramo.getFechaInicio().getYear() + " " + tramo.getFechaInicio().getHour() + "h:" + tramo.getFechaInicio().getMinute() + "m");
            System.out.println("Fecha Fin Recorrido: " + tramo.getFechaFin().getDayOfMonth() + "/" + tramo.getFechaFin().getMonthValue() + "/" + tramo.getFechaFin().getYear() + " " + tramo.getFechaFin().getHour() + "h:" + tramo.getFechaFin().getMinute() + "m");
            //System.out.println("Bloqueado: " + (tramo.isBloqueado() ? "Sí" : "No"));
            System.out.println("--------------------------------------------------");
        }



        return planTransporteFinal;
    }


    double calcularCostoSolucion(ArrayList<Tramo>tramos){
        double costo = 0.0;
        for(Tramo tramo : tramos){
            costo += distancias.get(tramo.getubicacionOrigen().getUbigeo()).get(tramo.getubicacionDestino().getUbigeo());
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

    private void actualizarFeromonasRuta(List<Tramo> tramos, double costo) {
        double feromonasDeposito = 1.0/costo;
        for(Tramo tramo : tramos){
            String ubigeoOrigen = tramo.getubicacionOrigen().getUbigeo();
            String ubigeoDestino = tramo.getubicacionDestino().getUbigeo();
            feromonas.get(ubigeoOrigen).put(ubigeoDestino,feromonas.get(ubigeoOrigen).get(ubigeoDestino)+feromonasDeposito);
        }
    }

    private void evaporarFeromonas(){
        for(String ubigeoOrigen : feromonas.keySet()){
            for(String ubigeoDestino : feromonas.get(ubigeoOrigen).keySet()){
                feromonas.get(ubigeoOrigen).put(ubigeoDestino, feromonas.get(ubigeoOrigen).get(ubigeoDestino)*(1-tasaEvaporacion));
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
