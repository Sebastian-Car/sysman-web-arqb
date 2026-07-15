/*
 * EAlmacenContabilidadMControladorUrlEnum
 *
 * 1.0
 *
 * 06/07/2018
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilizar.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EalmacencontabilidadMControladorUrlEnum {
    // Año
    URL4410("EALMACENCONTABILIDADMCONTROLADORURLENUM4410", "4001"),
    // Mes
    URL4420("EALMACENCONTABILIDADMCONTROLADORURLENUM4420", "7001"),
    // CentroCosto
    URL4430("EALMACENCONTABILIDADMCONTROLADORURLENUM4430", "20013"),
    // Tercero
    URL4440("EALMACENCONTABILIDADMCONTROLADORURLENUM4440", "14001")

    ;

    private final String key;
    private final String value;

    private EalmacencontabilidadMControladorUrlEnum(String key, String value) {
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
