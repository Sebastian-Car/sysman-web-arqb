/*
 * VertimientoalcantarilladoxlsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum VertimientoalcantarilladoxlsControladorUrlEnum {

    URL8284("VERTIMIENTOALCANTARILLADOXLSCONTROLADORURL8284",
                    "227001"),

    URL9240("VERTIMIENTOALCANTARILLADOXLSCONTROLADORURL9240",
                    "227003"),

    URL10341("VERTIMIENTOALCANTARILLADOXLSCONTROLADORURL10341",
                    "214081"),

    URL8727("VERTIMIENTOALCANTARILLADOXLSCONTROLADORURL8727",
                    "227053"),

    URL16415("VERTIMIENTOALCANTARILLADOXLSCONTROLADORURL16415",
                    " List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"),

    URL13710("VERTIMIENTOALCANTARILLADOXLSCONTROLADORURL13710",
                    "317001"),

    URL9757("VERTIMIENTOALCANTARILLADOXLSCONTROLADORURL9757",
                    "227054"),

    URL10984("VERTIMIENTOALCANTARILLADOXLSCONTROLADORURL10984",
                    "118003");

    private final String key;
    private final String value;

    private VertimientoalcantarilladoxlsControladorUrlEnum(String key,
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
