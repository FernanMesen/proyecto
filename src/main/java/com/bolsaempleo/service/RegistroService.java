package com.bolsaempleo.service;

import com.bolsaempleo.model.*;
import com.bolsaempleo.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroService {

    private final UsuarioRepository usuarioRepo;
    private final EmpresaRepository empresaRepo;
    private final OferenteRepository oferenteRepo;
    private final PasswordEncoder encoder;

    public RegistroService(UsuarioRepository usuarioRepo,
                           EmpresaRepository empresaRepo,
                           OferenteRepository oferenteRepo,
                           PasswordEncoder encoder) {
        this.usuarioRepo  = usuarioRepo;
        this.empresaRepo  = empresaRepo;
        this.oferenteRepo = oferenteRepo;
        this.encoder      = encoder;
    }

    @Transactional
    public void registrarEmpresa(String correo, String clave,
                                 String nombre, String localizacion,
                                 String telefono, String descripcion) {

        if (usuarioRepo.existsByCorreo(correo))
            throw new IllegalArgumentException("El correo ya está registrado.");

        Usuario u = new Usuario();
        u.setCorreo(correo);
        u.setClave(encoder.encode(clave));
        u.setRol(Usuario.Rol.EMPRESA);
        u.setActivo(false);
        usuarioRepo.save(u);

        Empresa e = new Empresa();
        e.setUsuario(u);
        e.setNombre(nombre);
        e.setLocalizacion(localizacion);
        e.setTelefono(telefono);
        e.setDescripcion(descripcion);
        empresaRepo.save(e);
    }

    @Transactional
    public void registrarOferente(String correo, String clave,
                                  String identificacion, String nombre,
                                  String primerApellido, String nacionalidad,
                                  String telefono, String residencia) {

        if (usuarioRepo.existsByCorreo(correo))
            throw new IllegalArgumentException("El correo ya está registrado.");

        Usuario u = new Usuario();
        u.setCorreo(correo);
        u.setClave(encoder.encode(clave));
        u.setRol(Usuario.Rol.OFERENTE);
        u.setActivo(false);
        usuarioRepo.save(u);

        Oferente o = new Oferente();
        o.setUsuario(u);
        o.setIdentificacion(identificacion);
        o.setNombre(nombre);
        o.setPrimerApellido(primerApellido);
        o.setNacionalidad(nacionalidad);
        o.setTelefono(telefono);
        o.setResidencia(residencia);
        oferenteRepo.save(o);
    }
}
