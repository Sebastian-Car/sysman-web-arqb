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
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 02/01/2018
 * @author ybecerra
 *
 */
public enum RiesgosControladorEnum {

    TOTAL_AFILIADOS_RIESGOS("TOTAL_AFILIADOS_RIESGOS"),

    VALOR_FONDO_SOLIDARIDAD_ARP("VALOR_FONDO_SOLIDARIDAD_ARP"),

    VALOR_TOTAL_NETO_ARP("VALOR_TOTAL_NETO_ARP"),

    VALOR_SALDOPERIODOANTERIORARP("VALOR_SALDOPERIODOANTERIORARP"),

    SUBTOTAL_APORTES_ARP("SUBTOTAL_APORTES_ARP"),

    VALOR_INTERESES_MORA_ARP("VALOR_INTERESES_MORA_ARP"),

    DIAS_MORA_ARP("DIAS_MORA_ARP"),

    VALOR_TOTAL_COTIZACION("VALOR_TOTAL_COTIZACION"),

    VALOR_AP_PAGADOS_OTROS_RIESGOS("VALOR_AP_PAGADOS_OTROS_RIESGOS"),

    VALOR_TOTAL_INCAP_PAGADASARP("VALOR_TOTAL_INCAP_PAGADASARP"),

    TOTAL_APORTES_ARP("TOTAL_APORTES_ARP"),

    NOMBRE_FONDO("NOMBRE_FONDO"),

    TIPO_ADMINISTRADORA("TIPO_ADMINISTRADORA"),

    NIT("NIT"),

    CODIGO_FONDO("CODIGO_FONDO"),

    KEY_ID_DE_PROCESO("KEY_ID_DE_PROCESO"),

    KEY_SUCURSAL("KEY_SUCURSAL"),

    KEY_NIT("KEY_NIT"),

    KEY_CODIGO_FONDO("KEY_CODIGO_FONDO"),

    KEY_TIPO_ADMINISTRADORA("KEY_TIPO_ADMINISTRADORA"),

    KEY_PERIODO("KEY_PERIODO"),

    KEY_MES("KEY_MES"),

    KEY_ANO("KEY_ANO"),

    KEY_COMPANIA("KEY_COMPANIA");

    private final String value;

    private RiesgosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
