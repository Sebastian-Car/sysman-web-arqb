/*
* CambiosdenitsControladorEnum
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
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum CambiosdenitsControladorEnum {
   
                  PARAM2("PARAM2"),   
                  PARAM1("PARAM1"),  
                  PARAM3("PARAM3"),  
                  PARAM0("PARAM0"),
                  D_CAMBIOSDENIT("D_CAMBIOSDENIT"),
                  SUCURSALANTERIOR("SUCURSALANTERIOR"),
                  MSM_REGISTRO_MODIFICADO("MSM_REGISTRO_MODIFICADO"),
                  MSM_REGISTRO_ELIMINADO("MSM_REGISTRO_ELIMINADO"),
                  NITANTERIOR("NITANTERIOR"),
                  NIT("NIT"),
                  SUCURSALNUEVA("SUCURSALNUEVA"),
                  REGISTRADO("REGISTRADO"),
                  VALOR("30307"),
                  RNUM("RNUM"),
                  CAMBIOSDENIT("CAMBIOSDENIT"),
                  CANT("CANT"),
                  PAMETRO("ACTIVA BOTON MANTENIMIENTO TERCERO DETALLE PRESUPUESTO"),
                  NITNUEVO("NITNUEVO");
    
        	
	private final String value;
	
	private  CambiosdenitsControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
