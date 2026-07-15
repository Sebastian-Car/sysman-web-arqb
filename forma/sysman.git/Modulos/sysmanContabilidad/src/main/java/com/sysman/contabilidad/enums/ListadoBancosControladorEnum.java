/*
* ListadoBancosControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum ListadoBancosControladorEnum {   
           
                  SYSDATE("SYSDATE"),
                  PR_RESPONSABLE_AREA("PR_RESPONSABLE_AREA"),
                  PR_DESCRIPCION_FECHAS("PR_DESCRIPCION_FECHAS"),
                  NOMBREINFORME("000639ListadoDeBancos"),
                  TIPOINICIAL("tipoInicial"),
                  TIPOFINAL("tipoFinal"),
                  FECHAINICIAL("fechaInicial"),
                  FECHAFINAL("fechaFinal"),
                  IDIOMA1("TB_TB522"),
                  IDIOMA2("TB_TB525"),                  
                  NOMBRE_RESPONSABLE_DE_AREA("NOMBRE RESPONSABLE DE AREA"),
                  MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),
                  ANIO("ANIO");
        	
	private final String value;
	
	private  ListadoBancosControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
