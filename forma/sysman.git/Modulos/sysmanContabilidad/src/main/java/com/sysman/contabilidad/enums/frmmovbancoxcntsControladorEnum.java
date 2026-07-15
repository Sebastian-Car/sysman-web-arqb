package com.sysman.contabilidad.enums;

public enum frmmovbancoxcntsControladorEnum {
	 TIPOINICIAL("TIPOINICIAL"),
	 
	 CLASECUENTA("CLASECUENTA"),
	 
	 CUENTAINICIAL("CUENTAINICIAL");
	
	   private final String value;

	    private frmmovbancoxcntsControladorEnum(String value)
	    {
	        this.value = value;
	    }

	    public String getValue()
	    {
	        return value;
	    }
}
