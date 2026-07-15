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
public enum FrmCaEjecucionPasControladorUrlEnum {

    URL11959("FRMCAEJECUCIONPASCONTROLADORURL11959","94109"),
    URL12000("FRMCAEJECUCIONPASCONTROLADORURL12000","94111"),
    URL13622("FRMCAEJECUCIONPASCONTROLADORURL13622","4001");

    private final String key;
    private final String value;

    private  FrmCaEjecucionPasControladorUrlEnum(String key, String value) {
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