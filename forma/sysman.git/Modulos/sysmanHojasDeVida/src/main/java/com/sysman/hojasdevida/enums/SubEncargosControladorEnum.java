/*-
 * SubEncargosControladorEnum.java
 *
 * 1.0
 * 
 * 19/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubEncargosControladorEnum {

    ID_DE_CARGO("ID_DE_CARGO"),

    CLASE_ENCARGO("CLASE_ENCARGO"),

    ENCARGOS("ENCARGOS"),

    TIPO_NOVEDAD("TIPO_NOVEDAD"),

    FECHAACTA("FECHAACTA"),

    FECHAACTO("FECHAACTO"),

    FECHAEFECTIVIDAD("FECHAEFECTIVIDAD");

    private final String value;

    private SubEncargosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
