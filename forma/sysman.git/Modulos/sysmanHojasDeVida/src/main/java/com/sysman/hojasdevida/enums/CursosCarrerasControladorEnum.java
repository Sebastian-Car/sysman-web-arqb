/*-
 * CursosCarrerasControladorEnum.java
 *
 * 1.0
 * 
 * 2/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring de sentencias SQL.
 * 
 * @version 1.0, 2/02/2018
 * @author pespitia
 *
 */
public enum CursosCarrerasControladorEnum {

    NITENTIDAD("NITENTIDAD");

    private final String value;

    private CursosCarrerasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
