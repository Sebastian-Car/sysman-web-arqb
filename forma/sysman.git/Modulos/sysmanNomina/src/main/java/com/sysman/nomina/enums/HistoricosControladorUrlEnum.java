/*
 * HistoricosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum HistoricosControladorUrlEnum {

    URL6171("HISTORICOSCONTROLADORURL6171",
                    " listaIDdeConceptoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR41:TBCB395\", \"SELECT \" + \" CONCEPTOS.ID_DE_CONCEPTO, \" + \" CONCEPTOS.NOMBRE_CONCEPTO, \" + \" CONCEPTOS.CLASE \" + \" FROM \" + \" CONCEPTOS \" + \" WHERE CONCEPTOS.CLASE <> 2\" + \" AND CONCEPTOS.COMPANIA = '\" + compania + \"'\" + \" ORDER BY CONCEPTOS.ID_DE_CONCEPTO\","),

    URL5432("HISTORICOSCONTROLADORURL5432",
                    "620001"),

    URL7627("HISTORICOSCONTROLADORURL7627",
                    "151021"),

    URL6890("HISTORICOSCONTROLADORURL6890",
                    "210031"), 
    URL4444("HISTORICOSCONTROLADORURL4444",
                    "51002");

    private final String key;
    private final String value;

    private HistoricosControladorUrlEnum(String key, String value) {
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
