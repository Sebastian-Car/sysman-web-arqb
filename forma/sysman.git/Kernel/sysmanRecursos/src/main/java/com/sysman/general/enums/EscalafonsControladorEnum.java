/*
 * EscalafonsControladorEnum
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
public enum EscalafonsControladorEnum {
    ENCARRERA("ENCARRERA"),

    PLAZAS("PLAZAS"),

    NOMBRE_CATEGORIA("NOMBRE_CATEGORIA"),

    ID_DE_CATEGORIA("ID_DE_CATEGORIA"),

    NOMBRE_DEL_CARGO("NOMBRE_DEL_CARGO"),

    ID_DE_CARGO("ID_DE_CARGO"),

    TG_NO_EXISTE("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"),

    REPORTE800094("800094EmpleadosNivelesyCategorias"),

    ESCALAFON("ESCALAFON"),

    SALARIOANTERIOR("SALARIOANTERIOR"),

    CATEGORIA("CATEGORIA"),

    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),

    SALARIO_RETROACTIVO("SALARIO_RETROACTIVO"),

    VLR_INCREMENTO("VLR_INCREMENTO");

    private final String value;

    private EscalafonsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
