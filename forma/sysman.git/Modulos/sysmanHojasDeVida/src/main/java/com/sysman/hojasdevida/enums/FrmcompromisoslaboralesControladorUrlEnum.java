/*
 * FrmcompromisoslaboralesControladorUrlEnum
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
public enum FrmcompromisoslaboralesControladorUrlEnum {

    URL7080("FRMCOMPROMISOSLABORALESCONTROLADORURL7080", "773011"),
    
    URL8574("FRMCOMPROMISOSLABORALESCONTROLADORURL8574", "98500N"),

    URL6387("FRMCOMPROMISOSLABORALESCONTROLADORURL6387", "753012"),

    URL5044("FRMCOMPROMISOSLABORALESCONTROLADORURL5044", "62038"),

    URL0001("FRMCOMPROMISOSLABORALESCONTROLADORURL0001", "118029"),

    URL5637("FRMCOMPROMISOSLABORALESCONTROLADORURL5637", "772001"),
    
    URL5743("FRMCOMPROMISOSLABORALESCONTROLADORURL5743", "210136"),
    
    URL1872("FRMCOMPROMISOSLABORALESCONTROLADORURL1872", "985001"),
    
    URL4217("FRMCOMPROMISOSLABORALESCONTROLADORURL4217", "947007");

    private final String key;
    private final String value;

    private FrmcompromisoslaboralesControladorUrlEnum(String key,
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
