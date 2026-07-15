/*
 * FrmImportarRipsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.sysmanauditoriacuentasmedicas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmCausarRipsControladorUrlEnum {

    URL4391("FRMCAUSARRIPSCONTROLADORURL4391",
                    "1828001"),

    URL4392("FRMCAUSARRIPSCONTROLADORURL4392",
                    "1820001"),

    URL4395("FRMCAUSARRIPSCONTROLADORURL4395",
                    "14001"),

    URL437("FRMCAUSARRIPSCONTROLADORURL437",
                    "1823001"),

    URL368("FRMCAUSARRIPSCONTROLADORURL368",
                    "1828004"),

    URL654("FRMCAUSARRIPSCONTROLADORURL654",
                    "1828005"),
	
	URL655("FRMCAUSARRIPSCONTROLADORURL655",
            "1828006"); // MPEREZ 7715707

    private final String key;
    private final String value;

    private FrmCausarRipsControladorUrlEnum(String key, String value) {
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
