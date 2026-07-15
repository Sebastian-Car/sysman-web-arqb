/*
 * PreReciboPropsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PreReciboPropsControladorUrlEnum {

    URL9270("PRERECIBOPROPSCONTROLADORURL9270", "385018"),

    URL19016("PRERECIBOPROPSCONTROLADORURL19016", "367177"),

    URL16480("PRERECIBOPROPSCONTROLADORURL16480", "385001"),

    URL19692("PRERECIBOPROPSCONTROLADORURL19692", "367185"),

    URL10648("PRERECIBOPROPSCONTROLADORURL10648", "367175"),

    URL15659("PRERECIBOPROPSCONTROLADORURL15659", "367022"),

    URL18549("PRERECIBOPROPSCONTROLADORURL18549",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, \"IP_USUARIOS_PREDIAL\","),

    URL13164("PRERECIBOPROPSCONTROLADORURL13164",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"IP_USUARIOS_PREDIAL\","),

    URL11915("PRERECIBOPROPSCONTROLADORURL11915",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"IP_USUARIOS_PREDIAL\","),

    URL15240("PRERECIBOPROPSCONTROLADORURL15240",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"IP_USUARIOS_PREDIAL\","),

    URL6331("PRERECIBOPROPSCONTROLADORURL6331", "367173"),

    URL8156("PRERECIBOPROPSCONTROLADORURL8156", "367174");

    private final String key;
    private final String value;

    private PreReciboPropsControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
