/*-
 * PeriodoNovedadesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 29/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * 
 * @version 1.0, 29/09/2017
 * @author jcrodriguez
 *
 */
public enum PeriodoNovedadesControladorUrlEnum {
    URL3723("NIVELCUMPLIMIENTOSCONTROLADORURL3723", "4001");

    private final String key;
    private final String value;

    private PeriodoNovedadesControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
