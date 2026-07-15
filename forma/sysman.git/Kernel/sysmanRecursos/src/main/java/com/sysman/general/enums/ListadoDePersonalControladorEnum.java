/*
 * ListadoDePersonalControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ListadoDePersonalControladorEnum {

    ID_DE_CARGO("ID_DE_CARGO"),

    ENFERMEDADINICIAL("ENFERMEDADINICIAL"),

    INCAPACIDADINICIAL("INCAPACIDADINICIAL"),

    TIPOADMINISTRADORA("TIPOADMINISTRADORA"),

    EPSINICIAL("EPSINICIAL"),

    IDFORMA("IDFORMA"),

    CODIGOPROF("CODIGOPROF"),

    LICENCIA("LICENCIA"),

    INCAPACIDAD("INCAPACIDAD"),

    CARGOINICIAL("CARGOINICIAL"),

    CODIGOEN("CODIGOEN"),

    PROFESIONINICIAL("PROFESIONINICIAL"),

    FONDO_SALUD("FONDO_SALUD"),

    LICENCIAINICIAL("LICENCIAINICIAL"),

    CODIGOINICIAL("CODIGOINICIAL"),

    TIPOCONTRATOINICIAL("TIPOCONTRATOINICIAL"),

    PRNOMBREEMPRESA("PR_NOMBREEMPRESA"),

    FECHAINICIAL("fechaFinal");

    private final String value;

    private ListadoDePersonalControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
