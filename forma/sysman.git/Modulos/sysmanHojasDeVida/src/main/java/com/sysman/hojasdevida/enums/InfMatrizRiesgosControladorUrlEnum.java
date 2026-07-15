/*
 * CalificacionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InfMatrizRiesgosControladorUrlEnum {

    URL6229("INFMATRIZRIESGOSCONTROLADORURL6229", "1731001"),
    URL5746("INFMATRIZRIESGOSCONTROLADORURL5746", "1728001"),
    URL4123("INFMATRIZRIESGOSCONTROLADORURL4123", "173100R"),
    URL8711("INFMATRIZRIESGOSCONTROLADORURL8711", "1729004"),
    URL8254("INFMATRIZRIESGOSCONTROLADORURL8254", "1729003");

    private final String key;
    private final String value;

    private InfMatrizRiesgosControladorUrlEnum(String key,
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
