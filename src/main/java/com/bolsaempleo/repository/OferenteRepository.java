package com.bolsaempleo.repository;

import com.bolsaempleo.model.Oferente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OferenteRepository extends JpaRepository<Oferente, Long> {
    Optional<Oferente> findByUsuarioId(Long usuarioId);
    List<Oferente> findByUsuarioActivoFalse();
}