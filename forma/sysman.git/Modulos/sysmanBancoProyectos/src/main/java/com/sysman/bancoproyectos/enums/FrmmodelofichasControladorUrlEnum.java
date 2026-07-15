/*
 * FrmmodelofichasControladorUrlEnum
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
public enum FrmmodelofichasControladorUrlEnum {

    URL4838("FRMMODELOFICHASCONTROLADORURL4838",
                    "564001"),

    URL3781("FRMMODELOFICHASCONTROLADORURL3781",
                    "564002"),

    URL5050("FRMMODELOFICHASCONTROLADORURL5050",
                    "564003"),

    URL15847("FRMMODELOFICHASCONTROLADORURL15847",
                    "56400C");

    private final String key;
    private final String value;

    private FrmmodelofichasControladorUrlEnum(String key, String value) {
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
