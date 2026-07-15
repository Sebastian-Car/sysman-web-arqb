package com.sysman.presupuesto.enums;

public enum LisanualpacpagosControladorUrlEnum {
	
	URL4395("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL4395","4007"),
    URL5616("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL5616","45070"),
    URL6657("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL6657","45072");
    private final String key;
    private final String value;

    private  LisanualpacpagosControladorUrlEnum(String key, String value) {
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
