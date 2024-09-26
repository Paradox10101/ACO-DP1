package com.example.backend.Service;

import com.example.backend.Repository.TipoVehiculoRepository;
import com.example.backend.Repository.TramoRepository;
import com.example.backend.models.TipoVehiculo;
import com.example.backend.models.Tramo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
@Service
public class TipoVehiculoService {

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    public List<TipoVehiculo> obtenerTodos() {
        return tipoVehiculoRepository.findAll();
    }

    public Optional<TipoVehiculo> obtenerPorId(Long id) {
        return tipoVehiculoRepository.findById(id);
    }

    public TipoVehiculo guardar(TipoVehiculo tipoVehiculo) {
        return tipoVehiculoRepository.save(tipoVehiculo);
    }

    public void eliminar(Long id) {
        tipoVehiculoRepository.deleteById(id);
    }
}
