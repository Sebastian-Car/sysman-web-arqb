/*
 * FrmInformesPlanIndicativoUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmInformesPlanIndicativoUrlEnum {

    URL4040("FRMINFORMESPLANINDICATIVOURL4040",
                    "4043"),

    URL7182("FRMINFORMESPLANINDICATIVOURL7182",
                    "554005"),

    URL4842("FRMINFORMESPLANINDICATIVOURL4842",
                    "554006"),

    URL4512("FRMINFORMESPLANINDICATIVOURL4512",
                    "4001");

    private final String key;
    private final String value;

    private FrmInformesPlanIndicativoUrlEnum(String key, String value) {
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
