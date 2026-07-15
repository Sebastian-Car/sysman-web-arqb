/*-
 * SolicitudesfinanciamientosControladorEnum.java
 *
 * 1.0
 *
 * 02/02/2018
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum SolicitudesfinanciamientosControladorEnum {

    CONTROLSANCION("CONTROLSANCION");

    private final String value;

    private SolicitudesfinanciamientosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
