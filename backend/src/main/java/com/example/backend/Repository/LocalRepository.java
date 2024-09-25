
package com.example.backend.Repository;

import java.util.List;
import java.util.Optional;

public interface LocalRepository<T, ID> {
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
}
