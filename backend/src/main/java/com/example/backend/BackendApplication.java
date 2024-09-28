package com.example.backend;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.backend.Repository.MantenimientoRepository;
import com.example.backend.Service.*;
import com.example.backend.models.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.backend.algorithm.Aco;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BackendApplication.class, args);

        ArrayList<Oficina> oficinas;
        ArrayList<Tramo> tramos;
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

        UbicacionService ubicacionService = context.getBean(UbicacionService.class);
        RegionService regionService = context.getBean(RegionService.class);
        OficinaService oficinaService = context.getBean(OficinaService.class);
        VehiculoService vehiculoService = context.getBean(VehiculoService.class);
        TramoService tramoService = context.getBean(TramoService.class);
        PedidoService pedidoService = context.getBean(PedidoService.class);
        BloqueoService bloqueoService = context.getBean(BloqueoService.class);
        MantenimientoService mantenimientoService = context.getBean(MantenimientoService.class);
        PlanTransporteService planTransporte = context.getBean(PlanTransporteService.class);
        Aco aco = context.getBean(Aco.class);

        regionService.guardar(new Region("COSTA", 1));
        regionService.guardar(new Region("SIERRA", 2));
        regionService.guardar(new Region("SELVA", 3));
        regiones = regionService.obtenerTodas();

        oficinas = oficinaService.cargarOficinasDesdeBD("dataset/Oficinas/c.1inf54.24-2.oficinas.v1.0.txt", regiones, ubicaciones);
        vehiculos = vehiculoService.cargarVehiculosAlmacenesDesdeArchivo("dataset/Vehiculos/vehiculos.txt",almacenes, vehiculos, ubicaciones, tiposVehiculo);
        caminos = aco.cargarCaminosDesdeArchivo("dataset/Tramos/c.1inf54.24-2.tramos.v1.0.txt", ubicaciones);
        //tramos = Tramo.cargarTramosDesdeArchivo("dataset/Tramos/c.1inf54.24-2.tramos.v1.0.txt", caminos);
        bloqueos = bloqueoService.cargarBloqueosDesdeArchivo("dataset/Bloqueos/c.1inf54.24-2.bloqueo.01.txt", ubicaciones);
        pedidos = pedidoService.cargarPedidosDesdeArchivo("dataset/Pedidos/c.1inf54.ventas202409.txt", oficinas, ubicaciones, clientes, paquetes);
        mantenimientos = mantenimientoService.cargarMantenimientosDesdeArchivo("dataset/Mantenimientos/c.1inf54.24-2.plan.mant.2024.trim.abr.may.jun.txt", vehiculos);

        LocalDateTime fechaActual = LocalDateTime.now();

        ArrayList<Pedido> pedidosFuturos = pedidos.stream()
                        .filter(pedidoS -> pedidoS.getFechaRegistro().isAfter(fechaActual))
                        .collect(Collectors.toCollection(ArrayList::new));
        PlanTransporte plan = planTransporte.definirPlanTransporte(pedidosFuturos.get(0), almacenes, caminos, regiones, ubicaciones, vehiculos);
        PlanTransporte plan2 = planTransporte.definirPlanTransporte(pedidosFuturos.get(1), almacenes, caminos, regiones, ubicaciones, vehiculos);
        PlanTransporte plan3 = planTransporte.definirPlanTransporte(pedidosFuturos.get(2), almacenes, caminos, regiones, ubicaciones, vehiculos);
        PlanTransporte plan4 = planTransporte.definirPlanTransporte(pedidosFuturos.get(3), almacenes, caminos, regiones, ubicaciones, vehiculos);

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
