package com.bolsaempleo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "oferente")
public class Oferente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String identificacion;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "primer_apellido", length = 100)
    private String primerApellido;

    @Column(length = 80)
    private String nacionalidad;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String residencia;

    @Column(name = "cv_path", length = 300)
    private String cvPath;

    @OneToMany(mappedBy = "oferente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Habilidad> habilidades;

    public String getNombreCompleto() {
        return nombre + (primerApellido != null ? " " + primerApellido : "");
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getResidencia() { return residencia; }
    public void setResidencia(String residencia) { this.residencia = residencia; }

    public String getCvPath() { return cvPath; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }

    public List<Habilidad> getHabilidades() { return habilidades; }
    public void setHabilidades(List<Habilidad> habilidades) { this.habilidades = habilidades; }
}