package com.codigo.ms_examen.service.impl;

import com.codigo.ms_examen.aggregates.constants.Constants;
import com.codigo.ms_examen.aggregates.request.PersonaRequest;
import com.codigo.ms_examen.aggregates.request.UsuarioRequest;
import com.codigo.ms_examen.aggregates.response.UsuarioResponse;
import com.codigo.ms_examen.entities.Rol;
import com.codigo.ms_examen.entities.Usuario;
import com.codigo.ms_examen.redis.RedisService;
import com.codigo.ms_examen.repository.UsuarioRepository;
import com.codigo.ms_examen.service.UsuarioService;
import com.codigo.ms_examen.util.Util;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RedisService redisService;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                return usuarioRepository.findByEmail(username).orElseThrow(
                        () -> new UsernameNotFoundException("USUARIO NO ENCONTRADO"));
            }
        };
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public UsuarioResponse buscarUsuarioDni(String dni) {
        //API:CONSUMO:EXTERNA:72032008
        String redisInfo = redisService.getDataDesdeRedis(Constants.REDIS_KEY_API_PERSON + dni);
        if (Objects.nonNull(redisInfo)) {
            return Util.convertirDesdeString(redisInfo, UsuarioResponse.class);
        } else {
            //consulta a la BD
            Usuario usuarioEntity = usuarioRepository.findByNumeroDocumento(dni).get();
            UsuarioResponse usuarioResponse = new UsuarioResponse();
            usuarioResponse.setId(usuarioEntity.getId());
            usuarioResponse.setNombres(usuarioEntity.getNombres());
            usuarioResponse.setApellidoPaterno(usuarioEntity.getApellidoPaterno());
            usuarioResponse.setApellidoMaterno(usuarioEntity.getApellidoMaterno());
            usuarioResponse.setTipoDocumento(usuarioEntity.getTipoDocumento());
            usuarioResponse.setNumeroDocumento(usuarioEntity.getNumeroDocumento());
            usuarioResponse.setDigitoVerificador(usuarioEntity.getDigitoVerificador());
            usuarioResponse.setEmail(usuarioEntity.getEmail());
            Set<Rol> roles = usuarioEntity.getRoles();

            if (roles != null) {
                List<String> rolesNombres = roles.stream()
                        .map(Rol::getNombreRol) // Obtener solo los nombres de los roles
                        .collect(Collectors.toList());
                usuarioResponse.setRol(rolesNombres);
            }
            String dataForRedis = Util.convertirAString(usuarioResponse);
            redisService.guardarEnRedis(Constants.REDIS_KEY_API_PERSON + dni, dataForRedis, Constants.REDIS_EXP);
            return usuarioResponse;
        }
    }

    @Override
    public Usuario actualizarUsuario(Long id, UsuarioRequest request) {
        // Buscar el usuario por ID
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuario.setNombres(request.getNombres());
        usuario.setApellidoPaterno(request.getApellidoPaterno());
        usuario.setApellidoMaterno(request.getApellidoMaterno());
        usuario.setTipoDocumento(request.getTipoDocumento());
        usuario.setNumeroDocumento(request.getNumeroDocumento());
        usuario.setDigitoVerificador(request.getDigitoVerificador());
        usuario.setEmail(request.getEmail());

        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
}
