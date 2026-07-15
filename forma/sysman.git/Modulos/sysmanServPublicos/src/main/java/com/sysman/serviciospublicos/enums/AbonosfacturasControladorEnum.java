/*
* AbonosfacturasControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum AbonosfacturasControladorEnum {
   
    CODRUTA("CODIGODERUTA"),  
    ANO("ANIO"),  
    KCOMPANIA("KEY_COMPANIA"),  
    KCICLO("KEY_CICLO"),  
    KRUTA("KEY_CODIGORUTA"),  
    VALOR("VALOR"),  
    TOTFACTURA("totFacturaPerActual"),  
    CICLOMIN("ciclo"),  
    PERIODOMIN("periodo"),  
    SERVICIO("SERVICIO"),  
    ABONO("ABONO"),  
    RUTASUBDETALLE("/subabonos.sysman"),
    CODRUTAMINUS("codigoRuta"),
    CONSECMINUS("consecutivo"),
    ANIOACT("anioAct"),
    PERIODOACT("periodoAct"),
    ACU("Acueducto"),
    ALC("Alcantarillado"),
    ASE("Aseo")
    ;
        	
	private final String value;
	
	private  AbonosfacturasControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
