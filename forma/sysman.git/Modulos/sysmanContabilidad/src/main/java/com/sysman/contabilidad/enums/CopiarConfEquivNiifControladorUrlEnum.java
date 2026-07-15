/*
 * CopiarConfEquivNiifControladorUrlEnum
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
public enum CopiarConfEquivNiifControladorUrlEnum {

    URL001("COPIARCONFEQUIVNIIFCONTROLADORURL001", "4014"),

    URL5310("COPIARCONFEQUIVNIIFCONTROLADORURL5310", "4016"),

    URL9266("COPIARCONFEQUIVNIIFCONTROLADORURL9266", "16082"),

    URL6412("COPIARCONFEQUIVNIIFCONTROLADORURL6412", "16056"),

    URL4953("COPIARCONFEQUIVNIIFCONTROLADORURL4953", "4001"),

    URL264("COPIARCONFEQUIVNIIFCONTROLADORURL4953", "16081");

    private final String key;
    private final String value;

    private CopiarConfEquivNiifControladorUrlEnum(String key, String value) {
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
