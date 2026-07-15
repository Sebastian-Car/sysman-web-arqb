/*-
 * RptresponsabledependenciaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 27/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * del refactoring.
 * 
 * @version 1.0, 27/09/2017
 * @author pespitia
 *
 */
public enum RptresponsabledependenciaControladorUrlEnum {

    URL0001("RPTRESPONSABLEDEPENDENCIACONTROLADORURL0001", "62002"),

    URL0002("RPTRESPONSABLEDEPENDENCIACONTROLADORURL0002", "62019");

    private final String key;
    private final String value;

    private RptresponsabledependenciaControladorUrlEnum(String key,
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
