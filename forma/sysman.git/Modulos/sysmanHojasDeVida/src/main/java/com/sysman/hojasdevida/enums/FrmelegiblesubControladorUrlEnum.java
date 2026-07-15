/*
 * FrmelegiblesubControladorUrlEnum
 *
 * 1.0
 *
 * 31/01/2018
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
public enum FrmelegiblesubControladorUrlEnum {

    URL4130("FRMELEGIBLESUBCONTROLADORURL4130", "953002"),

    URL4131("FRMELEGIBLESUBCONTROLADORURL4131", "953001"),

    URL4132("FRMELEGIBLESUBCONTROLADORURL4132", "953004"),

    URL4133("FRMELEGIBLESUBCONTROLADORURL4133", "953005"),

    ;

    private final String key;
    private final String value;

    private FrmelegiblesubControladorUrlEnum(String key, String value) {
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
