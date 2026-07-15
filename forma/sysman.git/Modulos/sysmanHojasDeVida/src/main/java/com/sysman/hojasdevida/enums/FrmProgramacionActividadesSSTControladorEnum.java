/*
 * FrmProgramacionActividadesSSTControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmProgramacionActividadesSSTControladorEnum {

    COORDEVENTO("COORDEVENTO"),

    DEPENDENCIA("DEPENDENCIA"),

    MENU210802050102("210802050102"),

    MENU210802050203("210802050203"),

    NUMERODOCUMENTO("NUMERODOCUMENTO"),

    TIPODOCUMENTO("TIPODOCUMENTO"),

    IDEVENTO("IDEVENTO"),

    NOMBREDEP("NOMBREDEP"),

    NOMBREES("NOMBREES"),

    NIT("NIT"),

    NOMBRE("NOMBRE"),

    NITESTABLECIMIENTO("NITESTABLECIMIENTO"),

    SUCURSALCOOR("SUCURSALCOOR"),

    IDEMPLEADO("ID_DE_EMPLEADO"),

    ESCALAFON("ESCALAFON"),

    TIPOEVENTO("TIPOEVENTO");

    private final String value;

    private FrmProgramacionActividadesSSTControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
