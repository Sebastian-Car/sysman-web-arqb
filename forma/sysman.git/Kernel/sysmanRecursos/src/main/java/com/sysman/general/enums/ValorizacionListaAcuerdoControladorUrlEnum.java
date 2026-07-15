/*-
 * ActualizaparametrosretroactivosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 15/01/2018
 * @author vmolano
 *
 */
public enum ValorizacionListaAcuerdoControladorUrlEnum { 
    
    URL0001("VALORIZACIONLISTAACUERDOCONTROLADORURL", "1799001"),
    URL0003("VALORIZACIONLISTAACUERDOCONTROLADORURL", "1799003"),
    URL0005("VALORIZACIONLISTAACUERDOCONTROLADORURL", "58005") 
    ;

    private final String key;
    private final String value;

    private ValorizacionListaAcuerdoControladorUrlEnum(String key,
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
