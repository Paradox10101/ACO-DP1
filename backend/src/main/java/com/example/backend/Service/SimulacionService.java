package com.example.backend.Service;

import com.example.backend.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;

import com.sun.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.sun.management.OperatingSystemMXBean;

@Service
public class SimulacionService {
    @Autowired
    private RegionService regionService;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private FilesService filesService;
    @Autowired
    private TramoService tramoService;
    @Autowired
    private VehiculoService vehiculoService;
    @Autowired
    private PlanTransporteService planTransporteService;



    public SimulacionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void simulacionSemanal(LocalDateTime fechaInicioSimulacion){
        //LocalDateTime fechaFinSimulacion = fechaInicioSimulacion.plusDays(1);
        LocalDateTime fechaFinSimulacion = fechaInicioSimulacion.plusHours(72);
        int minutesIncrement = 60*24;
        ArrayList<Oficina> oficinas;
        ArrayList<Pedido> pedidos;
        ArrayList<Bloqueo> bloqueos;
        ArrayList<Mantenimiento> mantenimientos;
        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();
        ArrayList<Region> regiones = new ArrayList<Region>(); // Es hardcodeado
        ArrayList<Vehiculo> vehiculos = new ArrayList<Vehiculo>(); // Es hardcodeado
        ArrayList<Almacen> almacenes = new ArrayList<Almacen>();
        ArrayList<TipoVehiculo> tiposVehiculo = new ArrayList<>();
        ArrayList<Cliente> clientes = new ArrayList<>();
        ArrayList<Paquete> paquetes = new ArrayList<>();
        HashMap<String, ArrayList<Ubicacion>> caminos;

        regionService.guardar(new Region("COSTA", 1));
        regionService.guardar(new Region("SIERRA", 2));
        regionService.guardar(new Region("SELVA", 3));
        regiones = regionService.obtenerTodas();

        oficinas = filesService.cargarOficinasDesdeBD("dataset/Oficinas/c.1inf54.24-2.oficinas.v1.0.txt", regiones, ubicaciones);
        vehiculos = filesService.cargarVehiculosAlmacenesDesdeArchivo("dataset/Vehiculos/vehiculos.txt",almacenes, vehiculos, ubicaciones, tiposVehiculo);
        caminos = filesService.cargarCaminosDesdeArchivo("dataset/Tramos/c.1inf54.24-2.tramos.v1.0.txt", ubicaciones);
        //tramos = Tramo.cargarTramosDesdeArchivo("dataset/Tramos/c.1inf54.24-2.tramos.v1.0.txt", caminos);
        bloqueos = filesService.cargarBloqueosDesdeArchivo("dataset/Bloqueos/c.1inf54.24-2.bloqueo.04.txt", ubicaciones);
        //bloqueos = filesService.cargarBloqueosDesdeDirectorio("dataset/Bloqueos", ubicaciones);
        pedidos = pedidoService.cargarPedidosDesdeArchivo("dataset/Pedidos/c.1inf54.ventas202404.txt", oficinas, ubicaciones, clientes, paquetes);
        //pedidos = filesService.cargarPedidosDesdeDirectorio("dataset/Pedidos", oficinas, ubicaciones, clientes, paquetes);
        //pedidos = pedidoService.cargarPedidosDesdeArchivo("dataset/Pedidos/c.1inf54.ventas202409.txt", oficinas, ubicaciones, clientes, paquetes);
        mantenimientos = filesService.cargarMantenimientosDesdeArchivo("dataset/Mantenimientos/c.1inf54.24-2.plan.mant.2024.trim.abr.may.jun.txt", vehiculos);
        //mantenimientos = filesService.cargarMantenimientosDesdeDirectorio("dataset/Mantenimientos", vehiculos);
        LocalDateTime fechaActualSimulacion = fechaInicioSimulacion;
        System.out.println();
        System.out.println();
        System.out.println("=============================================================INICIO DE LA SIMULACION=======================================================================================================");
        //Desde aqui se empieza el conteno del tiempo
        // Medir tiempo de inicio
        long startTime = System.nanoTime();
        while(fechaActualSimulacion.isBefore(fechaFinSimulacion)){
            List<Pedido> pedidosPorAtender = pedidoService.obtenerPedidosEntreFechas(fechaActualSimulacion, fechaActualSimulacion.plusMinutes(minutesIncrement));
            HashMap<Pedido, List<PlanTransporte>> pedidosAtendidos = new HashMap<>();
            HashMap<Vehiculo, ArrayList<Tramo>>rutasVehiculosDefinidas = new HashMap<>();
            //Lista de pedidos por atender
            if(pedidosPorAtender!=null) {
                for (Pedido pedido : pedidosPorAtender) {
                    Random random = new Random();
                    int semilla = random.nextInt(8000);
                    ArrayList<PlanTransporte> planes = planTransporteService.definirPlanesTransporte(fechaActualSimulacion, pedido, caminos, semilla, rutasVehiculosDefinidas);
                    pedidosAtendidos.put(pedido, planes);
                }
                System.out.println("==================================================================================================================================================================================");
                System.out.println("SIMULACION EJECUTADA EN EL PERIODO:" +
                        " " + fechaActualSimulacion.getDayOfMonth() + "/" + fechaActualSimulacion.getMonthValue() + "/" + fechaActualSimulacion.getYear() + " " + fechaActualSimulacion.getHour() + "h:" + fechaActualSimulacion.getMinute() + "m"+
                        " - " + fechaActualSimulacion.plusMinutes(minutesIncrement).getDayOfMonth() + "/" + fechaActualSimulacion.plusMinutes(minutesIncrement).getMonthValue() + "/" + fechaActualSimulacion.plusMinutes(minutesIncrement).getYear() + " " + fechaActualSimulacion.plusMinutes(minutesIncrement).getHour() + "h:" + fechaActualSimulacion.plusMinutes(minutesIncrement).getMinute() + "m");
                System.out.println("==================================================================================================================================================================================");
                System.out.println("PLANES DE TRANSPORTE GENERADOS: ");
            }

            for (Pedido pedido : pedidosAtendidos.keySet()){
                for(PlanTransporte planTransporte : pedidosAtendidos.get(pedido)){
                    planTransporteService.imprimirDatosPlanTransporte(planTransporte);
                    planTransporteService.imprimirRutasPlanTransporte(planTransporte);
                }
            }
            for (Vehiculo vehiculo : rutasVehiculosDefinidas.keySet()){
                vehiculo.setEstado(EstadoVehiculo.EnRuta);
                vehiculoService.guardar(vehiculo);
            }

            fechaActualSimulacion = fechaActualSimulacion.plusMinutes(minutesIncrement);
            tramoService.actualizarEstadoTramos(fechaInicioSimulacion, fechaFinSimulacion);
            vehiculoService.actualizarEstadoVehiculos(fechaInicioSimulacion, fechaFinSimulacion, caminos);
        }
        // Medir tiempo de fin
        long endTime = System.nanoTime();

        // Calcular el tiempo total de ejecución
        long durationInNano = endTime - startTime;
        double durationInSeconds = (double) durationInNano / 1_000_000_000.0;

        System.out.println();
        System.out.println();
        System.out.println("=============================================================FIN DE LA SIMULACION==========================================================================================================");
        System.out.println("El tiempo total de ejecución de la simulación fue: " + durationInSeconds + " segundos");

    }


    public void atenderCantidadEspecificaPedidosDesdeFecha(LocalDateTime fechaInicioSimulacion, int numeroPedidos) {
        int minutesIncrement = 60; /// PORQUE TENIA  180????
        int totalIteraciones = 30; // Siempre 30 iteraciones
        LocalDateTime fechaFinSimulacion = fechaInicioSimulacion.plusDays(7);

        ArrayList<Oficina> oficinas;
        ArrayList<Pedido> pedidos;
        ArrayList<Bloqueo> bloqueos;
        ArrayList<Mantenimiento> mantenimientos;
        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();
        ArrayList<Region> regiones = new ArrayList<Region>();
        ArrayList<Vehiculo> vehiculos = new ArrayList<Vehiculo>();
        ArrayList<Almacen> almacenes = new ArrayList<Almacen>();
        ArrayList<TipoVehiculo> tiposVehiculo = new ArrayList<>();
        ArrayList<Cliente> clientes = new ArrayList<>();
        ArrayList<Paquete> paquetes = new ArrayList<>();
        HashMap<String, ArrayList<Ubicacion>> caminos;

        regionService.guardar(new Region("COSTA", 1));
        regionService.guardar(new Region("SIERRA", 2));
        regionService.guardar(new Region("SELVA", 3));
        regiones = regionService.obtenerTodas();

        oficinas = filesService.cargarOficinasDesdeBD("dataset/Oficinas/c.1inf54.24-2.oficinas.v1.0.txt", regiones, ubicaciones);
        vehiculos = filesService.cargarVehiculosAlmacenesDesdeArchivo("dataset/Vehiculos/vehiculos.txt", almacenes, vehiculos, ubicaciones, tiposVehiculo);
        caminos = filesService.cargarCaminosDesdeArchivo("dataset/Tramos/c.1inf54.24-2.tramos.v1.0.txt", ubicaciones);
        bloqueos = filesService.cargarBloqueosDesdeArchivo("dataset/Bloqueos/c.1inf54.24-2.bloqueo.04.txt", ubicaciones);
        pedidos = pedidoService.cargarPedidosDesdeArchivo("dataset/Pedidos/c.1inf54.ventas202402.txt", oficinas, ubicaciones, clientes, paquetes);//ESTO SE COLOCO A LEER DATASET DE PRUEBA PARA EL EXP NUM
        mantenimientos = filesService.cargarMantenimientosDesdeArchivo("dataset/Mantenimientos/c.1inf54.24-2.plan.mant.2024.trim.abr.may.jun.txt", vehiculos);

        if (pedidos.size() < numeroPedidos * totalIteraciones) {
            System.out.println("No hay suficientes pedidos en el archivo para completar las 30 iteraciones.");
            return;
        }

        for (int iteracion = 1; iteracion <= totalIteraciones; iteracion++) {
            // Limpiar las listas para almacenar los datos de cada iteración
            List<Long> memoryUsageHistory = new ArrayList<>();
            List<Double> cpuUsageHistory = new ArrayList<>();
            
            // Preparación para el monitoreo de memoria y CPU
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            // Thread para medir la memoria y CPU cada 500ms mientras se ejecuta el código
            Thread resourceMonitor = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                    memoryUsageHistory.add(heapMemoryUsage.getUsed());

                    // Obtener el uso de CPU más preciso
                    double cpuLoad = osBean.getProcessCpuLoad() * 100; // Convertir a porcentaje
                    cpuUsageHistory.add(cpuLoad);

                    try {
                        Thread.sleep(500); // medir cada 500ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            resourceMonitor.start(); // Iniciar el monitoreo

            // Obtener el subconjunto de pedidos para esta iteración
            int inicio = (iteracion - 1) * numeroPedidos;
            int fin = Math.min(iteracion * numeroPedidos, pedidos.size());
            List<Pedido> pedidosSubconjunto = pedidos.subList(inicio, fin);

            LocalDateTime fechaActualSimulacion = fechaInicioSimulacion;

            System.out.println("=========== INICIO DE LA SIMULACION ITERACION " + iteracion + " ===========");

            // Medir tiempo de inicio de la simulación para esta iteración
            long startTime = System.nanoTime();
            int contadorPedido = 0;
            long tiempoTotalEntrega = 0;

            while (contadorPedido < pedidosSubconjunto.size()) {
                List<Pedido> pedidosPorAtender = pedidosSubconjunto.subList(contadorPedido,
                        Math.min(contadorPedido + numeroPedidos, pedidosSubconjunto.size()));
                HashMap<Pedido, List<PlanTransporte>> pedidosAtendidos = new HashMap<>();
                HashMap<Vehiculo, ArrayList<Tramo>> rutasVehiculosDefinidas = new HashMap<>();

                if (pedidosPorAtender != null) {
                    contadorPedido += pedidosPorAtender.size();
                    for (Pedido pedido : pedidosPorAtender) {
                        Random random = new Random();
                        int semilla = random.nextInt(8000);
                        ArrayList<PlanTransporte> planes = planTransporteService.definirPlanesTransporte(fechaActualSimulacion, pedido, caminos, semilla, rutasVehiculosDefinidas);
                        pedidosAtendidos.put(pedido, planes);
                    }
                    // Calcular el tiempo total de entrega para los planes de transporte
                    tiempoTotalEntrega = (long) (planTransporteService
                            .obtenerTiempoTotalRecorridosParaTodosPlanTransporte() * 60);// (totalTime / 60000);
                    System.out.println("SIMULACION EJECUTADA EN EL PERIODO: " + fechaActualSimulacion + " - " + fechaActualSimulacion.plusMinutes(minutesIncrement));

                    for (Pedido pedido : pedidosAtendidos.keySet()) {
                        for (PlanTransporte planTransporte : pedidosAtendidos.get(pedido)) {
                            gestionarAverias(planTransporte);

                            // Imprimir información de las rutas de cada plan de transporte
                            planTransporteService.imprimirDatosPlanTransporte(planTransporte);
                            planTransporteService.imprimirRutasPlanTransporte(planTransporte);
                        }
                    }

                    for (Vehiculo vehiculo : rutasVehiculosDefinidas.keySet()) {
                        vehiculo.setEstado(EstadoVehiculo.EnRuta);
                        vehiculoService.guardar(vehiculo);
                    }

                    fechaActualSimulacion = fechaActualSimulacion.plusMinutes(minutesIncrement);
                    tramoService.actualizarEstadoTramos(fechaInicioSimulacion, fechaFinSimulacion);
                    vehiculoService.actualizarEstadoVehiculos(fechaInicioSimulacion, fechaFinSimulacion, caminos);
                }
            }

            // Medir tiempo de fin de la simulación para esta iteración
            long endTime = System.nanoTime();
            long totalTime = (endTime - startTime) / 1_000_000; // Convertir a milisegundos

            resourceMonitor.interrupt(); // Parar el monitoreo
            //resourceMonitor.join(); // Esperar a que el hilo termine antes de continuar
            try {
                resourceMonitor.join(); // Esperar a que el hilo termine antes de continuar
            } catch (InterruptedException e) {
                // En caso de interrupción, restablecemos el estado interrumpido del hilo
                Thread.currentThread().interrupt();
                System.err.println("El hilo de monitoreo fue interrumpido: " + e.getMessage());
            }

            // Calcular promedios de uso de memoria y CPU
            double avgCpuUsage = cpuUsageHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            long avgMemoryUsage = (long) memoryUsageHistory.stream().mapToLong(Long::longValue).average().orElse(0L) / (1024 * 1024); // Convertir a MB

            // Crear el archivo para la iteración actual
            String fileName = "resource_usage_iter_" + iteracion + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write("Iteración: " + iteracion + "\n");
                writer.write("Tiempo de ejecución del programa (ms): " + totalTime + "\n");
                writer.write("Tiempo total de entrega (min): " + tiempoTotalEntrega + "\n");
                writer.write("Consumo de memoria promedio (MB): " + avgMemoryUsage + "\n");
                writer.write("Consumo de CPU promedio (%): " + avgCpuUsage + "\n");
                writer.write("--- Estadísticas de la iteración completada ---\n");
            } catch (IOException e) {
                System.err.println("Error al escribir el archivo de resultados: " + e.getMessage());
            }

            System.out.println("=========== FIN DE LA SIMULACION ITERACION " + iteracion + " ===========");
            System.out.println("El tiempo total de ejecución fue: " + totalTime + " ms");
            memoryUsageHistory.clear();
            cpuUsageHistory.clear();
        }
    }

    private void gestionarAverias(PlanTransporte planTransporte) {
        Vehiculo vehiculo = planTransporte.getVehiculo();
        List<Tramo> tramos = tramoService.obtenerPorPlanTransporte(planTransporte);

        // Recorremos los tramos y verificamos si ocurre una avería
        for (Tramo tramo : tramos) {
            // Probabilidad del 10% de que ocurra una avería en un tramo
            if (Math.random() < 0.1) {
                // Generamos el tipo de avería aleatoria
                TipoAveria tipoAveria = generarAveriaAleatoria();
                System.out.println(
                        "El vehículo " + vehiculo.getCodigo() + " ha sufrido una avería de tipo: " + tipoAveria);

                // Gestionamos la avería según su tipo
                vehiculoService.gestionarAveria(vehiculo, tipoAveria, tramo);

                // Si el vehículo no puede continuar debido a una avería grave, detenemos el
                // proceso
                if (vehiculo.getEstado() == EstadoVehiculo.Averiado) {
                    System.out.println("El vehículo " + vehiculo.getCodigo() + " ha quedado averiado y no puede continuar.");
                    break;
                }
            }
        }
    }

    // Método auxiliar para generar un tipo de avería aleatoria
    private TipoAveria generarAveriaAleatoria() {
        TipoAveria[] tiposAveria = TipoAveria.values();
        Random random = new Random();
        return tiposAveria[random.nextInt(tiposAveria.length)];
    }


}
