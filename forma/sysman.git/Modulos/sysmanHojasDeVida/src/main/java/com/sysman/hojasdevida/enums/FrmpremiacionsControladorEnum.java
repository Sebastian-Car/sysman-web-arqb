/*-
 * FrmpremiacionsControladorEnum.java
 *
 * 1.0
 * 
 * 6/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * @version 1.0, 6/02/2018
 * @author crodriguez
 *
 */
public enum FrmpremiacionsControladorEnum {
    TIPOEVENTO("TIPOEVENTO"),

    IDEVENTO("IDEVENTO"),

    ID_TIPO_PREMIO("ID_TIPO_PREMIO"),

    NOMBRE_TIPO_PREMIO("NOMBRE_TIPO_PREMIO"),

    BENEFICIARIO("BENEFICIARIO"),

    CALIFICACION("CALIFICACION"),

    EVENTO("EVENTO"),

    ASISTENTE("ASISTENTE");

    private final String value;

    private FrmpremiacionsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
