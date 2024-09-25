package com.example.backend;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.backend.algorithm.Aco;
import com.example.backend.models.Almacen;
import com.example.backend.models.Bloqueo;
import com.example.backend.models.Mantenimiento;
import com.example.backend.models.Oficina;
import com.example.backend.models.Pedido;
import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Region;
import com.example.backend.models.TipoVehiculo;
import com.example.backend.models.Tramo;
import com.example.backend.models.Ubicacion;
import com.example.backend.models.Vehiculo;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);

        /*
        ArrayList<Oficina> oficinas;
        ArrayList<Tramo> tramos;
        ArrayList<Pedido> pedidos;
        ArrayList<Bloqueo> bloqueos;
        ArrayList<Mantenimiento> mantenimientos;
        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();
        ArrayList<Region> regiones = new ArrayList<Region>(); //Es hardcodeado
        ArrayList<Vehiculo> vehiculos = new ArrayList<Vehiculo>(); //Es hardcodeado
        ArrayList<Almacen> almacenes = new ArrayList<Almacen>();
        ArrayList<TipoVehiculo> tiposVehiculo = new ArrayList<>();
        HashMap<String, ArrayList<Ubicacion>> caminos = new HashMap<>();
        regiones.add(new Region("COSTA",1));
        regiones.add(new Region("SIERRA",2));
        regiones.add(new Region("SELVA",3));
        regiones.get(0).setRelacionRegionVelocidad(regiones.get(0),70);
        regiones.get(0).setRelacionRegionVelocidad(regiones.get(1),50);
        regiones.get(1).setRelacionRegionVelocidad(regiones.get(1),60);
        regiones.get(1).setRelacionRegionVelocidad(regiones.get(2),55);
        regiones.get(2).setRelacionRegionVelocidad(regiones.get(2),65);

        oficinas = Oficina.cargarOficinasDesdeArchivo("dataset/Oficinas/c.1inf54.24-2.oficinas.v1.0.txt", regiones, caminos, ubicaciones);
        vehiculos = Vehiculo.cargarVehiculosAlmacenesDesdeArchivo("dataset/Vehiculos/vehiculos.txt",almacenes, vehiculos, oficinas,ubicaciones, tiposVehiculo);
        tramos = Tramo.cargarTramosDesdeArchivo("dataset/Tramos/c.1inf54.24-2.tramos.v1.0.txt", caminos);
        pedidos = Pedido.cargarPedidosDesdeArchivo("dataset/Pedidos/c.1inf54.ventas202403.txt", oficinas, ubicaciones);
        bloqueos = Bloqueo.cargarBloqueosDesdeArchivo("dataset/Bloqueos/c.1inf54.24-2.bloqueo.01.txt", ubicaciones);
        mantenimientos = Mantenimiento.cargarMantenimientosDesdeArchivo("dataset/Mantenimientos/c.1inf54.24-2.plan.mant.2024.trim.abr.may.jun.txt", vehiculos);
        /*System.out.println("Listado de Oficinas:");
        System.out.println("--------------------------------------------------");
        for (Oficina oficina : oficinas) {
            System.out.println("ID Oficina: " + oficina.getId_oficina());
            System.out.println("Capacidad Utilizada: " + oficina.getCapacidadUtilizada());
            System.out.println("Capacidad Máxima: " + oficina.getCapacidadMaxima());
            System.out.println("--------------------------------------------------");
        }*/

        /*
        // Crea una instancia del algoritmo ACO
        Aco aco = new Aco();

        // Para cada pedido, busca la mejor ruta utilizando ACO y el plan de transporte
        for (Pedido pedido : pedidos) {
            // Crea un nuevo PlanTransporte y ejecuta el ACO para encontrar la ruta óptima
            PlanTransporte planTransporte = new PlanTransporte();
            PlanTransporte rutaOptima = planTransporte.crearRuta(pedido, almacenes, oficinas, tramos, regiones);

            if (rutaOptima != null) {
                System.out.println("Ruta óptima encontrada para el pedido " + pedido.getId_pedido());
                rutaOptima.getTramos().forEach(tramo -> System.out.println("Tramo de " + tramo.getFid_ubicacion_origen() 
                    + " hacia " + tramo.getFid_ubicacion_destino()));
            } else {
                System.out.println("No se encontró una ruta válida para el pedido " + pedido.getId_pedido());
            }
        }
        
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
            PlanTransporte planTransporte = algoritmoACO.ejecutar(oficinas, tramos, pedido, grafoTramos, 0);  Ajusta
            el mé

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


    /*
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
    */

}
