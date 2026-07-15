/*-
 * SolicitudesAutDetalladosControladorUrlEnum.java
 *
 * 1.0
 *
 * 6 de feb. de 2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y
 *
 * asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum SolicitudesAutDetalladosControladorUrlEnum {

    URL0001("SOLICITUDESAUTDETALLADOSCONTROLADORURL0001", "210108"),

    URL0002("SOLICITUDESAUTDETALLADOSCONTROLADORURL0002", "1008001"),

    URL0003("SOLICITUDESAUTDETALLADOSCONTROLADORURL0003", "1009001"),

    URL0004("SOLICITUDESAUTDETALLADOSCONTROLADORURL0004", "463003"),

    URL0005("SOLICITUDESAUTDETALLADOSCONTROLADORURL0005", "471063"),

    URL0006("SOLICITUDESAUTDETALLADOSCONTROLADORURL0006", "471064"),

    URL0007("SOLICITUDESAUTDETALLADOSCONTROLADORURL0007", "471065"),

    URL0008("SOLICITUDESAUTDETALLADOSCONTROLADORURL0008", "59020"),

    URL0009("SOLICITUDESAUTDETALLADOSCONTROLADORURL0009", "104064"),

    URL0010("SOLICITUDESAUTDETALLADOSCONTROLADORURL0010", "1663003");

    private final String key;
    private final String value;

    private SolicitudesAutDetalladosControladorUrlEnum(String key,
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
