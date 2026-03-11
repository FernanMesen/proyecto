package com.bolsaempleo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "aplicacion")
public class Aplicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    @ManyToOne
    @JoinColumn(name = "oferente_id")
    private Oferente oferente;

    @Column(length = 200)
    private String nombreInvitado;

    @Column(length = 200)
    private String correoInvitado;

    @Column(length = 50)
    private String telefonoInvitado;

    @Column(length = 500)
    private String mensaje;

    @Column(name = "cv_invitado", length = 300)
    private String cvInvitado;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDateTime fechaAplicacion = LocalDateTime.now();


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Puesto getPuesto() { return puesto; }
    public void setPuesto(Puesto puesto) { this.puesto = puesto; }

    public Oferente getOferente() { return oferente; }
    public void setOferente(Oferente oferente) { this.oferente = oferente; }

    public String getNombreInvitado() { return nombreInvitado; }
    public void setNombreInvitado(String nombreInvitado) { this.nombreInvitado = nombreInvitado; }

    public String getCorreoInvitado() { return correoInvitado; }
    public void setCorreoInvitado(String correoInvitado) { this.correoInvitado = correoInvitado; }

    public String getTelefonoInvitado() { return telefonoInvitado; }
    public void setTelefonoInvitado(String telefonoInvitado) { this.telefonoInvitado = telefonoInvitado; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getCvInvitado() { return cvInvitado; }
    public void setCvInvitado(String cvInvitado) { this.cvInvitado = cvInvitado; }

    public LocalDateTime getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDateTime fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }

    public String getNombreDisplay() {
        if (oferente != null) return oferente.getNombreCompleto();
        return nombreInvitado != null ? nombreInvitado : "Invitado";
    }

    public String getCorreoDisplay() {
        if (oferente != null) return oferente.getUsuario().getCorreo();
        return correoInvitado;
    }
}
