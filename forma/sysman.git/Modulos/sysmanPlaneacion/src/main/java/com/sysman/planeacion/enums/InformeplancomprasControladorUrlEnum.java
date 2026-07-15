/*
 * InformeplancomprasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InformeplancomprasControladorUrlEnum {

    URL6632("INFORMEPLANCOMPRASCONTROLADORURL6632", "4001"),

    URL0004("INFORMEPLANCOMPRASCONTROLADORURL0004", "108004"),

    URL2407("INFORMEPLANCOMPRASCONTROLADORURL2407", "114003"),

    URL0001("INFORMEPLANCOMPRASCONTROLADORURL0001", "114006"),

    URL0002("INFORMEPLANCOMPRASCONTROLADORURL0002", "114007"),

    URL0003("INFORMEPLANCOMPRASCONTROLADORURL0003", "114009");

    private final String key;
    private final String value;

    private InformeplancomprasControladorUrlEnum(String key, String value) {
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
