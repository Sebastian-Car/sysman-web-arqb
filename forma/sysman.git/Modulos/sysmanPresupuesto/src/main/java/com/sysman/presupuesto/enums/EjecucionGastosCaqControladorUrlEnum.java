/*
 * BalanceapropiacioninicialControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EjecucionGastosCaqControladorUrlEnum {

    URL419("EJECUCIONGASTOSCAQCONTROLADORURL419", "4001"),

    URL452("EJECUCIONGASTOSCAQCONTROLADORURL452", "7001"),

    URL482("EJECUCIONGASTOSCAQCONTROLADORURL482", "203002"),

    URL536("EJECUCIONGASTOSCAQCONTROLADORURL536", "45018"),

    URL557("EJECUCIONGASTOSCAQCONTROLADORURL557", "45020"),

    URL558("EJECUCIONGASTOSCAQCONTROLADORURL558", "45065"),

    URL559("EJECUCIONGASTOSCAQCONTROLADORURL559", "45067"),

    URL584("EJECUCIONGASTOSCAQCONTROLADORURL584", "20013"),

    URL600("EJECUCIONGASTOSCAQCONTROLADORURL600", "20015"),

    URL622("EJECUCIONGASTOSCAQCONTROLADORURL622", "14001"),

    URL642("EJECUCIONGASTOSCAQCONTROLADORURL642", "14026"),

    URL663("EJECUCIONGASTOSCAQCONTROLADORURL663", "34001"),

    URL684("EJECUCIONGASTOSCAQCONTROLADORURL684", "34003"),

    URL707("EJECUCIONGASTOSCAQCONTROLADORURL707", "13001"),

    URL732("EJECUCIONGASTOSCAQCONTROLADORURL732", "13035"),

    URL749("EJECUCIONGASTOSCAQCONTROLADORURL749", "23006"),

    URL776("EJECUCIONGASTOSCAQCONTROLADORURL776", "23008"),

    URL796("EJECUCIONGASTOSCAQCONTROLADORURL796", "62002"),

    URL813("EJECUCIONGASTOSCAQCONTROLADORURL813", "62019"),

    URL800("EJECUCIONGASTOSCAQCONTROLADORURL800", "203007");

    private final String key;
    private final String value;

    private EjecucionGastosCaqControladorUrlEnum(String key,
        String value)
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
