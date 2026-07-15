/*-
 * ActualizaparametrosretroactivosControladorEnum.java
 *
 * 1.0
 *
 * 18/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeracion.
 *
 * @version 1.0, 28/03/2019
 * @author mzanguna
 *
 */
public enum AsignacionpagosespecialesControladorEnum {

    CATEGORIA("ID_DE_CATEGORIA"),

    PAGO("PAGO"),

    PAGOESPECIAL_PERSONAL("PAGOESPECIAL_PERSONAL"),

    PAGOESPECIALCARGOS("PAGOESPECIAL_CARGOS"),

    PAGOESPECIALCATEGORIA("PAGOESPECIAL_CATEGORIA"),

    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),

    ANO("ANIO"),

    CATESCALAFON("CATEGORIAESCALAFONREF"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    IDCATEGORIA("ID_DE_CATEGORIA"),

    APLICACOD("APLICACOD"),

    ESCALAFON("ESCALAFON"),

    CODESCALAFON("CODESCALAFON"),

    MENSAJEMOD("MSM_REGISTRO_MODIFICADO"),

    CODIGOPAGOESP("CODIGOPAGOESP"),

    MENSAJEEXITOSO("TB_TB1945"),

    CODIGODETALLEPAGO("CODIGODETALLEPAGO")

    ;

    private final String value;

    private AsignacionpagosespecialesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
