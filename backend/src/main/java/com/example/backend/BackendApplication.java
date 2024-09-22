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
import com.example.backend.models.Tramo;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
        ArrayList<Oficina> oficinas;
        ArrayList<Tramo> tramos;
        ArrayList<Pedido> pedidos;
        ArrayList<Bloqueo> bloqueos;
        ArrayList<Mantenimiento> mantenimientos;
        oficinas = Oficina.cargarOficinasDesdeArchivo("backend/dataset/c.1inf54.24-2.oficinas.v1.0.txt");// C:\Users\USUARIO\Documents\GitHub\ACO-DP1\backend\dataset\c.1inf54.24-2.oficinas.v1.0.txt
        tramos = Tramo.cargarTramosDesdeArchivo("backend/dataset/c.1inf54.24-2.tramos.v1.0.txt");
        pedidos = Pedido.cargarPedidosDesdeArchivo("backend/dataset/c.1inf54.ventas202403.txt");
        bloqueos = Bloqueo.cargarBloqueosDesdeArchivo("backend/dataset/c.1inf54.24-2.bloqueo.01.txt");
        mantenimientos = Mantenimiento.cargarMantenimientosDesdeArchivo("backend/dataset/c.1inf54.24-2.plan.mant.2024.trim.abr.may.jun.txt");
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
