package com.example.backend.Service;

import com.example.backend.models.Region;
import com.example.backend.Repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    public ArrayList<Region> obtenerTodas() {
        return new ArrayList<>(regionRepository.findAll());
    }

    public Optional<Region> obtenerPorId(Long id) {// public Optional<Region> obtenerPorId(Long id) {
        return regionRepository.findById(id);
    }

    public Region guardar(Region region) {
        return regionRepository.save(region);
    }

    public void eliminar(Long id) {
        regionRepository.deleteById(id);
    }
}
