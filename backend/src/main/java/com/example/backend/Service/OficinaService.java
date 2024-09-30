package com.example.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backend.models.Oficina;
import com.example.backend.Repository.OficinaRepository;

import java.util.*;
import java.util.Optional;

@Service
public class OficinaService {

    @Autowired
    private OficinaRepository oficinaRepository;

    // Obtener todas las oficinas
    public List<Oficina> obtenerTodasLasOficinas() {
        return oficinaRepository.findAll();
    }

    // Guardar una nueva oficina
    public Oficina guardarOficina(Oficina oficina) {
        return oficinaRepository.save(oficina);
    }

    // Buscar una oficina por ID
    public Optional<Oficina> buscarOficinaPorId(Long id) {
        return oficinaRepository.findById(id);
    }

    // Actualizar una oficina
    public Oficina actualizarOficina(Oficina oficina) {
        return oficinaRepository.save(oficina);
    }

    // Eliminar una oficina
    public void eliminarOficina(Long id) {
        oficinaRepository.deleteById(id);
    }
}
