/*
 * FrmPlanIndicativoIndiControladorUrlEnum
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
public enum FrmPlanIndicativoIndiControladorUrlEnum {

    URL16823("FRMPLANINDICATIVOINDICONTROLADORURL16823", "4001"),

    URL13639("FRMPLANINDICATIVOINDICONTROLADORURL13639",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"PI_PLAN_INDICATIVO_OBS\","),

    URL14845("FRMPLANINDICATIVOINDICONTROLADORURL14845",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"PI_PLAN_INDICATIVO_OBS\","),

    URL11702("FRMPLANINDICATIVOINDICONTROLADORURL11702",
                    "1683001"),
    URL554023("FRMPLANINDICATIVOINDICONTROLADORURL554023",
            "554023");

    private final String key;
    private final String value;

    private FrmPlanIndicativoIndiControladorUrlEnum(String key, String value)
    {
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
