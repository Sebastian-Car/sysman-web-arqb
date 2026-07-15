/*-
 * DatosTransaccionControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */	

package com.sysman.transautomaticas.enums;

public enum DatosTransaccionControladorEnum {

	COMPROBANTE("COMPROBANTE"),

	TIPO_CPTE("TIPO_CPTE"),
	
	NUMERO_MODELO("NUMERO_MODELO"),
	
	MEDIO("MEDIO_PAGO"),
	
        NOMBRECOD("CODIGONOMBRE"),
    
        NOMBRETERCERO("TERCERONOMBRE"),
        
        GASTOTIPO("TIPO_GASTO"),
        
        AUXILIARNOMBRE("AUXILIARNOMBRE"),
        
        NOMBRECONCEPTO("NOMBRECONCEPTO"),
        
        NOMBRETIPOGASTO("NOMBRETIPOGASTO"),
        
        NOMBREMEDIOPAGO("NOMBREMEDIOPAGO"),
        
        REFERENCIANOMBRE("REFERENCIANOMBRE"),
        
        CENTROCOSTONOMBRE("CENTROCOSTONOMBRE"),
        
        FUENTERECURSONOMBRE("FUENTERECURSONOMBRE"),
        
        AFECTAPPTO("AFECTAPPTO")
        ;
	
	private final String value;

	private DatosTransaccionControladorEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
