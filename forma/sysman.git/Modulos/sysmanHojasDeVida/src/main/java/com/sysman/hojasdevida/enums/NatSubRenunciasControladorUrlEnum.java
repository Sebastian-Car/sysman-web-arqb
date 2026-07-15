/*
 * NatSubRenunciasControladorUrlEnum
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
public enum NatSubRenunciasControladorUrlEnum {

    URL8754("NATSUBRENUNCIASCONTROLADORURL8754","697001"),
    URL6201("NATSUBRENUNCIASCONTROLADORURL6201","697004"),
    URL4968("NATSUBRENUNCIASCONTROLADORURL4968","697003"),
    URL5473("NATSUBRENUNCIASCONTROLADORURL5473","697005"),
    URL5594("NATSUBRENUNCIASCONTROLADORURL5594","465002"),
    URL7452("NATSUBRENUNCIASCONTROLADORURL7452","700001"),
    URL2346("NATSUBRENUNCIASCONTROLADORURL2346","463015"),
    URL8147("NATSUBRENUNCIASCONTROLADORURL2346","463030");

    private final String key;
    private final String value;

    private  NatSubRenunciasControladorUrlEnum(String key, String value) {
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
