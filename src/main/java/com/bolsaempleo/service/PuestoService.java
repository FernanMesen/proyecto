package com.bolsaempleo.service;

import com.bolsaempleo.model.*;
import com.bolsaempleo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PuestoService {

    private final PuestoRepository puestoRepo;
    private final PuestoCaracteristicaRepository pcRepo;
    private final EmpresaRepository empresaRepo;
    private final OferenteRepository oferenteRepo;
    private final HabilidadRepository habilidadRepo;

    public PuestoService(PuestoRepository puestoRepo,
                         PuestoCaracteristicaRepository pcRepo,
                         EmpresaRepository empresaRepo,
                         OferenteRepository oferenteRepo,
                         HabilidadRepository habilidadRepo) {
        this.puestoRepo    = puestoRepo;
        this.pcRepo        = pcRepo;
        this.empresaRepo   = empresaRepo;
        this.oferenteRepo  = oferenteRepo;
        this.habilidadRepo = habilidadRepo;
    }

    public List<Puesto> ultimos5Publicos() {
        return puestoRepo.findTop5ByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto.PUBLICO);
    }

    public List<Puesto> buscarTodos(boolean puedeVerPrivados) {
        if (puedeVerPrivados)
            return puestoRepo.findByActivoTrue();
        return puestoRepo.findByTipoAndActivoTrue(Puesto.TipoPuesto.PUBLICO);
    }

    public List<Puesto> buscarPorCaracteristicas(List<Long> caracteristicaIds, boolean puedeVerPrivados) {
        List<Puesto> base = buscarTodos(puedeVerPrivados);
        if (caracteristicaIds == null || caracteristicaIds.isEmpty()) return base;

        return base.stream()
            .filter(p -> p.getCaracteristicas().stream()
                .anyMatch(pc -> caracteristicaIds.contains(pc.getCaracteristica().getId())))
            .toList();
    }

    @Transactional
    public Puesto publicarPuesto(Long usuarioId, String descripcion, BigDecimal salario,
                                 Puesto.TipoPuesto tipo,
                                 List<Long> caracteristicaIds, List<Integer> niveles,
                                 CaracteristicaRepository carRepo) {

        Empresa empresa = empresaRepo.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        Puesto puesto = new Puesto();
        puesto.setEmpresa(empresa);
        puesto.setDescripcion(descripcion);
        puesto.setSalario(salario);
        puesto.setTipo(tipo);
        puestoRepo.save(puesto);

        for (int i = 0; i < caracteristicaIds.size(); i++) {
            Long carId = caracteristicaIds.get(i);
            if (carId == null || carId == 0) continue;

            Caracteristica c = carRepo.findById(carId).orElseThrow();
            PuestoCaracteristica pc = new PuestoCaracteristica();
            pc.setPuesto(puesto);
            pc.setCaracteristica(c);
            pc.setNivelMinimo(niveles.get(i));
            pcRepo.save(pc);
        }
        return puesto;
    }

    @Transactional
    public void desactivarPuesto(Long puestoId, Long usuarioId) {
        Puesto p = puestoRepo.findById(puestoId)
            .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
        if (!p.getEmpresa().getUsuario().getId().equals(usuarioId))
            throw new SecurityException("No autorizado");
        p.setActivo(false);
        puestoRepo.save(p);
    }

    public void activarPuesto(Long puestoId, Long usuarioId) {
        Puesto p = puestoRepo.findById(puestoId)
            .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
        if (!p.getEmpresa().getUsuario().getId().equals(usuarioId))
            throw new SecurityException("No autorizado");
        p.setActivo(true);
        puestoRepo.save(p);
    }

    public List<ResultadoCandidato> buscarCandidatos(Long puestoId) {
        Puesto puesto = puestoRepo.findById(puestoId)
            .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));

        List<PuestoCaracteristica> requeridas = pcRepo.findByPuesto(puesto);
        List<Oferente> todos = oferenteRepo.findAll();
        List<ResultadoCandidato> resultados = new ArrayList<>();

        for (Oferente oferente : todos) {
            if (!oferente.getUsuario().isActivo()) continue;

            int cumplidos = 0;
            for (PuestoCaracteristica req : requeridas) {
                Optional<Habilidad> hab = habilidadRepo
                    .findByOferenteAndCaracteristicaId(oferente, req.getCaracteristica().getId());
                if (hab.isPresent() && hab.get().getNivel() >= req.getNivelMinimo())
                    cumplidos++;
            }
            if (cumplidos > 0) {
                double pct = requeridas.isEmpty() ? 100.0 : (cumplidos * 100.0) / requeridas.size();
                resultados.add(new ResultadoCandidato(oferente, cumplidos, requeridas.size(), pct));
            }
        }

        resultados.sort(Comparator.comparingDouble(ResultadoCandidato::getPorcentaje).reversed());
        return resultados;
    }

    public static class ResultadoCandidato {
        private final Oferente oferente;
        private final int cumplidos;
        private final int totalRequeridos;
        private final double porcentaje;

        public ResultadoCandidato(Oferente oferente, int cumplidos, int totalRequeridos, double porcentaje) {
            this.oferente        = oferente;
            this.cumplidos       = cumplidos;
            this.totalRequeridos = totalRequeridos;
            this.porcentaje      = porcentaje;
        }

        public Oferente getOferente()           { return oferente; }
        public int getCumplidos()               { return cumplidos; }
        public int getTotalRequeridos()         { return totalRequeridos; }
        public double getPorcentaje()           { return porcentaje; }
        public String getPorcentajeFormateado() { return String.format("%.2f%%", porcentaje); }
    }
}