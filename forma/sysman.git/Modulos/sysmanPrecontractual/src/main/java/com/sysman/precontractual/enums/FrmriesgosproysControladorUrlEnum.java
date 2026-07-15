/*
 * FrmriesgosproysControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmriesgosproysControladorUrlEnum {

    URL6466("FRMRIESGOSPROYSCONTROLADORURL6466", "61018"),

    URL5935("FRMRIESGOSPROYSCONTROLADORURL5935", "478002"),

    URL5496("FRMRIESGOSPROYSCONTROLADORURL5496", "479001"),

    URL7236("FRMRIESGOSPROYSCONTROLADORURL7236", "515001"),

    URL0001("FRMRIESGOSPROYSCONTROLADORURL0001", "1705001"), // FUENTE

    URL0002("FRMRIESGOSPROYSCONTROLADORURL0002", "1705003"), // ETAPA

    URL0003("FRMRIESGOSPROYSCONTROLADORURL0003", "1705005"), // TIPO

    URL0004("FRMRIESGOSPROYSCONTROLADORURL0004", "1706001"), // PROBABILIDAD

    URL0005("FRMRIESGOSPROYSCONTROLADORURL0005", "1707001"), // IMPACTO

    URL0006("FRMRIESGOSPROYSCONTROLADORURL0006", "1706003"), // NOMBRE
                                                             // CATEGORIA
                                                             // POR
                                                             // VALORACION

    URL0007("FRMRIESGOSPROYSCONTROLADORURL0006", "478003");

    private final String key;
    private final String value;

    private FrmriesgosproysControladorUrlEnum(String key, String value) {
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
