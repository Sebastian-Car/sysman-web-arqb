/*-
 * LegalizacionViaticosControladorUrlEnum.java
 *
 * 1.0
 *
 * 28/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 *
 * @version 1.0, 17/01/2018
 * @author ybecerra
 *
 */
public enum LegalizacionViaticosControladorUrlEnum {

    URL184("LEGALIZACIONVIATICOSCONTROLADORURL184", "4001"),

    URL213("LEGALIZACIONVIATICOSCONTROLADORURL213", "14001"),

    URL235("LEGALIZACIONVIATICOSCONTROLADORURL235", "761001")

    ;

    private final String key;
    private final String value;

    private LegalizacionViaticosControladorUrlEnum(String key, String value) {
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
