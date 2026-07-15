/*
 * RecursohumanosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.mantenimientoactivos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RecursohumanosControladorUrlEnum {

    URL19332("RECURSOHUMANOSCONTROLADORURL19332",
                    "455003"),

    URL7602("RECURSOHUMANOSCONTROLADORURL7602",
                    "455001"),

    URL9692("RECURSOHUMANOSCONTROLADORURL9692",
                    "457001"),

    URL14383("RECURSOHUMANOSCONTROLADORURL14383",
                    "455002"),

    URL18183("RECURSOHUMANOSCONTROLADORURL18183",
                    "457002"),

    URL17807("RECURSOHUMANOSCONTROLADORURL17807",
                    "45500D"),

    URL21293("RECURSOHUMANOSCONTROLADORURL21293",
                    "45700D"),

    URL001("RECURSOHUMANOSCONTROLADORURL001",
                    "14098"),

    URL002("RECURSOHUMANOSCONTROLADORURL002",
                    "14101"),

    URL003("RECURSOHUMANOSCONTROLADORURL003",
                    "14102"),

    URL004("RECURSOHUMANOSCONTROLADORURL004",
                    "14103"),

    URL005("RECURSOHUMANOSCONTROLADORURL005",
                    "1400D"),

    URL9853("RECURSOHUMANOSCONTROLADORURL9853",
                    "453001"),

    URL10400("RECURSOHUMANOSCONTROLADORURL10400",
                    "454001"),

    URL1515("RECURSOHUMANOSCONTROLADORURL1515",
                    "22001"),

    URL11177("RECURSOHUMANOSCONTROLADORURL11177",
                    "14104");

    private final String key;
    private final String value;

    private RecursohumanosControladorUrlEnum(String key, String value) {
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
