/*
 * ComprobantecntretencionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ComprobantecntretencionsControladorUrlEnum {

    URL24799("COMPROBANTECNTRETENCIONSCONTROLADORURL24799", "69002"),

    URL6758("COMPROBANTECNTRETENCIONSCONTROLADORURL6758", "15019"),

    URL7072("COMPROBANTECNTRETENCIONSCONTROLADORURL7072", "8005"),
    
    URL7073("COMPROBANTECNTRETENCIONSCONTROLADORURL7073", "8011"),

    URL7597("COMPROBANTECNTRETENCIONSCONTROLADORURL7597", "12005"),
    
    URL1953("COMPROBANTECNTRETENCIONSCONTROLADORURL1953", "1953001"),
    
    URL8012("COMPROBANTECNTRETENCIONSCONTROLADORURL1953", "8012"),
    
	URL8014("COMPROBANTECNTRETENCIONSCONTROLADORURL8014", "8014");

    private final String key;
    private final String value;

    private ComprobantecntretencionsControladorUrlEnum(String key,
        String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
