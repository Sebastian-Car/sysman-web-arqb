/*
 * ConsultaAcuerdoPagosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConsultaAcuerdoPagosControladorUrlEnum {

    URL0001("CONSULTAACUERDOPAGOSCONTROLADORURL0001", "719001"),

    URL0002("CONSULTAACUERDOPAGOSCONTROLADORURL0002", "719002"),

    URL0003("CONSULTAACUERDOPAGOSCONTROLADORURL0003", "720001"),

    URL0004("CONSULTAACUERDOPAGOSCONTROLADORURL0004", "720002"),
    
    URL0005("CONSULTAACUERDOPAGOSCONTROLADORURL0005", "720003");

    private final String key;
    private final String value;

    private ConsultaAcuerdoPagosControladorUrlEnum(String key, String value) {
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
