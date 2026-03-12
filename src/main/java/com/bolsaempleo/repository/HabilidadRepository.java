package com.bolsaempleo.repository;

import com.bolsaempleo.model.Habilidad;
import com.bolsaempleo.model.Oferente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HabilidadRepository extends JpaRepository<Habilidad, Long> {
    List<Habilidad> findByOferente(Oferente oferente);
    Optional<Habilidad> findByOferenteAndCaracteristicaId(Oferente oferente, Long caracteristicaId);
}