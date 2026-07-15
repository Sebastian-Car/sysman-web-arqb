/*-
 * APIAutoServicio.java
 *
 * 1.0
 * 
 * 18/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 18/10/2018
 * @author jgomez
 *
 */
public enum APIAutoServicioEnum {

    MSG_APIAUTOSERVICIO_URLERRADA("MSG_APIAUTOSERVICIO_URLERRADA"),

    MSG_APIAUTOSERVICIO_SERVICIONULL("MSG_APIAUTOSERVICIO_SERVICIONULL"),

    MSG_APIAUTOSERVICIO_CONECCION("MSG_APIAUTOSERVICIO_CONECCION"),

    MSG_APIAUTOSERVICIO_NORETORNA("MSG_APIAUTOSERVICIO_NORETORNA"),

    KEY_DOCUMENTO_FIRMADO("KEY_DOCUMENTO_FIRMADO"),

    REEMPLAZO_URL("s$url$s"),

    REEMPLAZO_CONE("s$conexion$s"),

    REEMPLZAO_SIG(" -> "),

    CONTENT_TYPE("Content-Type"),
    
    CONTENTTYPE("contentType"),

    AUTHORIZATION("Authorization"),

    ACCEPT("Accept"),

    METHOD("Method"),

    APPLICATIONSJON("application/json"),
    
    POST("POST"),
    
    GET("GET"),
    
    KEY_LOG_URL("URL-> {}"),
    
    KEY_VACIO(""),
    
    CACHE_CONTROL("Cache-Control"),
    
    CACHE_CONTROL_MINUS("cache-control"),
    
    NO_CACHE("no-cache"),
    
    DOS_PUNTOS(":"),
    
    MSG_ERR_APIDEUDAJUDICIAL_ABRIR_CONEXION("MSG_ERR_APIDEUDAJUDICIAL_ABRIR_CONEXION"),
    
    MSG_ERR_APIDEUDAJUDICIAL_HEADER_CONEXION("MSG_ERR_APIDEUDAJUDICIAL_HEADER_CONEXION"),
    
    MSG_ERR_APIDEUDAJUDICIAL_FALLO_HTTP("MSG_ERR_APIDEUDAJUDICIAL_FALLO_HTTP"),
    
    MSG_ERR_APIDEUDAJUDICIAL_NO_LECTURA("MSG_ERR_APIDEUDAJUDICIAL_NO_LECTURA"),
    
    MSG_ERR_APIDEUDAJUDICIAL_UTF8("MSG_ERR_APIDEUDAJUDICIAL_UTF8"),
    
    FILE_ENCODING("file.encoding"),
	
	DEFAULT_CHARSET("defaultCharset"),
	
	UTF_8("UTF-8"),
	
	PUT("PUT")
	
    ;
    private final String value;

    private APIAutoServicioEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
