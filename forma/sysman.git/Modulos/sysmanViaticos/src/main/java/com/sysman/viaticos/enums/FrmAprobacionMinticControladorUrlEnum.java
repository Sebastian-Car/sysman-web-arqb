/*
 * FrmAprobacionMinticControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmAprobacionMinticControladorUrlEnum {

    URL12550("FRMAPROBACIONMINTICCONTROLADORURL12550",
                    "5002"),

    URL11548("FRMAPROBACIONMINTICCONTROLADORURL11548",
                    "5002"),

    URL10202("FRMAPROBACIONMINTICCONTROLADORURL10202",
                    " listaPaisDestino = service.getListado(conectorPool, \"SELECT \" + \" PAISES.PAIS, \" + \" PAISES.NOMBRE FROM PAIS\");"),

    URL10536("FRMAPROBACIONMINTICCONTROLADORURL10536",
                    "2001"),

    URL11213("FRMAPROBACIONMINTICCONTROLADORURL11213",
                    "5002"),

    URL12216("FRMAPROBACIONMINTICCONTROLADORURL12216",
                    "2001"),

    URL9883("FRMAPROBACIONMINTICCONTROLADORURL9883",
                    "1001"),

    URL10881("FRMAPROBACIONMINTICCONTROLADORURL10881",
                    "2001"),

    URL11880("FRMAPROBACIONMINTICCONTROLADORURL11880",
                    "1001"),

    URL245157("FRMAPROBACIONMINTICCONTROLADORURL245157",
                    "761020"),

    URL28654("FRMAPROBACIONMINTICCONTROLADORURL28654",
                    "76100R"),

    URL272824("FRMAPROBACIONMINTICCONTROLADORURL272824",
                    "76100U");

    private final String key;
    private final String value;

    private FrmAprobacionMinticControladorUrlEnum(String key, String value) {
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
