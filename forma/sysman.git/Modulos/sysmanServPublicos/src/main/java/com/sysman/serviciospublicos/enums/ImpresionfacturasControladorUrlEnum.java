/*
 * ImpresionfacturasControladorUrlEnum
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
public enum ImpresionfacturasControladorUrlEnum {

    URL0001("IMPRESIONFACTURASCONTROLADORURL0001", "59013"),

    URL14360("IMPRESIONFACTURASCONTROLADORURL14360", "213100"),

    URL12595("IMPRESIONFACTURASCONTROLADORURL12595", "214056"),

    URL13432("IMPRESIONFACTURASCONTROLADORURL13432", "213098");

    private final String key;
    private final String value;

    private ImpresionfacturasControladorUrlEnum(String key, String value) {
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
