package com.codigo.ms_examen.controller;

import com.codigo.ms_examen.aggregates.request.PersonaRequest;
import com.codigo.ms_examen.aggregates.request.SignInRequest;
import com.codigo.ms_examen.aggregates.request.UsuarioRequest;
import com.codigo.ms_examen.aggregates.response.SignInResponse;
import com.codigo.ms_examen.aggregates.response.UsuarioResponse;
import com.codigo.ms_examen.controller.advice.AuthenticationException;
import com.codigo.ms_examen.controller.advice.CustomIllegalArgumentException;
import com.codigo.ms_examen.controller.advice.ResourceNotFoundException;
import com.codigo.ms_examen.entities.Usuario;
import com.codigo.ms_examen.service.AuthenticationService;
import com.codigo.ms_examen.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;
    private final UsuarioService usuarioService;

    @PostMapping("/registeradmin")
    public ResponseEntity<Usuario> signUpAdmin(@RequestBody PersonaRequest personaRequest) {
        return ResponseEntity.ok(authenticationService.signUpAdmin(personaRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> signUpUser(@RequestBody PersonaRequest personaRequest) {
        System.out.println("usuario registrado");
        if (personaRequest.getEmail() == null || personaRequest.getEmail().isEmpty()) {
            throw new CustomIllegalArgumentException("El email no puede estar vacío.");
        } else {
            return ResponseEntity.ok(authenticationService.signUpUser(personaRequest));
        }
    }

    @GetMapping("/login")
    public ResponseEntity<SignInResponse> signin(@RequestBody SignInRequest signInRequest) {
        if (signInRequest.getPassword() == null || signInRequest.getPassword().isEmpty()) {
            throw new AuthenticationException("Contraseña incorrecta");
        }
        return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }

    @GetMapping("/{dni}")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable("dni") String dni) {
        UsuarioResponse usuarioResponse = usuarioService.buscarUsuarioDni(dni);
        return ResponseEntity.ok(usuarioResponse);

    }

    @GetMapping("/")
    public ResponseEntity<List<Usuario>> buscarUsuario() {
        List<Usuario> usuarioResponse = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarioResponse);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable("id") Long id, @RequestBody UsuarioRequest usuarioRequest) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioRequest));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("id") Long id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content si la eliminación fue exitosa
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found si no se encontró el usuario
        }
    }

}
