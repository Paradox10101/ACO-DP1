package com.example.backend.Service;

import com.example.backend.models.Almacen;
import com.example.backend.Repository.AlmacenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlmacenService {

    @Autowired
    private AlmacenRepository almacenRepository;

    public List<Almacen> obtenerTodos() {
        return almacenRepository.findAll();
    }

    public List<Almacen> obtenerAlmacenesConVehiculosDisponibles() {
        return almacenRepository.findAlmacenesConVehiculosDisponibles();
    }


    public Optional<Almacen> obtenerPorId(Long id) {
        return almacenRepository.findById(id);
    }

    public Almacen guardar(Almacen almacen) {
        return almacenRepository.save(almacen);
    }

    public void eliminar(Long id) {
        almacenRepository.deleteById(id);
    }
}
