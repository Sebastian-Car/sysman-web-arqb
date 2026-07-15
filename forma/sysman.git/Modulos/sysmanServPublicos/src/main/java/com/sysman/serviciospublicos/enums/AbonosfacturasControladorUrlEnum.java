/*
* AbonosfacturasControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum AbonosfacturasControladorUrlEnum {
   
  URL79326("ABONOSFACTURASCONTROLADORURL79326","231004"),  
  URL28057("ABONOSFACTURASCONTROLADORURL28057","213186"),  
  URL47384("ABONOSFACTURASCONTROLADORURL47384","213195"),  
  URL27247("ABONOSFACTURASCONTROLADORURL27247","233007"),  
  URL53644("ABONOSFACTURASCONTROLADORURL53644","213012"),  
  URL26301("ABONOSFACTURASCONTROLADORURL26301","345001"),  
  URL102949("ABONOSFACTURASCONTROLADORURL102949","213184"),  
  URL51750("ABONOSFACTURASCONTROLADORURL51750"," rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"),  
  URL24758("ABONOSFACTURASCONTROLADORURL24758","227047"),  
  URL29427("ABONOSFACTURASCONTROLADORURL29427","233006"),  
  URL98613("ABONOSFACTURASCONTROLADORURL98613"," rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"),  
  URL22467("ABONOSFACTURASCONTROLADORURL22467","233004"),  
  URL49896("ABONOSFACTURASCONTROLADORURL49896"," rsAux = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"),  
  URL30038("ABONOSFACTURASCONTROLADORURL30038","47013"),  
  URL23796("ABONOSFACTURASCONTROLADORURL23796","227001"),  
  URL63676("ABONOSFACTURASCONTROLADORURL63676","233005"),  
  URL106687("ABONOSFACTURASCONTROLADORURL106687","Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, spAbonos,"),  
  URL28876("ABONOSFACTURASCONTROLADORURL28876","214094"),  
  URL78718("ABONOSFACTURASCONTROLADORURL78718","Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, tabla,"),  
  URL59027("ABONOSFACTURASCONTROLADORURL59027","Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, tabla,");  

        	
	private final String key;
	private final String value;
	
	private  AbonosfacturasControladorUrlEnum(String key, String value) {
	    this.key   = key; 
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
