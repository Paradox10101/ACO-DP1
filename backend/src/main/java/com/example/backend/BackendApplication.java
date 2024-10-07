package com.example.backend;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.example.backend.Service.*;
import com.example.backend.models.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BackendApplication.class, args);

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

        System.out.println("-----------------DATOS DEL PEDIDO 1---------------------------------");
        ArrayList<PlanTransporte> planes0 = planTransporte.definirPlanesTransporte(fechaSeleccionada, pedidosFuturos.get(0),  caminos);


        /*
        System.out.println("-----------------DATOS DEL PEDIDO 2---------------------------------");
        ArrayList<PlanTransporte> planes1 = planTransporte.definirPlanesTransporte(fechaSeleccionada, pedidosFuturos.get(1),  caminos);
        */

        /*
        System.out.println("-----------------DATOS DEL PEDIDO 3---------------------------------");
        ArrayList<PlanTransporte> planes2 = planTransporte.definirPlanesTransporte(pedidosFuturos.get(2), almacenes, caminos, regiones, ubicaciones, vehiculos, bloqueos);

        System.out.println("-----------------DATOS DEL PEDIDO 4---------------------------------");
        ArrayList<PlanTransporte> planes3 = planTransporte.definirPlanesTransporte(pedidosFuturos.get(3), almacenes, caminos, regiones, ubicaciones, vehiculos, bloqueos);
         */
        LocalDateTime fechaInicioSimulacion = LocalDateTime.of(2024, 4, 1, 0, 0);
        LocalDateTime fechaFinSimulacion = LocalDateTime.of(2024, 4, 1, 12, 40) ;

        tramoService.actualizarEstadoTramos(fechaInicioSimulacion, fechaFinSimulacion);
        vehiculoService.actualizarEstadoVehiculos(fechaInicioSimulacion, fechaFinSimulacion);

        while(true);

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

