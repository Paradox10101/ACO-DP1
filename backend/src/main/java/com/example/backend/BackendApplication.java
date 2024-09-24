package com.example.backend;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.backend.models.Bloqueo;
import com.example.backend.models.Mantenimiento;
import com.example.backend.models.Oficina;
import com.example.backend.models.Pedido;
import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Region;
import com.example.backend.models.Tramo;
import com.example.backend.models.Ubicacion;
import java.util.HashMap;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
        ArrayList<Oficina> oficinas;
        ArrayList<Tramo> tramos;
        ArrayList<Pedido> pedidos;
        ArrayList<Bloqueo> bloqueos;
        ArrayList<Mantenimiento> mantenimientos;
        ArrayList<Region> regiones = new ArrayList<Region>(); //Es hardcodeado
        HashMap<String, Ubicacion> caminos;
        regiones.add(new Region("COSTA",1));
        regiones.add(new Region("SIERRA",2));
        regiones.add(new Region("SELVA",3));
        regiones.get(0).setRelacionRegionVelocidad(regiones.get(0),70);
        regiones.get(0).setRelacionRegionVelocidad(regiones.get(1),50);
        regiones.get(1).setRelacionRegionVelocidad(regiones.get(1),60);
        regiones.get(1).setRelacionRegionVelocidad(regiones.get(2),55);
        regiones.get(2).setRelacionRegionVelocidad(regiones.get(2),65);



        oficinas = Oficina.cargarOficinasDesdeArchivo("backend/dataset/c.1inf54.24-2.oficinas.v1.0.txt", regiones);// C:\Users\USUARIO\Documents\GitHub\ACO-DP1\backend\dataset\c.1inf54.24-2.oficinas.v1.0.txt
        tramos = Tramo.cargarTramosDesdeArchivo("backend/dataset/c.1inf54.24-2.tramos.v1.0.txt");
        pedidos = Pedido.cargarPedidosDesdeArchivo("backend/dataset/c.1inf54.ventas202403.txt",oficinas);
        bloqueos = Bloqueo.cargarBloqueosDesdeArchivo("backend/dataset/c.1inf54.24-2.bloqueo.01.txt");
        mantenimientos = Mantenimiento.cargarMantenimientosDesdeArchivo("backend/dataset/c.1inf54.24-2.plan.mant.2024.trim.abr.may.jun.txt");
        //ubicaciones = Ubicacion.cargarUbicacionesDesdeArchivo("backend/dataset/c.1inf54.24-2.ubicaciones.v1.0.txt");
        
        /*
        // Crear el grafo a partir de los tramos
        grafoTramos = new HashMap<>();
        for (Tramo tramo : tramos) {
            grafoTramos.computeIfAbsent(tramo.getFid_ubicacion_origen(), k -> new ArrayList<>()).add(tramo);
        }

        // Ejemplo de ejecutar el ACO (Simulación con un solo pedido)
        if (!pedidos.isEmpty()) {
            Pedido pedido = pedidos.get(0); // Tomar un pedido como prueba
            // Ejecutar tu algoritmo ACO para encontrar la mejor ruta
            ACO algoritmoACO = new ACO(); // Aquí deberías tener la instancia de tu clase ACO
            PlanTransporte planTransporte = algoritmoACO.ejecutar(oficinas, tramos, pedido, grafoTramos, 0); // Ajusta el método de ejecución según tu implementación

            // Mostrar el resultado de la ejecución
            if (planTransporte != null) {
                System.out.println("Ruta óptima encontrada para el pedido: " + pedido.getId_pedido());
                System.out.println("Tramos de la ruta:");
                for (Tramo tramo : planTransporte.getTramos()) {
                    System.out.println("Origen: " + tramo.getFid_ubicacion_origen() + " -> Destino: " + tramo.getFid_ubicacion_destino());
                }
            } else {
                System.out.println("No se encontró una ruta válida para el pedido.");
            }
        }*/


        System.out.println("Fin Pruebas");
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
}
