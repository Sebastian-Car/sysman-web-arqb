/*
 * AuxContablePorAuxiliaresControladorUrl
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum auxContablePorAuxiliaresControladorUrlEnum {
	URL5222("AUXCONTABLEPORAUXILIARESCONTROLADORURL5222", "16008"), //cuenta inicial 
   
	URL5971("AUXCONTABLEPORAUXILIARESCONTROLADORURL5971", "16010"), //Cuentafinal 
    
	URL15003("AUXCONTABLEPORAUXILIARESCONTROLADORURLL15003", "15003"),//lista tipo comprobante final  
   
	URL3488("AUXCONTABLEPORAUXILIARESCONTROLADORURLL3488", "15005"),//lista tipo inicial comprobante final 
 
	URL7965("AUXCONTABLEPORAUXILIARESCONTROLADORURL7965", "20019"),

	URL7434("AUXCONTABLEPORAUXILIARESCONTROLADORURL7434", "20017"),
	    
	URL6436("AUXCONTABLEPORAUXILIARESCONTROLADORURL6436", "14001"),

	URL6904("AUXCONTABLEPORAUXILIARESCONTROLADORURL6904", "14031"),
	
	URL13028("AUXCONTABLEPORAUXILIARESCONTROLADORURL13028", "13028"), //Referencia Inicial

    URL13030("AUXCONTABLEPORAUXILIARESCONTROLADORURL13030", "13030"), //Referencia Final
    
	URL34043("AUXCONTABLEPORAUXILIARESCONTROLADORURL34043", "34043"), // Fuente Recursos Inicial

	URL34045("AUXCONTABLEPORAUXILIARESCONTROLADORURL34045", "34045"); // Fuente Recursos Final

    private final String key;
    private final String value;

    private auxContablePorAuxiliaresControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
