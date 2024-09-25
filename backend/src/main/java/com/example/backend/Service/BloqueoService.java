package com.example.backend.Service;

import com.example.backend.models.Bloqueo;
import com.example.backend.Repository.BloqueoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BloqueoService {

    @Autowired
    private BloqueoRepository bloqueoRepository;

    public List<Bloqueo> obtenerTodos() {
        return bloqueoRepository.findAll();
    }

    public Optional<Bloqueo> obtenerPorId(Long id) {
        return bloqueoRepository.findById(id);
    }

    public Bloqueo guardar(Bloqueo bloqueo) {
        return bloqueoRepository.save(bloqueo);
    }

    public void eliminar(Long id) {
        bloqueoRepository.deleteById(id);
    }
}
