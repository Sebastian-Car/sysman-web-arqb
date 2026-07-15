/**
 * Clase: FrmTipoActividadsstsControladorEnum.java
 * 
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */

package com.sysman.hojasdevida.enums;

/**
 * @version 1.0, 4/01/2018
 * @author fperez
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 *
 */

public enum FrmTipoActividadsstsControladorEnum {
    COMPANIA("COMPANIA"),

    CODIGO("CODIGO"),

    TIPO_TRANSACCION("TIPO_TRANSACCION"),

    NOMBRETIPOTX("NOMBRETIPOTX"),

    TIPO("TIPO"),

    FECHAGENERACION("FECHAGENERACION"),

    CODIGO_PLANTILLA("CODIGO_PLANTILLA");

    private final String value;

    private FrmTipoActividadsstsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
