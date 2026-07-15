/*
 * FuncionamientoFutControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FuncionamientoFutControladorUrlEnum {

    URL7526("FUNCIONAMIENTOFUTCONTROLADORURL7526",
                    "4001"),

    URL14094("FUNCIONAMIENTOFUTCONTROLADORURL14094",
                    " List<Registro> aux = service.getListado(ConectorPool.ESQUEMA_SYSMAN, consulta);");

    private final String key;
    private final String value;

    private FuncionamientoFutControladorUrlEnum(String key, String value) {
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
