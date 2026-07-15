package com.sysman.contabilidad.enums;

public enum FrmbalancefiscalControladorEnum {

	TERE("DES");
	
	 private final String value;

	    private FrmbalancefiscalControladorEnum(String value)
	    {
	        this.value = value;
	    }

	    public String getValue() {
	        return value;
	    }
}
