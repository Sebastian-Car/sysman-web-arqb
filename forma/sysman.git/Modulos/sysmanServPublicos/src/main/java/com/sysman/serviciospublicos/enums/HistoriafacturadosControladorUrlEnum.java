/*
 * HistoriafacturadosControladorUrlEnum
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
public enum HistoriafacturadosControladorUrlEnum {

    URL16650("HISTORIAFACTURADOSCONTROLADORURL16650",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, tabla,"),

    URL9838("HISTORIAFACTURADOSCONTROLADORURL9838",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, tabla,"),

    URL11040("HISTORIAFACTURADOSCONTROLADORURL11040",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, tabla,"),

    URL7055("HISTORIAFACTURADOSCONTROLADORURL7055", "298004"),

    URL1313("HISTORIAFACTURADOSCONTROLADORURL1313", "298002"),

    URL1414("HISTORIAFACTURADOSCONTROLADORURL1414", "298001"),

    URL5543("HISTORIAFACTURADOSCONTROLADORURL5543", "227007"),

    URL7004("HISTORIAFACTURADOSCONTROLADORURL7004", "227034"),

    URL6350("HISTORIAFACTURADOSCONTROLADORURL6350", "227016"),

    URL4883("HISTORIAFACTURADOSCONTROLADORURL4883", "227045"),

    URL4884("HISTORIAFACTURADOSCONTROLADORURL4884", "298005");

    private final String key;
    private final String value;

    private HistoriafacturadosControladorUrlEnum(String key, String value) {
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
