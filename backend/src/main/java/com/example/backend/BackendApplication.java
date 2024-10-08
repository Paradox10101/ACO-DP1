package com.example.backend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.backend.Service.SimulacionService;
import com.example.backend.Service.PlanTransporteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.sun.management.OperatingSystemMXBean;
import jakarta.annotation.Priority;


@SpringBootApplication
public class BackendApplication {

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private PlanTransporteService planTransporteService;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

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

            int totalIteraciones = 30; // Número de iteraciones, puedes modificarlo según sea necesario.

            for (int iteracion = 1; iteracion <= totalIteraciones; iteracion++) {
                // Preparación para el monitoreo de memoria y CPU
                MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
                OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

                List<Long> memoryUsageHistory = new ArrayList<>();
                List<Double> cpuUsageHistory = new ArrayList<>();
                long startTime = System.currentTimeMillis(); // Tiempo de inicio de la iteración

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

                // Iniciar el monitoreo de memoria y CPU
                resourceMonitor.start();

                try {
                    // Correr la simulación
                    simulacionService.atenderCantidadEspecificaPedidosDesdeFecha(LocalDateTime.of(2024, 4, 4, 1, 0),
                            10);

                } finally {
                    // Parar el monitoreo de memoria y CPU al finalizar la ejecución
                    resourceMonitor.interrupt();
                    resourceMonitor.join(); // Esperar a que el hilo termine antes de continuar
                }

                long endTime = System.currentTimeMillis(); // Tiempo de finalización de la iteración
                long totalTime = endTime - startTime; // Tiempo de ejecución en milisegundos

                // Calcular promedios de uso de memoria y CPU
                double avgCpuUsage = cpuUsageHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                long avgMemoryUsage = (long) memoryUsageHistory.stream().mapToLong(Long::longValue).average().orElse(0.0) / (1024 * 1024); // Convertir a MB

                // Calculo del tiempo total de entrega
                long tiempoTotalEntrega = (long)(planTransporteService.obtenerTiempoTotalRecorridosParaTodosPlanTransporte()*60);//(totalTime / 60000); // Convertido a minutos

                // Guardar los resultados en un archivo de texto
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

                // Limpiar los historiales para la siguiente iteración
                memoryUsageHistory.clear();
                cpuUsageHistory.clear();
            }
        };
    }
}
