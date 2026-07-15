/*-
 * FrmpremiacionsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 6/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enum necesario para traer datos de los combos utilizando el dss
 * correspondientes
 * 
 * @version 1.0, 6/02/2018
 * @author crodriguez
 *
 */
public enum FrmpremiacionsControladorUrlEnum {
    URL185("FRMPREMIACIONSCONTROLADOR185", "954004"),

    URL227("FRMPREMIACIONSCONTROLADOR227", "740003"),

    URL281("FRMPREMIACIONSCONTROLADOR281", "740007"),

    URL330("FRMPREMIACIONSCONTROLADOR330", "774001"),

    ;

    private final String key;
    private final String value;

    private FrmpremiacionsControladorUrlEnum(String key, String value) {
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
