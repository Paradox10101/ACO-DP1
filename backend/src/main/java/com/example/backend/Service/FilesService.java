package com.example.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.Repository.AlmacenRepository;
import com.example.backend.Repository.BloqueoRepository;
import com.example.backend.Repository.MantenimientoRepository;
import com.example.backend.Repository.OficinaRepository;
import com.example.backend.Repository.TipoVehiculoRepository;
import com.example.backend.Repository.UbicacionRepository;
import com.example.backend.Repository.VehiculoRepository;
import com.example.backend.models.Almacen;
import com.example.backend.models.Bloqueo;
import com.example.backend.models.Cliente;
import com.example.backend.models.EstadoVehiculo;
import com.example.backend.models.Mantenimiento;
import com.example.backend.models.Oficina;
import com.example.backend.models.Paquete;
import com.example.backend.models.Pedido;
import com.example.backend.models.Region;
import com.example.backend.models.TipoVehiculo;
import com.example.backend.models.Ubicacion;
import com.example.backend.models.Vehiculo;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
@Service
public class FilesService {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private BloqueoService bloqueoService;

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private BloqueoRepository bloqueoRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;
    
    @Autowired
    private AlmacenRepository   almacenRepository;

    public ArrayList<Pedido> cargarPedidosDesdeDirectorio(String directorioPath, List<Oficina> oficinas, List<Ubicacion> ubicaciones, List<Cliente> clientes, List<Paquete> paquetes) {
        
        ArrayList<Pedido> pedidos = new ArrayList<>();

        File directorio = new File(directorioPath);
        if (directorio.isDirectory()) {
            File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".txt"));

            if (archivos != null) {
                for (File archivo : archivos) {
                    System.out.println("Cargando archivo de pedidos: " + archivo.getName());
                    ArrayList<Pedido> pedidosCargados = pedidoService.cargarPedidosDesdeArchivo(archivo.getPath(), oficinas, ubicaciones, clientes, paquetes);
                    pedidos.addAll(pedidosCargados);
                }
            }
        } else {
            System.out.println("El path especificado no es un directorio válido.");
        }

        return pedidos;
    }

    public ArrayList<Bloqueo> cargarBloqueosDesdeDirectorio(String directorioPath, List<Ubicacion> ubicaciones) {

        ArrayList<Bloqueo> bloqueos = new ArrayList<>();

        File directorio = new File(directorioPath);
        if (directorio.isDirectory()) {
            File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".txt"));

            if (archivos != null) {
                for (File archivo : archivos) {
                    System.out.println("Cargando archivo de bloqueos: " + archivo.getName());
                    ArrayList<Bloqueo> bloqueosCargados = cargarBloqueosDesdeArchivo(archivo.getPath(), ubicaciones);
                    bloqueos.addAll(bloqueosCargados);
                }
            }
        } else {
            System.out.println("El path especificado no es un directorio válido.");
        }

        return bloqueos;
    }

    public ArrayList<Oficina> leerOficinasDesdeArchivo(String rutaArchivo) {
        ArrayList<Oficina> oficinas = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                Oficina oficina = new Oficina();
                Ubicacion ubicacion = new Ubicacion();

                String[] valores = linea.split(",");
                String ubigeo = valores[0];
                String departamento = valores[1];
                String provincia = valores[2];
                Float latitud = Float.parseFloat(valores[3]);
                Float longitud = Float.parseFloat(valores[4]);
                String nombreRegion = valores[5].trim();
                int capacidadMaxima = Integer.parseInt(valores[6]);

                // Configurando la ubicación y oficina basadas en el archivo
                ubicacion.setUbigeo(ubigeo);
                ubicacion.setDepartamento(departamento);
                ubicacion.setProvincia(provincia);
                ubicacion.setLatitud(latitud);
                ubicacion.setLongitud(longitud);

                oficina.setUbicacion(ubicacion);
                oficina.setCapacidadMaxima(capacidadMaxima);

                // Temporarily set the name of the region
                Region regionTemporal = new Region();
                regionTemporal.setNombre(nombreRegion);
                ubicacion.setRegion(regionTemporal);

                oficinas.add(oficina);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return oficinas;
    }

    public ArrayList<Oficina> cargarOficinasDesdeBD(String rutaArchivo,
            ArrayList<Region> regiones, ArrayList<Ubicacion> ubicaciones) {
        // Leer las oficinas desde el archivo
        ArrayList<Oficina> oficinas = leerOficinasDesdeArchivo(rutaArchivo);

        // Recorrer cada oficina y establecer las ubicaciones y regiones correspondientes
        for (Oficina oficina : oficinas) {
            Ubicacion ubicacion = oficina.getUbicacion();
            Optional<Region> regionSeleccionada = regiones.stream()
                    .filter(regionS -> regionS.getNombre().equals(ubicacion.getRegion().getNombre()))
                    .findFirst();

            if (regionSeleccionada.isPresent()) {
                ubicacion.setRegion(regionSeleccionada.get());
                // Guardar la ubicación si aún no existe en la base de datos
                if (Objects.isNull(ubicacion.getId_ubicacion()) || !ubicacionRepository.existsById(ubicacion.getId_ubicacion())) {
                    ubicacionRepository.save(ubicacion);
                    ubicaciones.add(ubicacion);
                }
            }
            if (Objects.isNull(oficina.getId_oficina()) || !oficinaRepository.existsById(oficina.getId_oficina())) {
                oficinaRepository.save(oficina);
            }
        }

        return oficinas;
    }

    public ArrayList<Vehiculo> cargarVehiculosAlmacenesDesdeArchivo(String rutaArchivo, List<Almacen> almacenes, ArrayList<Vehiculo> vehiculos, List<Ubicacion> ubicaciones, ArrayList<TipoVehiculo> tiposVehiculo) {
        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                for(int i=0; i<3 ;i++){
                    linea = lector.readLine();
                    TipoVehiculo tipoVehiculo = new TipoVehiculo();
                    String[] valores = linea.split(" ");
                    String categoria = valores[0].trim();
                    int capacidad = Integer.parseInt(valores[1].trim());
                    tipoVehiculo.setNombre(categoria);
                    tipoVehiculo.setCapacidadMaxima(capacidad);
                    tipoVehiculoRepository.save(tipoVehiculo);
                    tiposVehiculo.add(tipoVehiculo);
                }
                for(int i=0; i<3 ;i++){
                    Almacen almacen = new Almacen();
                    linea = lector.readLine();
                    String provinciaSel = linea.trim().toUpperCase();
                    Optional<Ubicacion> ubicacionSeleccionada = ubicaciones.stream().filter(ubicacionS -> ubicacionS.getProvincia().equals(provinciaSel)).findFirst();
                    if(ubicacionSeleccionada.isPresent()){
                        almacen.setUbicacion(ubicacionSeleccionada.get());
                        linea = lector.readLine();
                        String[] codigosVehiculos = linea.split(",");
                        almacen.setCantidadVehiculos(codigosVehiculos.length);
                        almacenRepository.save(almacen);
                        almacenes.add(almacen);
                        for(String codigoVehiculo : codigosVehiculos){
                            Vehiculo vehiculo = new Vehiculo();
                            String codigoCorregido = codigoVehiculo.trim();

                            for(TipoVehiculo tipoVehiculo : tiposVehiculo){
                                if(tipoVehiculo.getNombre().equals(String.valueOf(codigoCorregido.charAt(0)))){
                                    vehiculo.setTipoVehiculo(tipoVehiculo);
                                    vehiculo.setDistanciaTotal(0);
                                    vehiculo.setUbicacionActual(ubicacionSeleccionada.get());
                                    vehiculo.setCodigo(codigoCorregido);
                                    vehiculo.setAlmacen(almacen);
                                    vehiculo.setEstado(EstadoVehiculo.Disponible);
                                    vehiculo.setCapacidadMaxima(vehiculo.getTipoVehiculo().getCapacidadMaxima());
                                    vehiculoRepository.save(vehiculo);
                                    vehiculos.add(vehiculo);
                                    break;
                                }
                            }

                        }

                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return vehiculos;
    }

    public ArrayList<Bloqueo> cargarBloqueosDesdeArchivo(String rutaArchivo, List<Ubicacion> ubicaciones){
        ArrayList<Bloqueo> bloqueos = new ArrayList<>();
        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Bloqueo bloqueo = new Bloqueo();
                    String[] valores = linea.split(";");
                    String ubigeoString = valores[0];
                    String fechaString = valores[1];
                    String ubigeoOrigenString = ubigeoString.split(" => ")[0].trim();
                    String ubigeoDestinoString = ubigeoString.split(" => ")[1].trim();
                    String diaMesHoraMinutoInicioString = fechaString.split("==")[0].trim();
                    String diaMesHoraMinutoFinString = fechaString.split("==")[1].trim();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                    String diaMesInicioString = diaMesHoraMinutoInicioString.split(",")[0];
                    String horaMinutoInicioString = diaMesHoraMinutoInicioString.split(",")[1];
                    String mesInicioString = diaMesInicioString.substring(0, 2);
                    String diaInicioString = diaMesInicioString.substring(2);
                    String fechaHoraInicioString = diaInicioString + "/" +
                            mesInicioString + "/" + String.valueOf(LocalDateTime.now().getYear()) + " "
                            + horaMinutoInicioString;

                    LocalDateTime fechaHoraInicio = LocalDateTime.parse(fechaHoraInicioString, formatter);

                    String diaMesFinString = diaMesHoraMinutoFinString.split(",")[0];
                    String horaMinutoFinString = diaMesHoraMinutoFinString.split(",")[1];
                    String mesFinString = diaMesFinString.substring(0, 2);
                    String diaFinString = diaMesFinString.substring(2);
                    String fechaHoraFinString = diaFinString + "/" +
                            mesFinString + "/" + String.valueOf(LocalDateTime.now().getYear()) + " "
                            + horaMinutoFinString;

                    LocalDateTime fechaHoraFin = LocalDateTime.parse(fechaHoraFinString, formatter);
                    Optional<Ubicacion> ubicacionOrigenSeleccionada = ubicaciones.stream().filter(ubicacionSOr -> ubicacionSOr.getUbigeo().equals(ubigeoOrigenString)).findFirst();
                    Optional<Ubicacion> ubicacionDestinoSeleccionada = ubicaciones.stream().filter(ubicacionSDes -> ubicacionSDes.getUbigeo().equals(ubigeoDestinoString)).findFirst();
                    if(ubicacionOrigenSeleccionada.isPresent() && ubicacionDestinoSeleccionada.isPresent()){
                        bloqueo.setFechaInicio(fechaHoraInicio);
                        bloqueo.setFechaFin(fechaHoraFin);
                        bloqueo.setUbicacionOrigen(ubicacionOrigenSeleccionada.get());
                        bloqueo.setUbicacionDestino(ubicacionDestinoSeleccionada.get());
                        bloqueoRepository.save(bloqueo);
                        bloqueos.add(bloqueo);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return bloqueos;
    }

    public ArrayList<Mantenimiento> cargarMantenimientosDesdeArchivo(String rutaArchivo, List<Vehiculo> vehiculos) {
        ArrayList<Mantenimiento> mantenimientos = new ArrayList<>();
        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Mantenimiento mantenimiento = new Mantenimiento();
                    String[] valores = linea.split(":");
                    String anhoMesDiaString = valores[0];
                    String codigoString = valores[1];
                    String anhoString = anhoMesDiaString.substring(0, 4);
                    String mesString = anhoMesDiaString.substring(4, 6);
                    String diaString = anhoMesDiaString.substring(6, 8);
                    String fechaString = diaString + "/" + mesString + "/" + anhoString;

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date fecha = formatter.parse(fechaString);
                    Optional<Vehiculo> vehiculoSeleccionado = vehiculos.stream().filter(
                            vehiculoS -> vehiculoS.getCodigo().equals(codigoString)).findFirst();
                    if(vehiculoSeleccionado.isPresent()){
                        mantenimiento.setVehiculo(vehiculoSeleccionado.get());
                        mantenimiento.setFechaProgramada(fecha);
                        mantenimientoRepository.save(mantenimiento);
                        mantenimientos.add(mantenimiento);

                    }

                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Error al leer formato de fecha: " + e.getMessage());
        }
        return mantenimientos;
    }

    public HashMap<String, ArrayList<Ubicacion>> cargarCaminosDesdeArchivo(String rutaArchivo,
            ArrayList<Ubicacion> ubicaciones) {
        HashMap<String, ArrayList<Ubicacion>> caminos = new HashMap<>();
        for (Ubicacion ubicacion : ubicaciones) {
            caminos.put(ubicacion.getUbigeo(), new ArrayList<>());
        }
        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    String[] valores = linea.split(" => ");
                    String ubigeoOrigen = valores[0].trim();
                    String ubigeoDestino = valores[1].trim();
                    Optional<Ubicacion> ubicacionDestino = ubicaciones.stream()
                            .filter(ubicacionSel -> ubicacionSel.getUbigeo().equals(ubigeoDestino)).findFirst();
                    // caminos.get(ubigeoOrigen).add(ubicacionDestino.get());
                    if (ubicacionDestino.isPresent()) {
                        caminos.get(ubigeoOrigen).add(ubicacionDestino.get());
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return caminos;
    }
}
