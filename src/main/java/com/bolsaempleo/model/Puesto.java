package com.bolsaempleo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "puesto")
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotBlank
    @Column(nullable = false, length = 300)
    private String descripcion;

    @Column(precision = 15, scale = 2)
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoPuesto tipo = TipoPuesto.PUBLICO;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "puesto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PuestoCaracteristica> caracteristicas;

    public enum TipoPuesto { PUBLICO, PRIVADO }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public TipoPuesto getTipo() { return tipo; }
    public void setTipo(TipoPuesto tipo) { this.tipo = tipo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public List<PuestoCaracteristica> getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(List<PuestoCaracteristica> caracteristicas) { this.caracteristicas = caracteristicas; }
}
