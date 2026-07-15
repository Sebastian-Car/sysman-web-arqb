package com.sysman.contabilidad.enums;

public enum frmmovbancoxcntsControladorUrlEnum {
	
	URL15005("LIBRODEBANCOSCONTROLADORURL3361", "15005"),
	
	URL15003("LIBRODEBANCOSCONTROLADORURL4055", "15003"),
	
	URL4888("LIBRODEBANCOSCONTROLADORURL4888", "29118"),
	URL6232("LIBRODEBANCOSCONTROLADORURL6232", "29120");
	
	private final String key;
    private final String value;

    private frmmovbancoxcntsControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
