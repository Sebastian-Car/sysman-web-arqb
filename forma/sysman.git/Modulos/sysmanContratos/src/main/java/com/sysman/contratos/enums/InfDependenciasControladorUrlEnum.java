/*
 * InfDependenciasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InfDependenciasControladorUrlEnum {

    URL14664("INFDEPENDENCIASCONTROLADORURL14664",
                    " List<Registro> anios = service.getListado(ConectorPool.ESQUEMA_SYSMAN, consulta);"),

    URL5267("INFDEPENDENCIASCONTROLADORURL5267", "4001"),

    URL6192("INFDEPENDENCIASCONTROLADORURL6192", "62032"),

    URL5741("INFDEPENDENCIASCONTROLADORURL5741", "4032");

    private final String key;
    private final String value;

    private InfDependenciasControladorUrlEnum(String key, String value) {
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
