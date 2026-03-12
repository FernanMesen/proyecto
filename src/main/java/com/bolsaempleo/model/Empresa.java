package com.bolsaempleo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 200)
    private String localizacion;

    @Column(length = 20)
    private String telefono;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String descripcion;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Puesto> puestos;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getLocalizacion() { return localizacion; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<Puesto> getPuestos() { return puestos; }
    public void setPuestos(List<Puesto> puestos) { this.puestos = puestos; }
}