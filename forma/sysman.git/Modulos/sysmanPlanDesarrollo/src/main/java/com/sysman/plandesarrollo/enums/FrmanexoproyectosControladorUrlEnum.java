/*-
 * FrmanexoproyectosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 2/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 2/10/2019
 * @author bcardenas
 *
 */
public enum FrmanexoproyectosControladorUrlEnum {

    URL0001("FRMANEXOPROYECTOSCONTROLADOR", "1812001");

    private final String key;
    private final String value;

    private FrmanexoproyectosControladorUrlEnum(String key, String value) {
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