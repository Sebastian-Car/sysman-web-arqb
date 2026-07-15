/*-
 * PertinenciaNovedadControladorEnum.java
 *
 * 1.0
 * 
 * 21/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 21/12/2018
 * @author bcardenas
 *
 */
public enum PertinenciaNovedadControladorEnum {

    TIPOT("TIPOT"),

    CLASET("CLASET"),

    TIPON("TIPON"),

    CLASEN("CLASEN"),

    NOMBRE_INDICADOR("NOMBRE_INDICADOR"),

    DIGITOS("DIGITOS");

    private final String value;

    private PertinenciaNovedadControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
