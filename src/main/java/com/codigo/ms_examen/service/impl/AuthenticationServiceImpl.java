package com.codigo.ms_examen.service.impl;

import com.codigo.ms_examen.aggregates.constants.Constants;
import com.codigo.ms_examen.aggregates.request.PersonaRequest;
import com.codigo.ms_examen.aggregates.request.SignInRequest;
import com.codigo.ms_examen.aggregates.response.ReniecResponse;
import com.codigo.ms_examen.aggregates.response.SignInResponse;

import com.codigo.ms_examen.client.ReniecClient;
import com.codigo.ms_examen.entities.Rol;
import com.codigo.ms_examen.entities.Role;
import com.codigo.ms_examen.entities.Usuario;
import com.codigo.ms_examen.repository.RolRepository;
import com.codigo.ms_examen.repository.UsuarioRepository;
import com.codigo.ms_examen.service.AuthenticationService;
import com.codigo.ms_examen.service.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ReniecClient reniecClient;

    @Value("${token.api}")
    private String tokenapi;

    public AuthenticationServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, AuthenticationManager authenticationManager, JwtService jwtService, ReniecClient reniecClient) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.reniecClient = reniecClient;
    }

    @Override
    @Transactional
    public Usuario signUpUser(PersonaRequest personaRequest) {
        Usuario usuario = getUsuarioEntity(personaRequest);
        //ASIGANDO ROL QUE EXISTA
        //Rol userRol = getRoles(Role.USER);
        //ASIGNADO FINALMENTE EL ROL ENCONTRADO AL USUARIO:
        usuario.setRoles(Collections.singleton(getRoles(Role.USER)));
        usuario.setEmail(personaRequest.getEmail());
        usuario.setPassword(new BCryptPasswordEncoder().encode(personaRequest.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario signUpAdmin(PersonaRequest personaRequest) {
        Usuario admin = getUsuarioEntity(personaRequest);

        //ASIGNADO FINALMENTE EL ROL ENCONTRADO AL USUARIO:
        admin.setRoles(Collections.singleton(getRoles(Role.ADMIN)));
        admin.setEmail(personaRequest.getEmail());
        admin.setPassword(new BCryptPasswordEncoder().encode(personaRequest.getPassword()));
        return usuarioRepository.save(admin);
    }

    private Usuario getUsuarioEntity(PersonaRequest personaRequest) {
        Usuario usuarioEntity = new Usuario();

        ReniecResponse response = executionReniec(personaRequest.getNumdoc());
        if (Objects.nonNull(response)) {
            usuarioEntity.setNombres(response.getNombres());
            usuarioEntity.setApellidoPaterno(response.getApellidoPaterno());
            usuarioEntity.setApellidoMaterno(response.getApellidoMaterno());
            usuarioEntity.setNumeroDocumento(response.getNumeroDocumento());
            usuarioEntity.setTipoDocumento(response.getTipoDocumento());
            usuarioEntity.setDigitoVerificador(response.getDigitoVerificador());
            //usuarioEntity.setUsua_crea(Constants.USU_CREA);
            //usuarioEntity.setDate_crea(new Timestamp(System.currentTimeMillis()));
            usuarioEntity.setIsAccountNonExpired(Constants.ESTADO_ACTIVO);
            usuarioEntity.setIsAccountNonLocked(Constants.ESTADO_ACTIVO);
            usuarioEntity.setIsCredentialsNonExpired(Constants.ESTADO_ACTIVO);
            usuarioEntity.setIsEnabled(Constants.ESTADO_ACTIVO);
            return usuarioEntity;
        }

        return null;

    }

    private ReniecResponse executionReniec(String documento) {
        String auth = "Bearer " + tokenapi;
        ReniecResponse response = reniecClient.getPersonaReniec(documento, auth);
        return response;
    }

    @Override
    public SignInResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.getEmail(), signInRequest.getPassword()));
        var user = usuarioRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("ERROR USUARIO NO ENCONTRADO"));
        var token = jwtService.generateToken(user);
        SignInResponse response = new SignInResponse();
        response.setToken(token);
        return response;
    }

    private Rol getRoles(Role rolBuscado) {
        return rolRepository.findByNombreRol(rolBuscado.name())
                .orElseThrow(() -> new RuntimeException("EL ROL BSUCADO NO EXISTE : "
                        + rolBuscado.name()));
    }
}
