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

package com.sysman.plandesarrollo.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 20/04/2018
 * @author ybecerra
 *
 */
public enum PlanDeMetasControladorEnum {

    TIPO("TIPO"),

    GETMRES("GETM_RES"),

    ID_PLAN("ID_PLAN"),

    ID("ID"),

    NOMBREDEPENDENCIA("NOMBREDEPENDENCIA"),

    DIGITOS("DIGITOS"),

    PREDECESOR("PREDECESOR"),

    ACCION("ACCION"),

    PI_PLAN_INDICATIVO("PI_PLAN_INDICATIVO"),

    NIT("NIT"),

    PLAN("PLAN"),

    CODIGOBPIM("CODIGOBPIM"),

    VIGENCIA_FINAL("VIGENCIA_FINAL"),

    DEPENDENCIA_INI("DEPENDENCIA_INI"),

    DEPENDENCIA_FIN("DEPENDENCIA_FIN"),

    ESNUEVO("ESNUEVO"),

    VIGENCIA_PLAN("VIGENCIA_PLAN"),

    VALOR_PRESUPUESTO_FIN("VALOR_PRESUPUESTO_FIN"),

    VALOR_PAGADO_FIN("VALOR_PAGADO_FIN"),

    ANOINICIAL("ANOINICIAL"),

    ANOFINAL("ANOFINAL"),

    VALOR_COMPROMETIDO_FIN("VALOR_COMPROMETIDO_FIN"),

    PORCENTAJECOMPROMETIDO("PORCENTAJECOMPROMETIDO"),

    VALOR_OBLIGACIONES_FIN("VALOR_OBLIGACIONES_FIN"),

    PORCENTAJEPAGADO("PORCENTAJEPAGADO"),

    PORCENTAJEOBLIGACIONES("PORCENTAJEOBLIGACIONES"),

    VIGENCIA_META("VIGENCIA_META"),

    ID_PLAN_INDICATIVO("ID_PLAN_INDICATIVO"),

    FUENTE("FUENTE"),

    NOMBREFUENTE("NOMBREFUENTE");

    private final String value;

    private PlanDeMetasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
