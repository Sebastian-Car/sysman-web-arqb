/*
 * EncargosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum EncargosControladorEnum {
    FECHAACTO("FECHAACTO"),

    IDIOMATB_TB75("TB_TB75"),

    CEDULA("CEDULA"),

    BORRAR("BORRAR"),

    DETALLE_ENCARGOC("DETALLE_ENCARGOC"),

    VALORGR("VALORGR"),

    SALARIO_BASE("SALARIO_BASE"),

    GASTOSREPRESENTACION("GASTOSREPRESENTACION"),

    NOMBRE_CATEGORIA("NOMBRE_CATEGORIA"),

    CODIGOGRADO("CODIGOGRADO"),

    ID_DE_CARGO("ID_DE_CARGO"),

    ID_DE_PROCESO("ID_DE_PROCESO"),

    ESCALAFON("ESCALAFON"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    SUELDOMENSUAL("SUELDOMENSUAL"),

    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),

    FECHAPAGO("FECHAPAGO"),

    FECHAINICIO("FECHAINICIO"),

    FECHAFINAL("FECHAFINAL"),

    ID_DE_CATEGORIA("ID_DE_CATEGORIA"),

    TIPO_NOVEDAD("TIPO_NOVEDAD"),

    CLASE_ENCARGO("CLASE_ENCARGO");

    private final String value;

    private EncargosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
