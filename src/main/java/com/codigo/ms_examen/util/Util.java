package com.codigo.ms_examen.util;

import com.codigo.ms_examen.aggregates.response.ReniecResponse;
import com.codigo.ms_examen.aggregates.response.UsuarioResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
    public static String convertirAString(UsuarioResponse usuarioResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(usuarioResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertirDesdeString(String json, Class<T> tipoClase) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, tipoClase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
