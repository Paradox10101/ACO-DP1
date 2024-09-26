package com.example.backend.Service;

import com.example.backend.models.Tramo;
import com.example.backend.Repository.TramoRepository;
import com.example.backend.models.Ubicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class TramoService {

    @Autowired
    private TramoRepository tramoRepository;

    public List<Tramo> obtenerTodos() {
        return tramoRepository.findAll();
    }

    public Optional<Tramo> obtenerPorId(Long id) {
        return tramoRepository.findById(id);
    }

    public Tramo guardar(Tramo tramo) {
        return tramoRepository.save(tramo);
    }

    public void eliminar(Long id) {
        tramoRepository.deleteById(id);
    }


}
