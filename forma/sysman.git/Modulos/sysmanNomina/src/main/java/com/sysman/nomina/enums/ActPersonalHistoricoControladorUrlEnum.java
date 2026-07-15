package com.sysman.nomina.enums;

public enum ActPersonalHistoricoControladorUrlEnum {

	    URL7086("ACTPERSONALHISTORICOCONTROLADORURL", "537004"),

	    URL5665("ACTPERSONALHISTORICOCONTROLADORURL", "471008"),

	    URL6665("ACTPERSONALHISTORICOCONTROLADORURL", "471077"),
	    
	    URL6127("ACTPERSONALHISTORICOCONTROLADORURL", "7027"),
	    
;

	    private final String key;
	    private final String value;

	    private ActPersonalHistoricoControladorUrlEnum(String key, String value) {
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