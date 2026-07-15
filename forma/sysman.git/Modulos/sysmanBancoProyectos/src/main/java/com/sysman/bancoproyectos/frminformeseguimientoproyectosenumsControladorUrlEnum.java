package com.sysman.bancoproyectos;

public enum frminformeseguimientoproyectosenumsControladorUrlEnum {
	 URL9541("FRMINFORMESPROYECTOSCONTROLADORURL9541","32003"),  
	 URL10118("FRMINFORMESPROYECTOSCONTROLADORURL10118","32013");
	
	 private final String key;
	    private final String value;

	    private  frminformeseguimientoproyectosenumsControladorUrlEnum(String key, String value) {
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
