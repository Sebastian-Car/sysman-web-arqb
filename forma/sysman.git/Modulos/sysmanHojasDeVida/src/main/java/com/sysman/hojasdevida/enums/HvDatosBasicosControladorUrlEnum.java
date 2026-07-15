/*
 * EstratosfgControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum HvDatosBasicosControladorUrlEnum {

    URL159("IMPRIMIRHVDATOSBASICOSCONTROLADORURL159", "685001"),

    URL179("IMPRIMIRHVDATOSBASICOSCONTROLADORURL179", "685003");

    private final String key;
    private final String value;

    private HvDatosBasicosControladorUrlEnum(String key, String value) {
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
