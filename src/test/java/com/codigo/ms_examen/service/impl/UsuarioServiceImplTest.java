package com.codigo.ms_examen.service.impl;

import com.codigo.ms_examen.aggregates.constants.Constants;
import com.codigo.ms_examen.aggregates.request.UsuarioRequest;
import com.codigo.ms_examen.aggregates.response.UsuarioResponse;
import com.codigo.ms_examen.entities.Rol;
import com.codigo.ms_examen.entities.Usuario;
import com.codigo.ms_examen.redis.RedisService;
import com.codigo.ms_examen.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UsuarioExistente() {
        // Arrange
        String username = "julio@gmail.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(username);
        usuario.setNombres("julio");

        when(usuarioRepository.findByEmail(username)).thenReturn(Optional.of(usuario));

        // Act
        UserDetailsService userDetailsService = usuarioService.userDetailsService();
        UserDetails resultado = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(resultado);
        assertEquals(username, resultado.getUsername());
    }

    @Test
    void loadUserByUsername_UsuarioNoExistente() {
        // Arrange
        String username = "carlos@gmail.com";

        when(usuarioRepository.findByEmail(username)).thenReturn(Optional.empty());

        // Act & Assert
        UserDetailsService userDetailsService = usuarioService.userDetailsService();
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(username)
        );

        assertEquals("USUARIO NO ENCONTRADO", exception.getMessage());
    }

    @Test
    void findById_UsuarioExistente() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombres("Julio");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.findById(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Julio", resultado.get().getNombres());
    }

    @Test
    void findById_UsuarioNoExistente() {
        // Arrange
        Long id = 1L;

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.findById(id);

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    void buscarUsuarioDni_DatosEnRedis() {
        // Arrange
        String dni = "42408093";
        String redisData = "{\"id\":1,\"nombres\":\"Julio\",\"apellidoPaterno\":\"Quispe\",\"apellidoMaterno\":\"Mamani\"}";
        when(redisService.getDataDesdeRedis(Constants.REDIS_KEY_API_PERSON + dni)).thenReturn(redisData);

        // Act
        UsuarioResponse response = usuarioService.buscarUsuarioDni(dni);

        // Assert
        assertNotNull(response);
        assertEquals("Julio", response.getNombres());
        assertEquals("Quispe", response.getApellidoPaterno());
        verify(usuarioRepository, never()).findByNumeroDocumento(anyString()); // No se debe consultar la BD
    }

    @Test
    void buscarUsuarioDni_ConsultaBD() {
        // Arrange
        String dni = "42408093";
        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setId(1L);
        usuarioEntity.setNombres("Julio");
        usuarioEntity.setApellidoPaterno("Quispe");
        usuarioEntity.setApellidoMaterno("Mamani");
        usuarioEntity.setTipoDocumento("DNI");
        usuarioEntity.setNumeroDocumento(dni);
        usuarioEntity.setDigitoVerificador("1");
        usuarioEntity.setEmail("julio@gmail.com");

        Set<Rol> roles = new HashSet<>();
        Rol rol1 = new Rol();
        rol1.setNombreRol("ADMIN");
        roles.add(rol1);
        usuarioEntity.setRoles(roles);

        when(redisService.getDataDesdeRedis(Constants.REDIS_KEY_API_PERSON + dni)).thenReturn(null);
        when(usuarioRepository.findByNumeroDocumento(dni)).thenReturn(Optional.of(usuarioEntity));

        // Act
        UsuarioResponse response = usuarioService.buscarUsuarioDni(dni);

        // Assert
        assertNotNull(response);
        assertEquals("Julio", response.getNombres());
        assertEquals("Quispe", response.getApellidoPaterno());
        assertEquals("ADMIN", response.getRol().get(0)); // Verifica el rol
        verify(redisService).guardarEnRedis(anyString(), anyString(), anyInt()); // Verifica que los datos se guarden en Redis
    }

    @Test
    void buscarUsuarioDni_UsuarioNoEncontrado() {
        // Arrange
        String dni = "42408093";
        when(redisService.getDataDesdeRedis(Constants.REDIS_KEY_API_PERSON + dni)).thenReturn(null);
        when(usuarioRepository.findByNumeroDocumento(dni)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> usuarioService.buscarUsuarioDni(dni));
        verify(redisService, never()).guardarEnRedis(anyString(), anyString(), anyInt()); // No se debe guardar en Redis
    }

    @Test
    void buscarUsuarioDni_DatosNoEnRedis_RolesNulos() {
        // Arrange
        String dni = "42408093";
        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setId(1L);
        usuarioEntity.setNombres("Julio");
        usuarioEntity.setApellidoPaterno("Quispe");
        usuarioEntity.setApellidoMaterno("Mamani");
        usuarioEntity.setTipoDocumento("DNI");
        usuarioEntity.setNumeroDocumento(dni);
        usuarioEntity.setDigitoVerificador("1");
        usuarioEntity.setEmail("julio@gmail.com");
        usuarioEntity.setRoles(null); // Sin roles

        when(redisService.getDataDesdeRedis(Constants.REDIS_KEY_API_PERSON + dni)).thenReturn(null);
        when(usuarioRepository.findByNumeroDocumento(dni)).thenReturn(Optional.of(usuarioEntity));

        // Act
        UsuarioResponse response = usuarioService.buscarUsuarioDni(dni);

        // Assert
        assertNotNull(response);
        assertEquals("Julio", response.getNombres());
        assertNull(response.getRol());
        verify(redisService).guardarEnRedis(anyString(), anyString(), anyInt());
    }


    @Test
    void actualizarUsuario_UsuarioExistente() {
        // Arrange
        Long id = 1L;
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(id);
        usuarioExistente.setNombres("Julio");
        usuarioExistente.setApellidoPaterno("Quispe");
        usuarioExistente.setApellidoMaterno("Mamani");
        usuarioExistente.setNumeroDocumento("42408093");
        usuarioExistente.setTipoDocumento("DNI");
        usuarioExistente.setDigitoVerificador("1");
        usuarioExistente.setEmail("julio@gmail.com");

        UsuarioRequest request = new UsuarioRequest();
        request.setNombres("Julio");
        request.setApellidoPaterno("Quispe");
        request.setApellidoMaterno("Mamani");
        request.setTipoDocumento("DNI");
        request.setNumeroDocumento("42408093");
        request.setDigitoVerificador("2");
        request.setEmail("quispe@gmail.com");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        // Act
        Usuario actualizado = usuarioService.actualizarUsuario(id, request);

        // Assert
        assertNotNull(actualizado);
        assertEquals("Julio", actualizado.getNombres());
        assertEquals("Quispe", actualizado.getApellidoPaterno());
        assertEquals("Mamani", actualizado.getApellidoMaterno());
        assertEquals("DNI", actualizado.getTipoDocumento());
        assertEquals("42408093", actualizado.getNumeroDocumento());
        assertEquals("2", actualizado.getDigitoVerificador());
        assertEquals("quispe@gmail.com", actualizado.getEmail());

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_UsuarioNoExistente() {
        // Arrange
        Long id = 1L;
        UsuarioRequest request = new UsuarioRequest();

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(id, request);
        });

        assertEquals("Usuario no encontrado con ID: " + id, exception.getMessage());
        verify(usuarioRepository).findById(id);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }


    @Test
    void eliminarUsuario_UsuarioExistente() {
        // Arrange
        Long id = 1L;

        when(usuarioRepository.existsById(id)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(id);

        // Act
        boolean resultado = usuarioService.eliminarUsuario(id);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void eliminarUsuario_UsuarioNoExistente() {
        // Arrange
        Long id = 1L;

        when(usuarioRepository.existsById(id)).thenReturn(false);

        // Act
        boolean resultado = usuarioService.eliminarUsuario(id);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void listarUsuarios_UsuariosExistentes() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setNombres("Julio");

        Usuario usuario2 = new Usuario();
        usuario2.setNombres("Maria");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        // Act
        List<Usuario> resultado = usuarioService.listarUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Julio", resultado.get(0).getNombres());
        assertEquals("Maria", resultado.get(1).getNombres());
    }

    @Test
    void listarUsuarios_SinUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Usuario> resultado = usuarioService.listarUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }
}