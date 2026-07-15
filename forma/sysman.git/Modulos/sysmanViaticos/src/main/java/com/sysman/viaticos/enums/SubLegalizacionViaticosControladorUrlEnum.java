/*-
 * SubLegalizacionViaticosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 * 
 * @version 1.0, 19/01/2018
 * @author ybecerra
 *
 */
public enum SubLegalizacionViaticosControladorUrlEnum {

    URL188("SUBLEGALIZACIONVIATICOSCONTROLADORURL188", "766001"),

    URL301("SUBLEGALIZACIONVIATICOSCONTROLADORURL301", "764005");

    private final String key;
    private final String value;

    private SubLegalizacionViaticosControladorUrlEnum(String key,
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
