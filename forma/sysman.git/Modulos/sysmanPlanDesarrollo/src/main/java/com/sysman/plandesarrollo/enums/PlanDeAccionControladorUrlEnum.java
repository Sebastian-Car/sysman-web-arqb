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
public enum PlanDeAccionControladorUrlEnum {

    URL353("PLANDEACCIONCONTROLADORURL353", "71037"),

    URL396("PLANDEACCIONCONTROLADORURL396", "34040"),

    URL465("PLANDEACCIONCONTROLADORURL465", "4069"),

    URL1632("PLANDEACCIONCONTROLADORURL1632", "1043002"),

    URL497("PLANDEACCIONCONTROLADORURL497", "1043003"),

    URL616("PLANDEACCIONCONTROLADORURL616", "1713001")

    ;

    private final String key;
    private final String value;

    private PlanDeAccionControladorUrlEnum(String key, String value)
    {
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
