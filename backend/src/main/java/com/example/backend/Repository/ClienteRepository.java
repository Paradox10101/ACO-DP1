package com.example.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Cliente;


public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByCodigo(String codigo);//SE TIENE QUE IMPLEMENTAR ESTE METODO --> AUN FALTA
}
