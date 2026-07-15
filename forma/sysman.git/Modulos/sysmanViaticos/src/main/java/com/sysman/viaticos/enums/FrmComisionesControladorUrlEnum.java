/*
 * FrmComisionesControladorUrlEnum
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
public enum FrmComisionesControladorUrlEnum {

    URL16052("FRMCOMISIONESCONTROLADORURL16052",
                    "768001"),

    URL8275("FRMCOMISIONESCONTROLADORURL8275",
                    "769001"),

    URL9022("FRMCOMISIONESCONTROLADORURL9022",
                    "1001"),

    URL9122("FRMCOMISIONESCONTROLADORURL9122",
                    "1001"),

    URL8276("FRMCOMISIONESCONTROLADORURL8276",
                    "761007"),

    URL9123("FRMCOMISIONESCONTROLADORURL9123",
                    "2001"),

    URL8277("FRMCOMISIONESCONTROLADORURL8277",
                    "5002"),

    URL6646("FRMCOMISIONESCONTROLADORURL6646",
                    "62099"),

    URL6545("FRMCOMISIONESCONTROLADORURL6545",
                    "14173"),

    URL8784("FRMCOMISIONESCONTROLADORURL8784",
                    "36002"),

    URL1524("FRMCOMISIONESCONTROLADORURL1524",
                    "1725001"),

    URL4562("FRMCOMISIONESCONTROLADORURL4562",
                    "1001"),

    URL4521("FRMCOMISIONESCONTROLADORURL4521",
                    "2001"),

    URL4621("FRMCOMISIONESCONTROLADORURL4621",
                    "5002"),

    URL4247("FRMCOMISIONESCONTROLADORURL4247",
                    "5012")

    ;

    private final String key;
    private final String value;

    private FrmComisionesControladorUrlEnum(String key, String value) {
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
