/*-
 * CdEstadosPingresosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 28/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * 
 * @version 1.0, 28/11/2017
 * @author jcrodriguez
 *
 */
public enum CdEstadosPingresosControladorUrlEnum {

    URL11959("FRMCAEJECUCIONPASCONTROLADORURL11959", "94109"),

    URL12000("FRMCAEJECUCIONPASCONTROLADORURL12000", "94111"),

    URL13622("FRMCAEJECUCIONPASCONTROLADORURL13622", "4001");

    private final String key;
    private final String value;

    private CdEstadosPingresosControladorUrlEnum(String key, String value) {
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
