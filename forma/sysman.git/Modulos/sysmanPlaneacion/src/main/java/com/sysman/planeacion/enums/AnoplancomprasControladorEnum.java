/*-
 * AnoplancomprasControladorEnum.java
 *
 * 1.0
 * 
 * 6/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.planeacion.enums;

/**
 * 
 * @version 1.0, 6/09/2017
 * @author jcrodriguez
 *
 */
public enum AnoplancomprasControladorEnum {
    VALORUVT("VALORUVT"),

    PORCENTAJEPENSION("PORCENTAJEPENSION"),

    PORCENTAJESALUD("PORCENTAJESALUD"),

    VLRFONDOSOL_PENSIONAL("VLRFONDOSOL_PENSIONAL"),

    ANO("ANO");

    private final String value;

    private AnoplancomprasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
