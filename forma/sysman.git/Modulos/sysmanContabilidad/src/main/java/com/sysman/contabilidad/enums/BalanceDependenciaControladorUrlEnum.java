/*
 * BalanceDependenciaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum BalanceDependenciaControladorUrlEnum {

    URL3781("BALANCEDEPENDENCIACONTROLADORURL3781", "29007"),

    URL3247("BALANCEDEPENDENCIACONTROLADORURL3247", "4001"),

    URL4738("BALANCEDEPENDENCIACONTROLADORURL4738", "29009");

    private final String key;
    private final String value;

    private BalanceDependenciaControladorUrlEnum(String key, String value) {
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
