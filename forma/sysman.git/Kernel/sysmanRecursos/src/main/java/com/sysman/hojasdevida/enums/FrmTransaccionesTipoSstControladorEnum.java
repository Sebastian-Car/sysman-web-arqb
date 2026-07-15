/*-
 * FrmTransaccionesTipoSstControladorEnum.java
 *
 * 1.0
 * 
 * 18 de ene. de 2018
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
public enum FrmTransaccionesTipoSstControladorEnum {

    IND_REPONSABLE("IND_REPONSABLE"),

    IND_COMITE("IND_COMITE"),

    CLASE_TRANSACCION("CLASE_TRANSACCION"),

    IND_AGENTE("IND_AGENTE");

    private final String value;

    private FrmTransaccionesTipoSstControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
