/*
 * CertificadoEstratoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CertificadoEstratoControladorUrlEnum {
    URL3322("CERTIFICADOESTRATOCONTROLADORURL3322", "387001"),

    URL3339("CERTIFICADOESTRATOCONTROLADORURL3339", "367023"),

    URL3340("CERTIFICADOESTRATOCONTROLADORURL3340", "400004"),

    URL3341("CERTIFICADOESTRATOCONTROLADORURL3341", "104014"),

    ;
    private final String key;
    private final String value;

    private CertificadoEstratoControladorUrlEnum(String key, String value) {
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
