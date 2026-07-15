/*
 * ClasusuariosyrangoacueductoxlsControladorUrlEnum
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
public enum ClasusuariosyrangoacueductoxlsControladorUrlEnum {

    URL8766("CLASUSUARIOSYRANGOACUEDUCTOXLSCONTROLADORURL8766", "227003"),

    URL10507("CLASUSUARIOSYRANGOACUEDUCTOXLSCONTROLADORURL10507", "227005"),

    URL9282("CLASUSUARIOSYRANGOACUEDUCTOXLSCONTROLADORURL9282", "227004"),

    URL13232("CLASUSUARIOSYRANGOACUEDUCTOXLSCONTROLADORURL13232", "317001"),

    URL8254("CLASUSUARIOSYRANGOACUEDUCTOXLSCONTROLADORURL8254", "227002"),

    URL9865("CLASUSUARIOSYRANGOACUEDUCTOXLSCONTROLADORURL9865", "214029"),

    URL7812("CLASUSUARIOSYRANGOACUEDUCTOXLSCONTROLADORURL7812", "227001");

    private final String key;
    private final String value;

    private ClasusuariosyrangoacueductoxlsControladorUrlEnum(String key,
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
