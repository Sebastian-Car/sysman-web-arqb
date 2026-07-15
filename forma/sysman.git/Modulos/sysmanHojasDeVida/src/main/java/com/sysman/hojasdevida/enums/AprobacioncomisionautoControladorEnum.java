/*-
 * FamiliaresControladorEnum.java
 *
 * 1.0
 *
 * 28/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 *
 * @version 1.0, 17/01/2018
 * @author ybecerra
 *
 */
public enum AprobacioncomisionautoControladorEnum {

    CODSOLICITUD("CODSOLICITUD"),
    TIPOVIATICO("TIPO_VIATICO"),
    NOMBRETERCERO("NOMBRETERCERO"),
    FECHA("FECHA"),
    OBJETO("OBJETO"),
    TERCERO("TERCERO"),
    ESTADO1("ESTADO1"),

    PARAM1("");

    private final String value;

    private AprobacioncomisionautoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
