/*
 * TipolicenciasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum TipolicenciasControladorUrlEnum {

    URL3316("TIPOLICENCIASCONTROLADORURL3316",
                    "151001"),

    URL3917("TIPOLICENCIASCONTROLADORURL3917",
                    " listaIdConceptoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR22:TBCB35\", \"SELECT \" + \" CONCEPTOS.ID_DE_CONCEPTO, \" + \" CONCEPTOS.NOMBRE_CONCEPTO \" + \" FROM \" + \" CONCEPTOS \" + \" WHERE \" + \" (((CONCEPTOS.COMPANIA) = '\" + compania + \"'))\",");

    private final String key;
    private final String value;

    private TipolicenciasControladorUrlEnum(String key, String value) {
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
