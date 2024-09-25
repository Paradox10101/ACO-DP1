package com.example.backend.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Oficina;
import org.springframework.stereotype.Repository;
@Repository
public interface OficinaRepository extends JpaRepository<Oficina, Long> {
    // Puedes agregar métodos adicionales si es necesario
    //Oficina findByNombre(String nombre);
}