/*
 * ConsultasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConsultasControladorUrlEnum {

    URL7600("CONSULTASCONTROLADORURL7600",
                    "58001"),

    URL14935("CONSULTASCONTROLADORURL14935",
                    "105700C"),

    URL11400("CONSULTASCONTROLADORURL11400",
                    "1057002"),

    URL9654("CONSULTASCONTROLADORURL9654",
                    "1057001"),

    URL6861("CONSULTASCONTROLADORURL6861",
                    "1670001"),

    URL7334("CONSULTASCONTROLADORURL7334",
                    "1056002"),

    URL13306("CONSULTASCONTROLADORURL13306",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"D_CONSULTA_PARAMETROS\","),

    URL8351("CONSULTASCONTROLADORURL8351",
                    "167000C"),

    URL11903("CONSULTASCONTROLADORURL11903",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"D_CONSULTA_PARAMETROS\","),

    URL5850("CONSULTASCONTROLADORURL5850",
                    "105700G");

    private final String key;
    private final String value;

    private ConsultasControladorUrlEnum(String key, String value) {
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
