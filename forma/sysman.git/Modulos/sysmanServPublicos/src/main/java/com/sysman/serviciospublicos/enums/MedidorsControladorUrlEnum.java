/*
 * MedidorsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum MedidorsControladorUrlEnum {

    URL6654("MEDIDORSCONTROLADORURL6654",
                    "290001"),

    URL7311("MEDIDORSCONTROLADORURL7311",
                    "289009"),

    URL0001("MEDIDORSCONTROLADORURL0001", "289004"),

    URL0002("MEDIDORSCONTROLADORURL0002", "289006"),

    URL0003("MEDIDORSCONTROLADORURL0003", "289007"),

    URL0004("MEDIDORSCONTROLADORURL0004", "28900D"),;

    private final String key;
    private final String value;

    private MedidorsControladorUrlEnum(String key, String value) {
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
