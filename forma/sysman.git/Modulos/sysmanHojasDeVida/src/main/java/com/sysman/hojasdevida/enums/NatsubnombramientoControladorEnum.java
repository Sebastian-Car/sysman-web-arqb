/*
 * TarifasfgControladorEnum
 *
 * 1.0
 *
 * 18/12/2017
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
public enum NatsubnombramientoControladorEnum {

    NO_TIPO("NO_TIPO"),

    NO_DEPEANTE("NO_DEPEANTE"),

    NO_DEPENUEV("NO_DEPENUEV"),

    NOMBREDEPENDENCIA("NOMBREDEPENDENCIA"),

    NO_ESCALAFON("NO_ESCALAFON"),

    ESCALAFON("ESCALAFON"),

    ID_DE_CARGO("ID_DE_CARGO"),

    NO_FECHAEFECTIVIDAD("NO_FECHAEFECTIVIDAD"),

    ID_DE_CATEGORIA("ID_DE_CATEGORIA"),

    CODAREA("CODAREA"),

    NO_ID_DE_CARGO("NO_ID_DE_CARGO"),

    NOMBRECARGO("NOMBRECARGO"),

    NOMBRE_DEL_CARGO("NOMBRE_DEL_CARGO"),

    NO_CATEGORIA("NO_CATEGORIA"),

    NO_FECHRESODECR("NO_FECHRESODECR"),

    NO_FECHACTAPOSE("NO_FECHACTAPOSE"),

    NB_CODIGOPERSONA("NB_CODIGOPERSONA"),

    RETIRO_O_NOMBRAMIENTO("RETIRO_O_NOMBRAMIENTO");

    private final String value;

    private NatsubnombramientoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
