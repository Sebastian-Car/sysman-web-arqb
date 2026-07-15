/*-
 * FrmelegiblesControladorEnum.java
 *
 * 1.0
 *
 * 30/01/2018
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmelegiblesControladorEnum {

    NRO_CONVOCATORIA("NRO_CONVOCATORIA"),

    NOMBRE_DEL_CARGO("NOMBRE_DEL_CARGO"),

    FECHA_CONVOCATORIA("FECHA_CONVOCATORIA"),

    DENOMINACION("DENOMINACION");

    private final String value;

    private FrmelegiblesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
