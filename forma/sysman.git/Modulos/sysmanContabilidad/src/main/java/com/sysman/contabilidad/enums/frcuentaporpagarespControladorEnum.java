package com.sysman.contabilidad.enums;

public enum frcuentaporpagarespControladorEnum {
	
	    NIT("NIT"),

	    TERCEROINICIAL("TERCEROINICIAL"),
	    
	    PARAM0("CUENTAINI");

	    private final String value;

	    private frcuentaporpagarespControladorEnum(String value) {
	        this.value = value;
	    }

	    public String getValue() {
	        return value;
	    }

}

