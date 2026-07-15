/**
 * 
 */
package com.sysman.nomina.enums;

/**
 * @author dcastiblanco
 *Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo 
 * legacy obtenido con patrones de busqueda.
 */
public enum ResumenporcentrocostoAgrupadoControladorUrlEnum {

	 URL4750("RESUMENPORCENTROCOSTOAGRUPADOCONTROLADORURL4750", "537002"),

	    URL4751("RESUMENPORCENTROCOSTOAGRUPADOCONTROLADORURL4751", "471002"),

	    URL4752("RESUMENPORCENTROCOSTOAGRUPADOCONTROLADORURL4752", "7024"),

	    URL4753("RESUMENPORCENTROCOSTOAGRUPADOCONTROLADORURL4753", "471003"),

	    URL4754("RESUMENPORCENTROCOSTOAGRUPADOCONTROLADORURL4754", "20056"),
	
	    URL4755("RESUMENPORCENTROCOSTOAGRUPADOCONTROLADORURL4755", "151001") ;

	    private final String key;
	    private final String value;

	    private ResumenporcentrocostoAgrupadoControladorUrlEnum(String key, String value)
	    {
	        this.key = key;
	        this.value = value;
	    }

	    public String getKey()
	    {
	        return key;
	    }

	    public String getValue()
	    {
	        return value;
	    }
	}
