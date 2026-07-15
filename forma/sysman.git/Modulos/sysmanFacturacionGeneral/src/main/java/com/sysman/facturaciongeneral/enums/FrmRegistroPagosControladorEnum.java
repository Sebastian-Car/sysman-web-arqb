/*
 * FrmRegistroPagosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmRegistroPagosControladorEnum {

    ANIOFECPAGO("ANIOFECPAGO"),

    CUENTARECAUDO("CUENTARECAUDO"),

    NUMERO_FACTURA("NUMERO_FACTURA"), 
    	
    NROCONTRATOSIGEC("NROCONTRATOSIGEC"), 
    
    TIPOCONTRATOSIGEC("TIPOCONTRATOSIGEC"), 
    
    PLATAFORMA("PLATAFORMA"), 
    
    EQUIV_SIGEC("EQUIV_SIGEC"), 
    
    VALORTOTAL("VALORTOTAL"), 
    
    SIGEC("SIGEC"), 
    
    TERCERO("TERCERO"), 
    
    NOMBRE("NOMBRE"), 
    
    FECHAINICIO("FECHAINICIO"), 
    
    FECHAFINALIZACION("FECHAFINALIZACION"), 
    
    TIPO_SIGEC("TIPO_SIGEC");

    private final String value;

    private FrmRegistroPagosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
