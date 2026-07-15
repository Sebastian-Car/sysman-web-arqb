/*-
 * FrmevfuncionesControladorEnum.java
 *
 * 1.0
 *
 * 16/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum FrmevcompetenciasempleadosControladorEnum {

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    CONSECUTIVO("CONSECUTIVO"),

    CODIGO("CODIGO"),

    NUMERO_MANUAL("NUMERO_MANUAL"),

    DEPENDENCIA("DEPENDENCIA"),

    TIPO_COMPETENCIA("TIPO_COMPETENCIA"),

    VERSION("VERSION"),

    ID_COMPETENCIA("ID_COMPETENCIA"),

    EV_COMPETENCIAS("EV_COMPETENCIAS"),

    NOMBREPERSONA("NOMBREPERSONA"),

    NOMBREDEPENDENCIA("NOMBREDEPENDENCIA"),

    ID_EMPLEADO("ID_EMPLEADO"),

    NOMBREEMPLEADO("NOMBREEMPLEADO"),

    NOMBRETIPOCOMPETENCIA("NOMBRETIPOCOMPETENCIA");

    private final String value;

    private FrmevcompetenciasempleadosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
