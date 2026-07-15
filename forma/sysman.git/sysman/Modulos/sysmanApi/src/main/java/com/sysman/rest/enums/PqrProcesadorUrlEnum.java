package com.sysman.rest.enums;
/**
 * Enumerado para DSS de PQR
 * @author mochoa
 *
 */
public enum PqrProcesadorUrlEnum {
	
	URL1045("ProcesaWorflowPqr1045", "1045005");
	
	
	  private final String key;
	    private final String value;

	    private PqrProcesadorUrlEnum(String key, String value) {
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
