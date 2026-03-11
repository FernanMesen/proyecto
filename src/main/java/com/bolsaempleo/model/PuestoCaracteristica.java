package com.bolsaempleo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "puesto_caracteristica",
       uniqueConstraints = @UniqueConstraint(columnNames = {"puesto_id", "caracteristica_id"}))
public class PuestoCaracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "caracteristica_id", nullable = false)
    private Caracteristica caracteristica;

    @Min(1) @Max(5)
    @Column(name = "nivel_minimo", nullable = false)
    private int nivelMinimo;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Puesto getPuesto() { return puesto; }
    public void setPuesto(Puesto puesto) { this.puesto = puesto; }

    public Caracteristica getCaracteristica() { return caracteristica; }
    public void setCaracteristica(Caracteristica caracteristica) { this.caracteristica = caracteristica; }

    public int getNivelMinimo() { return nivelMinimo; }
    public void setNivelMinimo(int nivelMinimo) { this.nivelMinimo = nivelMinimo; }
}
