/*
 * FrmPlanAdquisicionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.plandesarrollo.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmPlanAdquisicionesControladorUrlEnum {

    URL6418("FRMPLANADQUISICIONESCONTROLADORURL6418",
                    "108009"),

    URL5943("FRMPLANADQUISICIONESCONTROLADORURL5943",
                    "4001"),

    URL5944("FRMPLANADQUISICIONESCONTROLADORURL5944",
                    "1041001"),

    URL6994("FRMPLANADQUISICIONESCONTROLADORURL6994",
                    "1039001"),

    URL6995("FRMPLANADQUISICIONESCONTROLADORURL6995",
                    "1039003"),

    URL6996("FRMPLANADQUISICIONESCONTROLADORURL6996",
                    "1039004"),

    URL6997("FRMPLANADQUISICIONESCONTROLADORURL6997",
                    "103900D"),

    URL4133("FRMPLANADQUISICIONESCONTROLADORURL4133",
                    "1039005"),

    URL6969("FRMPLANADQUISICIONESCONTROLADORURL6969",
                    "1043001"),

    URL7272("FRMPLANADQUISICIONESCONTROLADORURL7272",
                    "1041002"),

    ;

    private final String key;
    private final String value;

    private FrmPlanAdquisicionesControladorUrlEnum(String key, String value) {
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
