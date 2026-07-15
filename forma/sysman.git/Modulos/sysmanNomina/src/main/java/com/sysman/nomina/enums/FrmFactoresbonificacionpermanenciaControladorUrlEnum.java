package com.sysman.nomina.enums;

public enum FrmFactoresbonificacionpermanenciaControladorUrlEnum {

	   URL004("FACTORESBONIFICACIONPERMANENCIA004","471008"),  
	    URL005("FACTORESBONIFICACIONPERMANENCIA005","471028"),  
	    URL006("FACTORESBONIFICACIONPERMANENCIA006","471010");

	    private final String key;
	    private final String value;

	    private  FrmFactoresbonificacionpermanenciaControladorUrlEnum(String key, String value) {
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