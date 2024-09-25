package com.example.backend.Repository;

import java.util.Optional;

import org.hibernate.mapping.List;
import org.hibernate.mapping.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Oficina;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class OficinaRepository implements LocalRepository<Oficina, Integer> {

    //Optional<Oficina> findByCodigo(String codigo);
    private Map<Long, Oficina> dataStore = new HashMap<>();
    private Long currentId = 1L;  // Para simular el autoincremento

    @Override
    public Oficina save(Oficina oficina) {
        if (oficina.getId_oficina() == null) {
            oficina.setId_oficina(currentId++); // Asignar un ID único simulado
        }
        dataStore.put(oficina.getId_oficina(), oficina);
        return oficina;
    }

    @Override
    public Optional<Oficina> findById(Long id) {
        return Optional.ofNullable(dataStore.get(id));
    }

    @Override
    public List<Oficina> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    @Override
    public void deleteById(Long id) {
        dataStore.remove(id);
    }
}
