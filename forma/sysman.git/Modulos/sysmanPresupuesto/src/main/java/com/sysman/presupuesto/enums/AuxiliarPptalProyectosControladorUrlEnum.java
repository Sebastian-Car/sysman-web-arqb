/*
 * AuxiliarPptalProyectosControladorUrlEnum
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
public enum AuxiliarPptalProyectosControladorUrlEnum {

    URL7252("AUXILIARPPTALPROYECTOSCONTROLADORURL7252", "45016"),

    URL8370("AUXILIARPPTALPROYECTOSCONTROLADORURL8370", "14001"),

    URL9084("AUXILIARPPTALPROYECTOSCONTROLADORURL9084", "14026"),

    URL5910("AUXILIARPPTALPROYECTOSCONTROLADORURL5910", "45014"),

    URL10312("AUXILIARPPTALPROYECTOSCONTROLADORURL10312", "80003"),

    URL4885("AUXILIARPPTALPROYECTOSCONTROLADORURL4885", "25012"),

    URL4198("AUXILIARPPTALPROYECTOSCONTROLADORURL4198", "25008"),

    URL11516("AUXILIARPPTALPROYECTOSCONTROLADORURL11516", "80005");

    private final String key;
    private final String value;

    private AuxiliarPptalProyectosControladorUrlEnum(String key, String value) {
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
