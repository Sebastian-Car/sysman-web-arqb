package com.sysman.almacen.enums;

public enum FrmlistadoelementosUrlEnum {
	
		URL1538("PROCONTRACADQCONTROLADORURL3413", "141139"),

	    URL1539("PROCONTRACADQCONTROLADORURL2346", "141141");

	    private final String key;
	    private final String value;

	    private FrmlistadoelementosUrlEnum(String key, String value) {
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
