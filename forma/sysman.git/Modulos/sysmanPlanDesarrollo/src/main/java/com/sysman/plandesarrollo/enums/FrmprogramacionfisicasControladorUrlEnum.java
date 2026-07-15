/*-
 * FrminfPlanaccionControladorUrlEnum.java
 *
 * 1.0
 * 
 * 8/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * 
 * Enum necesario para traer datos de los combos utilizando el dss
 * correspondientes
 * 
 * @version 1.0, 8/03/2018
 * @author crodriguez
 *
 */
public enum FrmprogramacionfisicasControladorUrlEnum {

    URL174("FRMPROGRAMACIONFISICACONTROLADOR174", "4001"),

    URL155("FRMPROGRAMACIONFISICACONTROLADOR155", "1716001")

    ;

    private final String key;
    private final String value;

    private FrmprogramacionfisicasControladorUrlEnum(String key, String value)
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
