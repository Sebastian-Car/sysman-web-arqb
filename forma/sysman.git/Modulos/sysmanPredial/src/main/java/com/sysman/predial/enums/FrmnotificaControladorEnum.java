/*
 * FrmnotificaControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum FrmnotificaControladorEnum {

    TIPOINICIAL("TIPOINICIAL"),

    CODPREDIO("CODPREDIO"),

    FECHANOTIFICACION("FECHANOTIFICACION"),

    DOCSOPORTE("DOCSOPORTE"),

    OBSERVACIONES("OBSERVACIONES"),

    NOTIFICADOR("NOTIFICADOR"),

    PROCESO_DE_COBRO("PROCESO_DE_COBRO"),

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_CODIGO("KEY_CODIGO"),

    KEY_NUMERO_ORDEN("KEY_NUMERO_ORDEN");

    private final String value;

    private FrmnotificaControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
