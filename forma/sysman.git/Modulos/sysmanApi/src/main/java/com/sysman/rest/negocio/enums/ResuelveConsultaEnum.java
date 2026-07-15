/*-
 * ResuelveConsultaEnum.java
 *
 * 1.0
 * 
 * 21 feb. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.negocio.enums;

/**
 * Enumerado para el controlador que resuelve las consultas del
 * generador de reportes.
 * 
 * @version 1.0, 21 feb. 2019
 * @author jrodrigueza
 *
 */
public enum ResuelveConsultaEnum {

    /**
     * 1054003 getConsultasrpSqlPorCompaniaYCodigoQuery
     */
    DSS_1054003("consultasrp/sqlporcompaniaycodigo", "1054003"),
    /**
     * 1055006 getReportesCondicionYConsultaPorReporteQuery
     */
    DSS_1055006("reportes/condicionyconsultaporreporte", "1055006"),
    /**
     * 1055006 getDparametrosconsultasEtiquetaYTipoDeParametroQuery
     */
    DSS_1670002("dparametrosconsultas/etiquetaytipodeparametro", "1670002"),
    /**
     * 1058004 getDparametrosEtiquetaYTipoParametroQuery
     */
    DSS_1058004("dparametros/etiquetaytipoparametro", "1058004");

    private final String key;
    private final String value;

    private ResuelveConsultaEnum(String key,
        String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
