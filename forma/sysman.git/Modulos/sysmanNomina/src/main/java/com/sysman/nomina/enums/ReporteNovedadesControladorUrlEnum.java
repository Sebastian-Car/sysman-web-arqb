/*
 * ReporteNovedadesControladorUrlEnum
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
public enum ReporteNovedadesControladorUrlEnum {

    URL4943("REPORTENOVEDADESCONTROLADORURL4943",
                    "471066"),

    URL7879("REPORTENOVEDADESCONTROLADORURL7879",
                    "471039"),

    URL9535("REPORTENOVEDADESCONTROLADORURL9535",
                    "210050"),

    URL5546("REPORTENOVEDADESCONTROLADORURL5546",
                    "471039"),

    URL7196("REPORTENOVEDADESCONTROLADORURL7196",
                    "471045"),

    URL3894("REPORTENOVEDADESCONTROLADORURL3894",
                    "537003"),

    URL5959("REPORTENOVEDADESCONTROLADORURL5959",
                    "471041");

    private final String key;
    private final String value;

    private ReporteNovedadesControladorUrlEnum(String key, String value) {
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
