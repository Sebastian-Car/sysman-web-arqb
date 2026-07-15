/*
 * FrmfichatecnicaproyectosControladorEnum
 *
 * 1.0
 *
 * spina 14/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum FrmfichatecnicaproyectosControladorEnum {

    SECCION("SECCION"),

    CUMPLE("CUMPLE"),

    OBSERVACION("OBSERVACION"),

    CODIGO_DET("CODIGO_DET"),

    ITEM("ITEM"),

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_PROYECTO("KEY_PROYECTO"),

    KEY_SECTOR("KEY_SECTOR"),

    KEY_CODIGO_DET("KEY_CODIGO_DET");

    private final String value;

    private FrmfichatecnicaproyectosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
