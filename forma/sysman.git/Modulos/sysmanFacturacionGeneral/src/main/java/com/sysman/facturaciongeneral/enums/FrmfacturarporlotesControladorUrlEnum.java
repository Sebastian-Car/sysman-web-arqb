/*-
 * FrmlistadoRecaudoDifUrlEnum.java
 *
 * 1.0
 * 
 * 8/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * 
 * @version 1.0, 4/10/2018
 * @author mvenegas
 *
 */
public enum FrmfacturarporlotesControladorUrlEnum {

    URL0001("FACTURACIONPORLOTES001", "7001"), // MES

    URL0002("FACTURACIONPORLOTES002", "4001"), // AÑO

    URL0003("FACTURACIONPORLOTES003", "15067"), // TIPO COMPROBANTE

    URL0004("FACTURACIONPORLOTES004", "1732001"), // INMUEBLES
    
    URL1947001("FACTURACIONPORLOTESURLENUM1947001","1947001"), // INMUEBLES_FACTURACION INICIAL (TABLA)
    
    URL1947003("FACTURACIONPORLOTESURLENUM1947003","1947003"), // INMUEBLES_FACTURACION FINAL (TABLA)
    
    URL1947005("FACTURACIONPORLOTESURLENUM1947001","1947005"), // INMUEBLES_FACTURACION INICIAL POR UBICACION Y TERCERO (TABLA)
    
    URL1947007("FACTURACIONPORLOTESURLENUM1947003","1947007"), // INMUEBLES_FACTURACION FINAL POR UBICACION Y TERCERO (TABLA)
    
    URL666016("FACTURACIONPORLOTESURLENUM666016","666016"), // CONSULTA LOS OBJETO COBRO QUE SE VAN A FACTURAR
    
    URL661079("FACTURACIONPORLOTESURLENUM661079","661079"),
    
    URL14001("FACTURACIONPORLOTESURLENUM14001","14001"), // Tercero Inicial
    
    URL14176("FACTURACIONPORLOTESURLENUM14176","14176"), // Tercero Final
    ;

    private final String key;
    private final String value;

    private FrmfacturarporlotesControladorUrlEnum(String key, String value) {
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
