/*
 * FrmactualizarfechaslecturasControladorUrlEnum
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
public enum FrmactualizarfechaslecturasControladorUrlEnum {

    URL5876("FRMACTUALIZARFECHASLECTURASCONTROLADORURL5876", "213078"),

    URL2977("FRMACTUALIZARFECHASLECTURASCONTROLADORURL2977", "213072"),

    URL3716("FRMACTUALIZARFECHASLECTURASCONTROLADORURL3716", "213074"),

    URL4494("FRMACTUALIZARFECHASLECTURASCONTROLADORURL4494", "214010");

    private final String key;
    private final String value;

    private FrmactualizarfechaslecturasControladorUrlEnum(String key,
        String value) {
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
