/*
 * NatdatospersonalesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum NatdatospersonalesControladorEnum {

    DPTOEXPCEDULA("DPTOEXPCEDULA"),

    PAISRESIDE("PAISRESIDE"),

    DEPTORESIDE("DEPTORESIDE"),

    MUNICIPIORESIDE("MUNICIPIORESIDE"),

    DEPTONCTO("DEPTONCTO"),

    PAIS("PAIS"),

    DEPARAMENTO_LABORA("DEPARAMENTO_LABORA"),
    
    NUMEMRODCTO("numeroDcto"),
    
    SUCURSAL("sucursal"),
    
    CODIGO("codigo"),
    
    DPNUMEDOCU("dp_numedocu"),
    
    IDEMPLEADO("idEmpleado"),
    
    REMUNERACION("remuneracion"),
    
    SEGURIDAD("seguridad");

    private final String value;

    private NatdatospersonalesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
