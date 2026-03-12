package com.bolsaempleo.repository;

import com.bolsaempleo.model.Aplicacion;
import com.bolsaempleo.model.Oferente;
import com.bolsaempleo.model.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AplicacionRepository extends JpaRepository<Aplicacion, Long> {

    List<Aplicacion> findByPuesto(Puesto puesto);

    List<Aplicacion> findByPuestoId(Long puestoId);

    boolean existsByPuestoAndOferente(Puesto puesto, Oferente oferente);

    List<Aplicacion> findByPuestoEmpresaId(Long empresaId);
}