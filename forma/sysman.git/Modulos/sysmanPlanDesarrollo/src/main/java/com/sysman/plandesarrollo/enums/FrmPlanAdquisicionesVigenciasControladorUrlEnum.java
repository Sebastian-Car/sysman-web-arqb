/*
 * FrmPlanAdquisicionesVigenciasControladorUrlEnum
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
public enum FrmPlanAdquisicionesVigenciasControladorUrlEnum {

    URL8819("FRMPLANADQUISICIONESVIGENCIASCONTROLADORURL8819",
                    "4001"),

    URL8094("FRMPLANADQUISICIONESVIGENCIASCONTROLADORURL8094",
                    "1041003"),

    URL8095("FRMPLANADQUISICIONESVIGENCIASCONTROLADORURL8095",
                    "1041005"),

    URL8096("FRMPLANADQUISICIONESVIGENCIASCONTROLADORURL8096",
                    "1041006"),

    URL8097("FRMPLANADQUISICIONESVIGENCIASCONTROLADORURL8097",
                    "104100D"),

    URL5050("FRMPLANADQUISICIONESVIGENCIASCONTROLADORURL5050",
                    "1041007"),;

    private final String key;
    private final String value;

    private FrmPlanAdquisicionesVigenciasControladorUrlEnum(String key,
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
