/*-
 * ParametroLiquidacionViaticosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 20/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 * 
 * @version 1.0, 20/01/2018
 * @author crodriguez
 *
 */
public enum ParametroLiquidacionViaticosControladorUrlEnum {

    URL127("PARAMETROLIQUIDACIONVIATICOSCONTROLADORURL127", "67014"),

    URL130("PARAMETROLIQUIDACIONVIATICOSCONTROLADORURL130", "67016");

    private final String key;
    private final String value;

    private ParametroLiquidacionViaticosControladorUrlEnum(String key,
        String value) {
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
