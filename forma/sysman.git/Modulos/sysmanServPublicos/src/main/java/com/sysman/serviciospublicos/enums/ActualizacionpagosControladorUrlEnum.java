/*
 * ActualizacionpagosControladorUrlEnum
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
public enum ActualizacionpagosControladorUrlEnum {

    URL6766("ACTUALIZACIONPAGOSCONTROLADORURL6766",
                    "276006"),

    URL53249("ACTUALIZACIONPAGOSCONTROLADORURL53249",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"SP_FACTURADO\","),

    URL16076("ACTUALIZACIONPAGOSCONTROLADORURL16076",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, spPagoCons,"),

    URL35818("ACTUALIZACIONPAGOSCONTROLADORURL35818",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"SP_ABONOS\","),

    URL17849("ACTUALIZACIONPAGOSCONTROLADORURL17849",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, spPagoCons,"),

    URL54389("ACTUALIZACIONPAGOSCONTROLADORURL54389",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"SP_FACTURADO\","),

    URL14951("ACTUALIZACIONPAGOSCONTROLADORURL14951",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, spPagoCons,"),

    URL8030("ACTUALIZACIONPAGOSCONTROLADORURL8030", "345001"),

    URL59048("ACTUALIZACIONPAGOSCONTROLADORURL59048",
                    "276008");

    private final String key;
    private final String value;

    private ActualizacionpagosControladorUrlEnum(String key, String value) {
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
