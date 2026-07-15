/*-
 * ReporteSaldoServicioUrlEnum.java
 *
 * 1.0
 *
 * 15/06/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * Enumerado para las urls necesarias para las consultas de las listas
 * en el controlador ReporteSaldoServicio.
 *
 * @version 1.0, 15/06/2017
 * @author lcortes
 *
 */
public enum ReporteSaldoServicioUrlEnum {

    URL0001("REPORTESALDOSERVICIOURL0001", "214029"),

    URL0002("REPORTESALDOSERVICIOURL0002", "213046"),

    URL0003("REPORTESALDOSERVICIOURL0003", "213048");

    private final String key;
    private final String value;

    private ReporteSaldoServicioUrlEnum(String key, String value) {
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
