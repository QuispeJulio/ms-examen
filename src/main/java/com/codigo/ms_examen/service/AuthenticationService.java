package com.codigo.ms_examen.service;

import com.codigo.ms_examen.aggregates.request.PersonaRequest;
import com.codigo.ms_examen.aggregates.request.SignInRequest;
import com.codigo.ms_examen.aggregates.request.SignUpRequest;
import com.codigo.ms_examen.aggregates.response.SignInResponse;
import com.codigo.ms_examen.entities.Usuario;

import java.util.List;

public interface AuthenticationService {
    //SIGNUP -> INSCRIBIRSE
    Usuario signUpUser(PersonaRequest personaRequest);

    Usuario signUpAdmin(PersonaRequest personaRequest);
    
    //SIGNIN -> INICIAR SESION
    SignInResponse signIn(SignInRequest signInRequest);
}
