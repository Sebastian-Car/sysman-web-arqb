/*-
 * TipoClaseEventoSstsControladorEnum.java
 *
 * 1.0
 * 
 * 29 de dic. de 2017
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
public enum TipoClaseEventoSstsControladorEnum {

    TIPO_TRANSACCION("TIPO_TRANSACCION"),

    NOMBRETIPOTX("NOMBRETIPOTX");

    private final String value;

    private TipoClaseEventoSstsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
