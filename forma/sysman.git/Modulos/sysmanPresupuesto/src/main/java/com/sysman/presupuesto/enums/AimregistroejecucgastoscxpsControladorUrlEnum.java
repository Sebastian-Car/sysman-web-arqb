/*
 * AimregistroejecucgastoscxpsControladorUrlEnum
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
public enum AimregistroejecucgastoscxpsControladorUrlEnum {

    URL4902("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL4902","7016"),
    URL4395("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL4395","4007"),
    URL5616("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL5616","94036"),
    URL6657("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL6657","94034");
    private final String key;
    private final String value;

    private  AimregistroejecucgastoscxpsControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
