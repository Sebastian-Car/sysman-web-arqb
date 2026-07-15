/*
 * FrmmodificacionesdeudasControladorUrlEnum
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
public enum FrmmodificacionesdeudasControladorUrlEnum {

    URL11910("FRMMODIFICACIONESDEUDASCONTROLADORURL11910",
                    "283001"),

    URL15030("FRMMODIFICACIONESDEUDASCONTROLADORURL15030",
                    "283002"),

    URL34598("FRMMODIFICACIONESDEUDASCONTROLADORURL34598",
                    "213115"),

    URL17915("FRMMODIFICACIONESDEUDASCONTROLADORURL17915",
                    "213102"),

    URL31672("FRMMODIFICACIONESDEUDASCONTROLADORURL31672",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, spModificacionesDeudaC,"),

    URL33932("FRMMODIFICACIONESDEUDASCONTROLADORURL33932",
                    "213114"),

    URL17269("FRMMODIFICACIONESDEUDASCONTROLADORURL17269",
                    "309010"),

    URL13314("FRMMODIFICACIONESDEUDASCONTROLADORURL13314",
                    "215019"),

    URL16915("FRMMODIFICACIONESDEUDASCONTROLADORURL16915",
                    "337001"),

    URL41563("FRMMODIFICACIONESDEUDASCONTROLADORURL41563",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, spModificacionesDeudaC,"),

    URL47256("FRMMODIFICACIONESDEUDASCONTROLADORURL47256",
                    "309009"),

    URL20405("FRMMODIFICACIONESDEUDASCONTROLADORURL20405",
                    "213104"),

    URL16629("FRMMODIFICACIONESDEUDASCONTROLADORURL16629",
                    "215015");

    private final String key;
    private final String value;

    private FrmmodificacionesdeudasControladorUrlEnum(String key,
        String value) {
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
