/*
 * FrmdesviacionesControladorUrlEnum
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
public enum FrmdesviacionesControladorUrlEnum {

    URL5959("FRMDESVIACIONESCONTROLADORURL5959",
                    "104010"),
    
    URL1313("FRMDESVIACIONESCONTROLADORURL1313",
                    "333004"),
    
    
    URL1515("FRMDESVIACIONESCONTROLADORURL1515",
                    "332003"),
    
    URL9191("FRMDESVIACIONESCONTROLADORURL9191",
                    "334004"),
    
    URL9090("FRMDESVIACIONESCONTROLADORURL9090",
                    "214006"),

    URL10560("FRMDESVIACIONESCONTROLADORURL10560",
                    "213089"),

    URL9639("FRMDESVIACIONESCONTROLADORURL9639",
                    "213087"),

    URL8594("FRMDESVIACIONESCONTROLADORURL8594",
                    "214049");

    private final String key;
    private final String value;

    private FrmdesviacionesControladorUrlEnum(String key, String value) {
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
