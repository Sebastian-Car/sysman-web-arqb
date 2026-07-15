/*-
 * ReporteSaldoServicioEnum.java
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
 * Enumerado para los nombres de los parametros necesarios para las
 * consultas de las listas en el controlador ReporteSaldoServicio.
 *
 * @version 1.0, 15/06/2017
 * @author lcortes
 *
 */
public enum ReporteSaldoServicioEnum {

    PARAM0("RUTA_INICIAL");

    private final String value;

    private ReporteSaldoServicioEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
