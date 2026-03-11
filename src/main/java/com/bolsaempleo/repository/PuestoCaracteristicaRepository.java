package com.bolsaempleo.repository;

import com.bolsaempleo.model.Puesto;
import com.bolsaempleo.model.PuestoCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PuestoCaracteristicaRepository extends JpaRepository<PuestoCaracteristica, Long> {
    List<PuestoCaracteristica> findByPuesto(Puesto puesto);
    void deleteByPuesto(Puesto puesto);
}
