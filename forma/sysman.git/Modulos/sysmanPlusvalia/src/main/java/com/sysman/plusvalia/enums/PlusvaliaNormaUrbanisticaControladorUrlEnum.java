/*-
 * PlusvaliaNormaUrbanisticaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 3/04/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 3/04/2019
 * @author bcardenas
 *
 */
public enum PlusvaliaNormaUrbanisticaControladorUrlEnum {

    URL1785("PLUSVALIABENEFICIARIOSCONTROLADORURL1785", "1785001");

    private final String key;
    private final String value;

    private PlusvaliaNormaUrbanisticaControladorUrlEnum(String key,
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
