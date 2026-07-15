/*-
 * ConfigurarDeducibleExsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 28/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.exogenas.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 28/12/2018
 * @author bcardenas
 *
 */
public enum ConfigurarDeducibleExsControladorUrlEnum {

    URL0001("CONFIGURARDEDUCIBLEEXSCONTROLADORURL0001", "72093"),

    URL0002("CONFIGURARDEDUCIBLEEXSCONTROLADORURL0002", "72095");

    private final String key;
    private final String value;

    private ConfigurarDeducibleExsControladorUrlEnum(String key,
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
