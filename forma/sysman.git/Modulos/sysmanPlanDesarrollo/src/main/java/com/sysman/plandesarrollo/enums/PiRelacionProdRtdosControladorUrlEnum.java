/*
 * PiRelacionProdRtdosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.plandesarrollo.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PiRelacionProdRtdosControladorUrlEnum {

    URL5920("PIRELACIONPRODRTDOSCONTROLADORURL5920", "1022001"),

    URL8758("PIRELACIONPRODRTDOSCONTROLADORURL8758", "566002"),

    URL6594("PIRELACIONPRODRTDOSCONTROLADORURL6594", "566002"),

    URL7663("PIRELACIONPRODRTDOSCONTROLADORURL7663", "566002"),

    URL9842("PIRELACIONPRODRTDOSCONTROLADORURL9842", "566002"),

    URL0003("PIRELACIONPRODRTDOSCONTROLADORURL0003", "1020001"),

    URL0001("PIRELACIONPRODRTDOSCONTROLADORURL0001", "554007"),

    URL0002("PIRELACIONPRODRTDOSCONTROLADORURL0002", "554019");

    private final String key;
    private final String value;

    private PiRelacionProdRtdosControladorUrlEnum(String key, String value) {
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
