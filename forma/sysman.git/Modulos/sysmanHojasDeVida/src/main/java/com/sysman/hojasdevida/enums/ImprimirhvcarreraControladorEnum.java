/*-
 * ImprimirhvcarreraControladorEnum.java
 *
 * 1.0
 *
 * 14/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 *
 * @version 1.0, 14/12/2017
 * @author lcortes
 *
 */
public enum ImprimirhvcarreraControladorEnum {

    NUMEROCARPETA("NUMEROCARPETA"),

    NOMBRECOMPLETO("NOMBRECOMPLETO"),

    EMPLEADOINICIAL("EMPLEADOINICIAL");

    private final String value;

    private ImprimirhvcarreraControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
