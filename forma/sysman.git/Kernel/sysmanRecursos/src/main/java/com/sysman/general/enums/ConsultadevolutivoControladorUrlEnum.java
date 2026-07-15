/*
 * CdevolutivocuantiaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConsultadevolutivoControladorUrlEnum {

    URL2489("CONSULTADEVOLUTIVOCONTROLADORURL2489", "141001"),

    URL3360("CONSULTADEVOLUTIVOCONTROLADORURL3360", "141003"),

    URL3361("CONSULTADEVOLUTIVOCONTROLADORURL3361", "141005"),
    
    URL3362("CONSULTADEVOLUTIVOCONTROLADORURL3362", "141007"),
    
    URL3363("CONSULTADEVOLUTIVOCONTROLADORURL3363", "95002"),
    
    URL3364("CONSULTADEVOLUTIVOCONTROLADORURL3364", "141148"),
    
    URL3365("CONSULTADEVOLUTIVOCONTROLADORURL3365", "141150"),
    
    URL3366("CONSULTADEVOLUTIVOCONTROLADORURL3366", "141152"),
    
    URL141162("CONSULTADEVOLUTIVOCONTROLADORURL3366", "141162");

    private final String key;
    private final String value;

    private ConsultadevolutivoControladorUrlEnum(String key, String value) {
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
