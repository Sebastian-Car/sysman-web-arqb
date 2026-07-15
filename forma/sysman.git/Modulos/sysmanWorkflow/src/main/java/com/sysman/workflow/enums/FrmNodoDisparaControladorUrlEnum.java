package com.sysman.workflow.enums;

public enum FrmNodoDisparaControladorUrlEnum {

	    URL4334("FRMDNODOSCONTROLADORURL4334", "1035001"),

	    URL3984("FRMDNODOSCONTROLADORURL3984", "1032003"),

	    URL5169("FRMDNODOSCONTROLADORURL5169", "988005"),
	    
	    URL5170("FRMDNODOSCONTROLADORURL5169", "997001");

	    private final String key;
	    private final String value;

	    private FrmNodoDisparaControladorUrlEnum(String key, String value) {
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