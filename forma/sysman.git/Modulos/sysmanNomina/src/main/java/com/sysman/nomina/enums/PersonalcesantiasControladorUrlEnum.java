/*-
 * PersonalcesantiasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 4/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * @version 1.0, 4/01/2018
 * @author jcrodriguez
 *
 */
public enum PersonalcesantiasControladorUrlEnum {
    URL001("PERSONALCESANTIASCONTROLADORURL28520", "210023"),

    URL002("PERSONALCESANTIASCONTROLADORURL28521", "744001");

    private final String key;
    private final String value;

    private PersonalcesantiasControladorUrlEnum(String key, String value) {
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
