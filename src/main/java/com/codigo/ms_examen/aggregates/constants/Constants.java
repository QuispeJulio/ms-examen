package com.codigo.ms_examen.aggregates.constants;

public class Constants {
    public static final Boolean ESTADO_ACTIVO = true;
    public static final String CLAIM_ROLE = "rol";
    //    public static final String ENPOINTS_PERMIT = "/api/authentication/v1/**";
    public static final String ENPOINTS_PERMIT_REGISTER = "/api/v1/users/register";
    public static final String ENPOINTS_PERMIT_REGISTER_ADMIN = "/api/v1/users/registeradmin";
    public static final String ENPOINTS_PERMIT_LOGIN = "/api/v1/users/login";
    public static final String ENPOINTS_USER_DNI = "/api/v1/users/{dni}";
    public static final String ENPOINTS_USER_LISTA = "/api/v1/users/";
    public static final String ENPOINTS_ADMIN = "/api/v1/users/**";
    public static final String CLAVE_AccountNonExpired = "isAccountNonExpired";
    public static final String CLAVE_AccountNonLocked = "isAccountNonLocked";
    public static final String CLAVE_CredentialsNonExpired = "isCredentialsNonExpired";
    public static final String CLAVE_Enabled = "isEnabled";

    public static final Integer REDIS_EXP = 5;
    public static final String REDIS_KEY_API_PERSON = "MS:APIS:EXTERNAS:";

    public static final String ERROR_NOT_FOUND = " RECURSO SOLICITADO NO ENCONTRADO ";
    public static final String ERROR_NOT_VALID = " DATOS NO VALIDOS ";
    public static final String ERROR_UNAUTHORIZED = " NO TIENE AUTORIZACION  ";

}
