/*-
 * TiposretencionesControladorEnum.java
 *
 * 1.0
 * 
 * 30/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * @version 1.0, 30/10/2017
 * @author jcrodriguez
 *
 */
public enum TiposretencionesControladorEnum {

    CUENTACONTABLE("CUENTACONTABLE");

    private final String value;

    private TiposretencionesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
