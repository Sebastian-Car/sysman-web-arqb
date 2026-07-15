/*
 * ResumenMovPptalesControladorUrlEnum
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
public enum ResumenMovPptalesControladorUrlEnum {

    URL3328("RESUMENMOVPPTALESCONTROLADORURL3328", "4001"),

    URL3725("RESUMENMOVPPTALESCONTROLADORURL3725", "94092"),

    URL4635("RESUMENMOVPPTALESCONTROLADORURL4635", "94094");

    private final String key;
    private final String value;

    private ResumenMovPptalesControladorUrlEnum(String key, String value) {
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
