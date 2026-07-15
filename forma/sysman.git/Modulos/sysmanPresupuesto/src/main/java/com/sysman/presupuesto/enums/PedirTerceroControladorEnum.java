/*
* PedirTerceroControladorEnum
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
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum PedirTerceroControladorEnum {
   
              
                  CONTEO("CONTEO"),
                  REGISTROAUTOMATICO("registroAutomatico"),
                  STRTIPOCOMPROBANTE("STRTIPOCOMPROBANTE"),  
                  CODIGOCOM("CODIGOCOM"),  
                  TIPO_CPTE("TIPO_CPTE"),
                  PR_CARGO_SECRETARIA_HACIENDA("PR_CARGO_SECRETARIA_HACIENDA"),
                  PR_NOMBRE_SECRETARIA_HACIENDA("PR_NOMBRE_SECRETARIA_HACIENDA"),
                  PR_NOMBRE_COLUMNA_COD_PPTAL("PR_NOMBRE_COLUMNA_COD_PPTAL"),
                  PR_TEXTO_VENCIMIENTO_FORMATO_CDP("PR_TEXTO_VENCIMIENTO_FORMATO_CDP"),
                  PR_VISTO_BUENO("PR_VISTO_BUENO"),
                  PR_NOMBRE_COLUMNA_CENTRO_COSTO("PR_NOMBRE_COLUMNA_CENTRO_COSTO"),
                  PR_NOMBRE_COLUMNA_FUENTE("PR_NOMBRE_COLUMNA_FUENTE"),
                  PR_NOMBRE_COLUMNA_AUXILIAR_GENERAL("PR_NOMBRE_COLUMNA_AUXILIAR_GENERAL"),
                  PR_NOMBRE_COLUMNA_REFERENCIA("PR_NOMBRE_COLUMNA_REFERENCIA");
        	
	private final String value;
	
	private  PedirTerceroControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
