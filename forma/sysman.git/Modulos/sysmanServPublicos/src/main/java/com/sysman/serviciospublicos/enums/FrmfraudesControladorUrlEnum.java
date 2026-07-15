/*
 * FrmfraudesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmfraudesControladorUrlEnum {

    URL32293("FRMFRAUDESCONTROLADORURL32293",
                    "26000C"),

    URL12099("FRMFRAUDESCONTROLADORURL12099",
                    "260002"),

    URL37806("FRMFRAUDESCONTROLADORURL37806",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, cartaPregunta,"),

    URL19084("FRMFRAUDESCONTROLADORURL19084",
                    "104011"),

    URL42270("FRMFRAUDESCONTROLADORURL42270",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, fraudesCarta,"),

    URL20157("FRMFRAUDESCONTROLADORURL20157",
                    "213096"),

    URL19600("FRMFRAUDESCONTROLADORURL19600",
                    "299001"),

    URL18254("FRMFRAUDESCONTROLADORURL18254",
                    "227018"),

    URL16668("FRMFRAUDESCONTROLADORURL16668",
                    "365002"),

    URL33751("FRMFRAUDESCONTROLADORURL33751",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, cartaPregunta,"),

    URL36404("FRMFRAUDESCONTROLADORURL36404",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, fraudesCarta,"),

    URL17508("FRMFRAUDESCONTROLADORURL17508",
                    "227016"),

    URL30501("FRMFRAUDESCONTROLADORURL30501",
                    "300002"),

    URL38271("FRMFRAUDESCONTROLADORURL38271",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, fraudesCarta,"),

    URL14989("FRMFRAUDESCONTROLADORURL14989",
                    "300001"),

    URL5959("FRMFRAUDESCONTROLADORURL5959",
                    "365001"),

    URL6969("FRMFRAUDESCONTROLADORURL6969",
                    "104023"),

    URL1313("FRMFRAUDESCONTROLADORURL1313",
                    "301005"),

    URL1414("FRMFRAUDESCONTROLADORURL1414",
                    "104014"),

    URL1515("FRMFRAUDESCONTROLADORURL1515",
                    "300003"),

    URL1616("FRMFRAUDESCONTROLADORURL1616",
                    "260003");

    private final String key;
    private final String value;

    private FrmfraudesControladorUrlEnum(String key, String value) {
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
