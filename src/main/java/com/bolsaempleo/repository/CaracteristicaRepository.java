package com.bolsaempleo.repository;

import com.bolsaempleo.model.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Long> {
    List<Caracteristica> findByPadreIsNull();
    List<Caracteristica> findByPadreIsNotNull();
}
