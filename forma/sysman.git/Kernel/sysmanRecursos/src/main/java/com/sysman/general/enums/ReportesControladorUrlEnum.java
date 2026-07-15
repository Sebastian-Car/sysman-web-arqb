/*
* ReportesControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ReportesControladorUrlEnum {
   
           	URL8177("REPORTESCONTROLADORURL8177","1054001"),  
             	URL13756("REPORTESCONTROLADORURL13756","105700G"),  
             	URL15144("REPORTESCONTROLADORURL15144","105800G"),  
             	URL10305("REPORTESCONTROLADORURL10305","210128"),  
             	URL11554("REPORTESCONTROLADORURL11554","112115"),  
             	URL13311("REPORTESCONTROLADORURL13311","1056002"),
             	URL45227("REPORTESCONTROLADORURL45227","58001"),
             	URL9045("REPORTESCONTROLADORURL9045","1057003"),
             	URL8547("REPORTESCONTROLADORURL8547","1057004"),
             	URL6861("REPORTESCONTROLADORURL6861","1058001"),
             	URL8351("REPORTESCONTROLADORURL8351","105800C"),
             	URL14935("REPORTESCONTROLADORURL14935","105700C"),
             	URL12000("REPORTESCONTROLADORURL12000","1057006"),
             	URL11400("REPORTESCONTROLADORURL11400","1057002");
        	
	private final String key;
	private final String value;
	
	private  ReportesControladorUrlEnum(String key, String value) {
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
