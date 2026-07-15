/*
 * FrmTipoClasificadoresControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmTipoClasificadoresControladorEnum {

    PCT_APLICARLEY1607("PCT_APLICARLEY1607"),

    ANOPREPARAR("ANOPREPARAR"),
    
    CODIGOPADRE("CODIGOPADRE"),
    
    CODCLASIFICADOR("CODCLASIFICADOR"),
    
    NOMBREPADRE("NOMBREPADRE"),
    
    NOMBREHIJO("NOMBREHIJO"),
    
    IDPADRE("IDPADRE"),

    PREPARAANO("PREPARAANO"),
    
    CLASFIFICADORHIJO("CLASFIFICADORHIJO"),
    
    TIPOCLASIFICADORESHIJO("TIPOCLASIFICADORESHIJO"),
    
    IDHIJO("IDHIJO"),
    
    CLASECLASIFICADORPADRE("CLASECLASIFICADORPADRE"),
    
    TIPOCLASIFICADOR("TIPOCLASIFICADOR"),
    
    CODCLASECLASI("CODCLASECLASI"),
    
    ELREGISTROYAEXISTE("El HIJO ya pertence a el PADRE");

    private final String value;

    private FrmTipoClasificadoresControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
