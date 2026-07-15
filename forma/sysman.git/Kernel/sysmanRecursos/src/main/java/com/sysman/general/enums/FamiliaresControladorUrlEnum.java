/*-
 * FamiliaresControladorUrlEnum.java
 *
 * 1.0
 *
 * 28/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 *
 * @version 1.0, 28/12/2017
 * @author spina
 *
 */
public enum FamiliaresControladorUrlEnum {
    URL3320("TIPOLICENCIASCONTROLADORURL3320", "209001"),

    URL3321("TIPOLICENCIASCONTROLADORURL3321", "609001"),

    URL3322("TIPOLICENCIASCONTROLADORURL3322", "638002"),

    URL3323("TIPOLICENCIASCONTROLADORURL3323", "14001"),

    URL3324("TIPOLICENCIASCONTROLADORURL3324", "685035"),

    ;

    private final String key;
    private final String value;

    private FamiliaresControladorUrlEnum(String key, String value) {
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
