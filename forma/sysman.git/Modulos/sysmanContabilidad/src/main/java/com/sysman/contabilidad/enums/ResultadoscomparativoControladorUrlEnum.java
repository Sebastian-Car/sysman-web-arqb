/*
 * ResultadoscomparativoControladorUrlEnum
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
public enum ResultadoscomparativoControladorUrlEnum {

    URL10009("RESULTADOSCOMPARATIVOCONTROLADORURL10009",
                    " List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"),

    URL4811("RESULTADOSCOMPARATIVOCONTROLADORURL4811",
                    "4001"),

    URL4317("RESULTADOSCOMPARATIVOCONTROLADORURL4317",
                    "4001");
    private final String key;
    private final String value;

    private ResultadoscomparativoControladorUrlEnum(String key, String value) {
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
