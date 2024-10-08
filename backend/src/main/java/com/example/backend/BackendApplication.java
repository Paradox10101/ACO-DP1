package com.example.backend;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.backend.Service.SimulacionService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.sun.management.OperatingSystemMXBean;

@SpringBootApplication
public class BackendApplication {

    @Autowired
    private SimulacionService simulacionService;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }



        //simulacionService.simulacionSemanal(LocalDateTime.of(2024, 4, 4, 1, 0));
        //simulacionService.atenderCantidadEspecificaPedidosDesdeFecha(LocalDateTime.of(2024, 4, 4, 1, 0),3);

    
    @Bean

    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

    @Bean
    public ApplicationRunner initializer() {
        return args -> {
            // Preparación para el monitoreo de memoria y CPU
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            List<Long> memoryUsageHistory = new ArrayList<>();
            List<Double> cpuUsageHistory = new ArrayList<>();
            List<Long> timestamps = new ArrayList<>();

            // Thread para medir la memoria y CPU cada segundo mientras se ejecuta el código
            Thread resourceMonitor = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                    memoryUsageHistory.add(heapMemoryUsage.getUsed());

                    // Obtener el uso de CPU más preciso
                    double cpuLoad = osBean.getProcessCpuLoad() * 100; // Convertir a porcentaje
                    cpuUsageHistory.add(cpuLoad);

                    timestamps.add(java.lang.System.currentTimeMillis());
                    try {
                        Thread.sleep(1000); // medir cada segundo
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            // Iniciar el monitoreo de memoria y CPU
            resourceMonitor.start();

            try {
                // Correr el algoritmo ACO
                simulacionService.atenderCantidadEspecificaPedidosDesdeFecha(LocalDateTime.of(2024, 4, 4, 1, 0), 50);

            } finally {
                // Parar el monitoreo de memoria y CPU al finalizar la ejecución
                resourceMonitor.interrupt();
            }

            // Graficar el uso de memoria y CPU
            createMemoryUsageChart(memoryUsageHistory, timestamps);
            createCpuUsageChart(cpuUsageHistory, timestamps);
        };
    }

    private void createMemoryUsageChart(List<Long> memoryUsage, List<Long> timestamps) {
        XYSeries memorySeries = new XYSeries("Memory Usage (MB)");

        for (int i = 0; i < memoryUsage.size(); i++) {
            // Convertir timestamps a segundos desde el inicio
            long timeInSeconds = (timestamps.get(i) - timestamps.get(0)) / 1000;

            // Añadir datos de memoria (convertidos a MB)
            memorySeries.add(timeInSeconds, memoryUsage.get(i) / (1024 * 1024));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(memorySeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Consumo de Memoria",
                "Tiempo (s)",
                "Uso de Memoria (MB)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        // Guardar la gráfica de uso de memoria
        try {
            ChartUtils.saveChartAsPNG(new File("memory_usage_chart.png"), chart, 800, 600);
            System.out.println("Gráfica de memoria guardada como memory_usage_chart.png");
        } catch (IOException e) {
            System.err.println("Error al guardar la gráfica de memoria: " + e.getMessage());
        }
    }

    private void createCpuUsageChart(List<Double> cpuUsage, List<Long> timestamps) {
        XYSeries cpuSeries = new XYSeries("CPU Usage (%)");

        for (int i = 0; i < cpuUsage.size(); i++) {
            // Convertir timestamps a segundos desde el inicio
            long timeInSeconds = (timestamps.get(i) - timestamps.get(0)) / 1000;

            // Añadir datos de CPU (en porcentaje)
            cpuSeries.add(timeInSeconds, cpuUsage.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(cpuSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Consumo de CPU",
                "Time (s)",
                "Uso de CPU (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        // Guardar la gráfica de uso de CPU
        try {
            ChartUtils.saveChartAsPNG(new File("cpu_usage_chart.png"), chart, 800, 600);
            System.out.println("Gráfica de CPU guardada como cpu_usage_chart.png");
        } catch (IOException e) {
            System.err.println("Error al guardar la gráfica de CPU: " + e.getMessage());
        }
    }
}
