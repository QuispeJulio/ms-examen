package com.codigo.ms_examen.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaRequest {
    private String numdoc;
    private String email;
    private String password;
}
