/*
 * CalificacionControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum frmactdatospersonalsControladorEnum {

    MENU_PERSONALES("2109010201"),

    MENU_FAMILIARES("2109010202"),

    MENU_EXTERNOS("2109010203"),

    PAIS("PAIS"),

    CEDULA("CEDULA"),

    SUCURSAL("SUCURSAL"),

    DEPARTAMENTO("DEPARTAMENTO"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    CONSECUTIVO("CONSECUTIVO"),

    ACTUALIZADO("ACTUALIZADO"),

    ENVIADO("ENVIADO"),

    DESTINO("DESTINO"),

    ACCION("ACCION"),

    TIPO("TIPO"),

    ESTADO("ESTADO"),

    NUMDOCUMENTO("NUMDOCUMENTO"),

    IDEMPLEADO("IDEMPLEADO"),

    NIT("NIT"),

    PORTRAMITAR("PORTRAMITAR"),

    DESTINATARIO("DESTINATARIO"),

    NOMBREEMPLEADO("NOMBREEMPLEADO")

    ;

    private final String value;

    private frmactdatospersonalsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
