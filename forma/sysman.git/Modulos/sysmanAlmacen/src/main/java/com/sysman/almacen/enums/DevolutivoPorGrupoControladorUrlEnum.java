/*-
 * CuentaalmacendetalladoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * 
 * @version 1.0, 02/08/2018
 * @author jrojas
 *
 */
public enum DevolutivoPorGrupoControladorUrlEnum {

    URL3217("DEVOLUTIVOSPORGRUPOCONTROLADORURL3217", "141102"),
    URL3218("DEVOLUTIVOSPORGRUPOCONTROLADORURL3218", "141104");

    private final String key;
    private final String value;

    private DevolutivoPorGrupoControladorUrlEnum(String key, String value)
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
