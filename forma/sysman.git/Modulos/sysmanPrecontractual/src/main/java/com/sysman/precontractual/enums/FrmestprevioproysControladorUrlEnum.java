/*
 * FrmestprevioproysControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmestprevioproysControladorUrlEnum {

    URL54361("FRMESTPREVIOPROYSCONTROLADORURL54361",
                    "481009"),

    URL109314("FRMESTPREVIOPROYSCONTROLADORURL109314",
                    "62042"),

    URL103407("FRMESTPREVIOPROYSCONTROLADORURL103407",
                    "61020"),

    URL26550("FRMESTPREVIOPROYSCONTROLADORURL26550",
                    "71010"),

    URL26551("FRMESTPREVIOPROYSCONTROLADORURL26551",
                    "71012"),

    URL28436("FRMESTPREVIOPROYSCONTROLADORURL28436",
                    "14112"),

    URL22667("FRMESTPREVIOPROYSCONTROLADORURL22667",
                    "481004"),

    URL42723("FRMESTPREVIOPROYSCONTROLADORURL42723",
                    "484003"),

    URL122879("FRMESTPREVIOPROYSCONTROLADORURL122879",
                    "481011"),

    URL92561("FRMESTPREVIOPROYSCONTROLADORURL92561",
                    "524001"),

    URL41629("FRMESTPREVIOPROYSCONTROLADORURL41629",
                    "484002"),

    URL37363("FRMESTPREVIOPROYSCONTROLADORURL37363",
                    "481010"),

    URL24197("FRMESTPREVIOPROYSCONTROLADORURL24197",
                    "14036"),

    URL23510("FRMESTPREVIOPROYSCONTROLADORURL23510",
                    "516001"),

    URL22090("FRMESTPREVIOPROYSCONTROLADORURL22090",
                    "104043"),

    URL24611("FRMESTPREVIOPROYSCONTROLADORURL24611",
                    "111003"),

    URL25113("FRMESTPREVIOPROYSCONTROLADORURL25113",
                    "482001"),

    URL43446("FRMESTPREVIOPROYSCONTROLADORURL43446",
                    "477002"),

    URL38098("FRMESTPREVIOPROYSCONTROLADORURL38098",
                    "430005"),

    URL45316("FRMESTPREVIOPROYSCONTROLADORURL45316",
                    "111009"),

    URL25897("FRMESTPREVIOPROYSCONTROLADORURL25897",
                    "62005"),
    
    URL1893001("FRMESTPREVIOPROYSCONTROLADORURL1893001",
            "1893001");

    private final String key;
    private final String value;

    private FrmestprevioproysControladorUrlEnum(String key, String value) {
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
