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
public enum FrmAsignElementosProtPersonalControladorEnum {

    CONVOCATORIA("CONVOCATORIA"),

    SALARIO("SALARIO"),

    NOMBRE_EMPLEADO("NOMBRE_EMPLEADO"),

    NOMBREELEMENTO("NOMBREELEMENTO"),

    CEDULA("NUMCEDULA"),

    PARCARGO("CARGO ENCARGADO ENTREGA EPP");

    private final String value;

    private FrmAsignElementosProtPersonalControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
