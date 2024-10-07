package com.example.backend.Service;

import com.example.backend.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

            // Revisar y gestionar averías para los vehículos que están en cada plan de
            // transporte
            for (Pedido pedido : pedidosAtendidos.keySet()) {
                for (PlanTransporte planTransporte : pedidosAtendidos.get(pedido)) {
                    // Aquí gestionamos averías para cada plan de transporte
                    gestionarAverias(planTransporte);

                    // Imprimir información de las rutas de cada plan
                    planTransporteService.imprimirRutasPlanTransporte(planTransporte);
                }
            }

            fechaActualSimulacion = fechaActualSimulacion.plusMinutes(minutesIncrement);
            tramoService.actualizarEstadoTramos(fechaInicioSimulacion, fechaFinSimulacion);
            vehiculoService.actualizarEstadoVehiculos(fechaInicioSimulacion, fechaFinSimulacion, caminos);
        }
        System.out.println();
        System.out.println();
        System.out.println("=============================================================FIN DE LA SIMULACION==========================================================================================================");

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
                    System.out.println("El vehículo" + vehiculo.getCodigo() + "ha quedado averiado y no puede continuar.");
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
