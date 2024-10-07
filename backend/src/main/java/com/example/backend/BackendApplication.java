package com.example.backend;

import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.stream.Collectors;

import com.example.backend.Service.*;
//import com.example.backend.models.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
//import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.System;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.ssl.SslProperties.Bundles.Watch.File;
//import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
//import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

//import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
//import java.lang.System.*;
import java.util.*;



@SpringBootApplication
public class BackendApplication {

    @Autowired
    private SimulacionService simulacionService;

	public static void main(String[] args) {
        //ApplicationContext context = SpringApplication.run(BackendApplication.class, args);
        SpringApplication.run(BackendApplication.class, args);
        //SimulacionService simulacionService = context.getBean(SimulacionService.class);
        /*POR DESCOMENTAR
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

        RegionService regionService = context.getBean(RegionService.class);
        PedidoService pedidoService = context.getBean(PedidoService.class);
        PlanTransporteService planTransporte = context.getBean(PlanTransporteService.class);
        FilesService filesService = context.getBean(FilesService.class);
        VehiculoService vehiculoService = context.getBean(VehiculoService.class);
        TramoService tramoService = context.getBean(TramoService.class);
        MantenimientoService mantenimientoService = context.getBean(MantenimientoService.class);

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

        System.out.println("-----------------ENTRANDO DESDE MAIN---------------------------------");

        LocalDateTime fechaSeleccionada = LocalDateTime.of(2024, 4, 1, 0, 0);//LocalDateTime.now().minusHours(3).minusMinutes(0);
        //LocalDateTime fechaSeleccionada = LocalDateTime.of(2024, 9, 9, 7, 27);
        //LocalDateTime fechaSeleccionada = LocalDateTime.now();
        ArrayList<Pedido> pedidosFuturos = pedidos.stream()
                        .filter(pedidoS -> pedidoS.getFechaRegistro().isAfter(fechaSeleccionada))
                        .collect(Collectors.toCollection(ArrayList::new));

        // Ejecutar con diferentes semillas
        for (int semilla = 1; semilla <= 30; semilla++) {
            System.out.println("Ejecutando con semilla: " + semilla);
            
            System.out.println("-----------------DATOS DEL PEDIDO 1---------------------------------");
            ArrayList<PlanTransporte> planes0 = planTransporte.definirPlanesTransporte(fechaSeleccionada, pedidosFuturos.get(0),  caminos, semilla);

            System.out.println("-----------------DATOS DEL PEDIDO 2---------------------------------");
            ArrayList<PlanTransporte> planes1 = planTransporte.definirPlanesTransporte(fechaSeleccionada, pedidosFuturos.get(1),  caminos, semilla);

            System.out.println("Fin de ejecución con semilla: " + semilla);
        }

        */

        /*
        System.out.println("-----------------DATOS DEL PEDIDO 3---------------------------------");
        ArrayList<PlanTransporte> planes2 = planTransporte.definirPlanesTransporte(pedidosFuturos.get(2), almacenes, caminos, regiones, ubicaciones, vehiculos, bloqueos);

        System.out.println("-----------------DATOS DEL PEDIDO 4---------------------------------");
        ArrayList<PlanTransporte> planes3 = planTransporte.definirPlanesTransporte(pedidosFuturos.get(3), almacenes, caminos, regiones, ubicaciones, vehiculos, bloqueos);
         */

        /*
        LocalDateTime fechaInicioSimulacion = LocalDateTime.of(2024, 4, 1, 0, 0);
        LocalDateTime fechaFinSimulacion = LocalDateTime.of(2024, 4, 6, 12, 40) ;

        tramoService.actualizarEstadoTramos(fechaInicioSimulacion, fechaFinSimulacion);
        vehiculoService.actualizarEstadoVehiculos(fechaInicioSimulacion, fechaFinSimulacion, caminos);

        while(true);
        */
        //simulacionService.simulacionSemanal(LocalDateTime.of(2024, 4, 4, 1, 0));
	}
    
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // Replace with the allowed origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
    
    @Bean
    public ApplicationRunner initializer() {
        return args -> {
            // Preparación para el monitoreo de memoria
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            List<Long> memoryUsageHistory = new ArrayList<>();
            List<Long> timestamps = new ArrayList<>();

            // Thread para medir la memoria cada segundo mientras se ejecuta el código
            Thread memoryMonitor = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                    memoryUsageHistory.add(heapMemoryUsage.getUsed());
                    timestamps.add(java.lang.System.currentTimeMillis());
                    try {
                        Thread.sleep(1000); // medir cada segundo
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            // Iniciar el monitoreo de memoria
            memoryMonitor.start();

            try {
                // Correr el algoritmo ACO
                simulacionService.simulacionSemanal(LocalDateTime.of(2024, 4, 4, 1, 0));
                
            } finally {
                // Parar el monitoreo de memoria al finalizar la ejecución
                memoryMonitor.interrupt();
            }

            // Graficar el uso de memoria
            createMemoryUsageChart(memoryUsageHistory, timestamps);
        };
    }

    private void createMemoryUsageChart(List<Long> memoryUsage, List<Long> timestamps) {
        XYSeries series = new XYSeries("Memory Usage");

        for (int i = 0; i < memoryUsage.size(); i++) {
            // Convertir timestamps a segundos desde el inicio
            long timeInSeconds = (timestamps.get(i) - timestamps.get(0)) / 1000;
            series.add(timeInSeconds, memoryUsage.get(i) / (1024 * 1024)); // Convertir a MB
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Memory Usage Over Time",
                "Time (s)",
                "Memory (MB)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Guardar la gráfica como imagen
        try {
            
            ChartUtils.saveChartAsPNG(new File("memory_usage_chart.png"), chart, 800, 600);
            
            System.out.println("Gráfica guardada como memory_usage_chart.png");
        } catch (IOException e) {
            System.err.println("Error al guardar la gráfica: " + e.getMessage());
        }
    }

}


