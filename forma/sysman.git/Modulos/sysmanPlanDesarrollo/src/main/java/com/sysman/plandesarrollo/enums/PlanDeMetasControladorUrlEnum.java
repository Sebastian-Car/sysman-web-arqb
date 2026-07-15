/*-
 * FrmequivalenciasControladorUrlEnums.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * @version 1.0, 20/04/2018
 * @author ybecerra
 *
 */
public enum PlanDeMetasControladorUrlEnum {

    URL209("PLANDEMETASCONTROLADORURL209", "71037"),

    URL405("PLANDEMETASCONTROLADORURL405", "4069")

    ;

    private final String key;
    private final String value;

    private PlanDeMetasControladorUrlEnum(String key, String value)
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
