/*-
 * SubbpproyectoplanindicativosControladorEnum.java
 *
 * 1.0
 * 
 * 27/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * 
 * @version 1.0, 27/09/2017
 * @author jcrodriguez
 *
 */
public enum SubbpproyectoplanindicativosControladorEnum {

    VIGENCIA_META_P("VIGENCIA_META_P"),

    PROGRAMA("PROGRAMA"),

    NOMBREACTIVIDAD("NOMBREACTIVIDAD"),

    NOMBRECOMPONENTE("NOMBRECOMPONENTE"),

    NOMBRETIPOCOMPONENTE("NOMBRETIPOCOMPONENTE"),

    PROYECTO("PROYECTO"),

    VIGENCIA_PLAN("VIGENCIA_PLAN"),

    VIGENCIA_PLAN_P("VIGENCIA_PLAN_P"),

    DESCRIPCION_AUX("DESCRIPCION_AUX"),

    TODAS("TODAS"),

    ID_PLAN_P("ID_PLAN_P"),

    DESCRIPCION_META("DESCRIPCION_META"),

    COMPONENTE("COMPONENTE"),

    TIPOCOMPONENTE("TIPOCOMPONENTE"),

    ID_PLAN("ID_PLAN"),

    NIVEL("NIVEL"),

    ID("ID"),

    VIGENCIAPLANP("VIGENCIAPLANP"),

    DIGITOS("DIGITOS"),

    VIGENCIAMETAP("VIGENCIAMETAP"),

    CODIGOPROYECTO("CODIGOPROYECTO");

    private final String value;

    private SubbpproyectoplanindicativosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
