package com.bolsaempleo.repository;

import com.bolsaempleo.model.Empresa;
import com.bolsaempleo.model.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PuestoRepository extends JpaRepository<Puesto, Long> {

    List<Puesto> findByEmpresa(Empresa empresa);

    List<Puesto> findTop5ByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto tipo);

    List<Puesto> findByTipoAndActivoTrue(Puesto.TipoPuesto tipo);

    List<Puesto> findByActivoTrue();

    @Query("SELECT p FROM Puesto p " +
           "WHERE MONTH(p.fechaRegistro) = :mes " +
           "AND YEAR(p.fechaRegistro) = :anio " +
           "ORDER BY p.fechaRegistro")
    List<Puesto> findByMesYAnio(@Param("mes") int mes, @Param("anio") int anio);
}
