/*
 * PcontratosControladorEnum
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
public enum PcontratosControladorEnum {

    PARAM15("PARAM15"),

    PARAM14("PARAM14"),

    PARAM13("PARAM13"),

    PARAM9("PARAM9"),

    PARAM12("PARAM12"),

    PARAM11("PARAM11"),

    PARAM10("PARAM10"),

    PARAM6("PARAM6"),

    PARAM5("PARAM5"),

    PARAM8("PARAM8"),

    GRUPO("GRUPO"),

    CODIGOGRUPO("CODIGOGRUPO"),

    KEY_NUMERO("KEY_NUMERO"),

    KEY_CLASEORDEN("KEY_CLASEORDEN"),

    KEY_COMPANIA("KEY_COMPANIA"),

    PARAM0("CLASEF"),
    
    PLATAFORMA("PLATAFORMA"), 
    
    EQUIV_SIGEC("EQUIV_SIGEC"), 
    
    VALORTOTAL("VALORTOTAL"), 
    
    SIGEC("SIGEC"), 
    
    TERCERO("TERCERO"), 
    
    NOMBRE("NOMBRE"), 
    
    FECHAINICIO("FECHAINICIO"), 
    
    FECHAFINALIZACION("FECHAFINALIZACION"), 
    
    TIPO_SIGEC("TIPO_SIGEC"),
	
	NUMERO_CONTRATO("NUMERO_CONTRATO");

    private final String value;

    private PcontratosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
