package com.codigo.ms_examen.service;

import com.codigo.ms_examen.aggregates.request.PersonaRequest;
import com.codigo.ms_examen.aggregates.request.UsuarioRequest;
import com.codigo.ms_examen.aggregates.response.UsuarioResponse;
import com.codigo.ms_examen.entities.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    UserDetailsService userDetailsService();

    List<Usuario> listarUsuarios();

    UsuarioResponse buscarUsuarioDni(String dni);

    Usuario actualizarUsuario(Long id, UsuarioRequest request);

    boolean eliminarUsuario(Long dni);

    Optional<Usuario> findById(Long id);
}
