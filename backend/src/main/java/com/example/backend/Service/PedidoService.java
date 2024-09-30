package com.example.backend.Service;

import com.example.backend.Repository.ClienteRepository;
import com.example.backend.Repository.PaqueteRepository;
import com.example.backend.models.*;
import com.example.backend.Repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    public void mostrarDatosDelPedido(Long id){
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if(pedido.isPresent()){
            System.out.println("Datos del pedido: ");
            System.out.println("--------------------");
            System.out.println("Datos de la ruta del pedido: ");
            //System.out.println("Almacen"+ pedido.get().getAlmacen().getUbicacion().getProvincia() + "Oficina"+ pedido.get().getOficinaDestino().getUbicacion().getProvincia());
            System.out.println("--------------------");
            System.out.println("Cantidad de paquetes: " + pedido.get().getCantidadPaquetes());
            System.out.println("--------FECHAS RELACION:------------");            
            //System.out.println("ID: " + pedido.get().getId_pedido());
            System.out.println("Fecha de registro: " + pedido.get().getFechaRegistro());

            System.out.println("Fecha de entrega estimada: " + pedido.get().getFechaEntregaEstimada());
            System.out.println("Tiempo Restante: " + calcularTiempoRestante(pedido.get().getFechaRegistro(), pedido.get().getFechaEntregaEstimada()));
            System.out.println("Estado: " + pedido.get().getEstado());
        } else {
            System.out.println("Pedido no encontrado");
        }
    }

    public String calcularTiempoRestante(LocalDateTime fechaRegistro, LocalDateTime fechaEntregaEstimada) {
        // Calcular la duración entre las dos fechas
        Duration duracion = Duration.between(fechaRegistro, fechaEntregaEstimada);

        // Obtener los días y horas de la duración
        long dias = duracion.toDays();
        long horas = duracion.minusDays(dias).toHours(); // Restar los días para obtener las horas restantes

        return dias + "d " + horas + "h";
    }
    
    

    public ArrayList<Pedido> cargarPedidosDesdeArchivo(String rutaArchivo, List<Oficina> oficinas,
            List<Ubicacion> ubicaciones, List<Cliente> clientes, List<Paquete> paquetes) { // esto va ir en otra parte
                                                                                           // <---
        ArrayList<Pedido> pedidos = new ArrayList<>();
        ArrayList<Cliente> clientesExistentes = new ArrayList<>(clienteRepository.findAll());
        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    Pedido pedido = new Pedido();
                    Cliente cliente = new Cliente();
                    String[] valores = linea.split(",");
                    String anhoMesString = (rutaArchivo.substring(rutaArchivo.length() - 10)).split(".txt")[0];
                    String anhoString = anhoMesString.substring(0, 4);
                    String mesString = anhoMesString.substring(anhoMesString.length() - 2);
                    String diaString = (valores[0].split(" "))[0];
                    String horaMinutoString = (valores[0].split(" "))[1];
                    String fechaHoraString = diaString + "/" + mesString + "/" + anhoString + " " + horaMinutoString;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraString, formatter);

                    String tramoString = valores[1].trim();
                    String ubigeoDestino = (tramoString.split("=>"))[1].trim();
                    int cantidadPaquetes = Integer.parseInt(valores[2].trim());
                    String codigoCliente = valores[3].trim();
                    Optional<Ubicacion> ubicacionDestinoSeleccionada = ubicaciones.stream().filter(
                            ubicacionS -> ubicacionS.getUbigeo().equals(ubigeoDestino)).findFirst();
                    if (ubicacionDestinoSeleccionada.isPresent()) {
                        Optional<Oficina> oficinaSeleccionada = oficinas.stream().filter(
                                oficinaS -> oficinaS.getUbicacion().getId_ubicacion() == ubicacionDestinoSeleccionada
                                        .get().getId_ubicacion())
                                .findFirst();
                        if (oficinaSeleccionada.isPresent()) {
                            pedido.setOficinaDestino(oficinaSeleccionada.get());
                            pedido.setFechaRegistro(fechaHora);
                            pedido.setFechaEntregaEstimada(fechaHora.plusDays(oficinaSeleccionada.get().getUbicacion().getRegion().getDiasLimite()));//ESTO SE DEBE CAMBIAR A UNA FUNCION QUE CALCULE LA FECHA DE ENTREGA ESTIMADA
                            pedido.setCantidadPaquetes(cantidadPaquetes);
                            pedido.setEstado(EstadoPedido.Registrado);
                            cliente.setCodigo(codigoCliente);

                            Optional<Cliente> clienteSeleccionado = clientesExistentes.stream().filter(
                                    clienteS -> clienteS != null && clienteS.getCodigo() != null
                                            && clienteS.getCodigo().equals(codigoCliente))
                                    .findFirst();
                            if (!clienteSeleccionado.isPresent()) {
                                clienteRepository.save(cliente);
                                clientesExistentes.add(cliente);
                                clientes.add(cliente);
                            } else {
                                cliente = clienteSeleccionado.get();
                            }

                            pedidoRepository.save(pedido);
                            pedidos.add(pedido);

                            for (int i = 0; i < pedido.getCantidadPaquetes(); i++) {
                                Paquete paquete = new Paquete();
                                paquete.setPedido(pedido);
                                paquete.setEstado(EstadoPaquete.EnAlmacen);
                                paqueteRepository.save(paquete);
                                paquetes.add(paquete);

                            }
                        }

                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return pedidos;
    }
}
