/*
 * FrmConfigurarMovimientosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmConfigurarMovimientosControladorUrlEnum {

    URL7761("FRMCONFIGURARMOVIMIENTOSCONTROLADORURL7761",
                    "1794001"),

    URL8271("FRMCONFIGURARMOVIMIENTOSCONTROLADORURL8271",
                    "1794001"),

    URL3215("FRMCONFIGURARMOVIMIENTOSCONTROLADORURL3215",
                    "39086"),

    URL4587("FRMCONFIGURARMOVIMIENTOSCONTROLADORURL4587",
                    "39088"),
    URL4588("FRMCONFIGURARMOVIMIENTOSCONTROLADORURL4587",
            "7001"),
    URL4589("FRMCONFIGURARMOVIMIENTOSCONTROLADORURL4587",
            "7012"),
    URL4590("FRMCONFIGURARMOVIMIENTOSCONTROLADORURL4587",
            "4002");

    private final String key;
    private final String value;

    private FrmConfigurarMovimientosControladorUrlEnum(String key,
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
