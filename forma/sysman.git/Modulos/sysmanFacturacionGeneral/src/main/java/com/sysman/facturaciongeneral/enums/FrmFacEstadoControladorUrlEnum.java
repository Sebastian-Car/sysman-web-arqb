/*
 * FrmFacEstadoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmFacEstadoControladorUrlEnum {

    URL6811("FRMFACESTADOCONTROLADORURL6811",
                    "1857001"),

    URL6300("FRMFACESTADOCONTROLADORURL6300",
                    "1856001"),

    URL5881("FRMFACESTADOCONTROLADORURL5881",
                    "1850001"),

    URL5559("FRMFACESTADOCONTROLADORURL5559",
                    "1855001"),

    URL8974("FRMFACESTADOCONTROLADORURL8974",
                    "1855002"),

    URL5198("FRMFACESTADOCONTROLADORURL5198",
                    "185500C"),

    URL2548("FRMFACESTADOCONTROLADORURL2548",
                    "185700C"),

    URL9848("FRMFACESTADOCONTROLADORURL9848",
                    "185600C"),

    URL5474("FRMFACESTADOCONTROLADORURL5474",
                    "1856003"),

    URL5587("FRMFACESTADOCONTROLADORURL5587",
                    "1857003"),

    URL4567("FRMFACESTADOCONTROLADORURL5587",
                    "1858001"),

    URL9457("FRMFACESTADOCONTROLADORURL9457",
                    "1851001"),

    URL4658("FRMFACESTADOCONTROLADORURL4658",
                    "185800C"),
    
    URL666015("FRMFACESTADOCONTROLADORURL666015",
            "666015"),
    
    URL72120("FRMFACESTADOCONTROLADORURL72120",
    		"72120"),
    ;

    private final String key;
    private final String value;

    private FrmFacEstadoControladorUrlEnum(String key, String value) {
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
