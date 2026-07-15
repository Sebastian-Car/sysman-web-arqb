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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 *
 */

public enum FrmevmanualsControladorEnum {

    ID_DE_CARGO("ID_DE_CARGO"),

    ESCALAFONJI("ESCALAFONJI"),

    ESCALAFON("ESCALAFON"),

    ID_DE_CATEGORIA("ID_DE_CATEGORIA"),

    NOMBREESCALAFON("NOMBREESCALAFON"),

    NOMBRECATEGORIA("NOMBRECATEGORIA"),

    NOMBREESCALAFONJI("NOMBREESCALAFONJI"),

    CATEGORIA("CATEGORIA"),

    NOMBRECATEGORIAJI("NOMBRECATEGORIAJI"),

    NOMBRECARGO("NOMBRECARGO"),

    NOMBRECARGOJI("NOMBRECARGOJI"),

    NOMBREDEPENDENCIA("NOMBREDEPENDENCIA"),

    NUMERO_MANUAL("NUMERO_MANUAL"),

    FECHA_INICIAL("FECHA_INICIAL"),

    CATEGORIA_JEFE_INMEDIATO("CATEGORIA_JEFE_INMEDIATO"),

    TIPO("TIPO"),

    NOMBRE_DEL_CARGO("NOMBRE_DEL_CARGO"),

    CARGO_JEFE_INMEDIATO("CARGO_JEFE_INMEDIATO"),

    ESCALAFON_JEFE_INMEDIATO("ESCALAFON_JEFE_INMEDIATO"),

    NOMBRE_CATEGORIA("NOMBRE_CATEGORIA"),

    VERSION("VERSION"),

    KEY_NUMERO_MANUAL("KEY_NUMERO_MANUAL"),

    KEY_VERSION("KEY_VERSION"),

    NOMBRE_MANUAL("NOMBRE_MANUAL")

    ;

    private final String value;

    private FrmevmanualsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
