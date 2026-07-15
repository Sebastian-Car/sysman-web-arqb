/*-
 * PlusvaliaConceptosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 7/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 7/02/2019
 * @author bcardenas
 *
 */
public enum PlusvaliaConceptosControladorUrlEnum {

    URL16132("PLUSVALIACONCEPTOSCONTROLADORURL16132", "16132"),

    URL1032("PLUSVALIACONCEPTOSCONTROLADORURL16132", "1032001");

    private final String key;
    private final String value;

    private PlusvaliaConceptosControladorUrlEnum(String key, String value) {
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