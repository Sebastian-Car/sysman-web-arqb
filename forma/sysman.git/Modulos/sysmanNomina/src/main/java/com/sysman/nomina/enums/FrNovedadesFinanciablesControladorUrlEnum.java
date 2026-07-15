package com.sysman.nomina.enums;

public enum FrNovedadesFinanciablesControladorUrlEnum {
	
	
    URL8948("FrNovedadesFinanciablesControladorUrlEnum8948", "151024"),

    URL6298("SUBENOVEDADESCONTROLADORURL6298", "7027"),

    URL5617("SUBENOVEDADESCONTROLADORURL5617", "471008"),

    URL7130("SUBENOVEDADESCONTROLADORURL7130", "471048"),

    URL8216("SUBENOVEDADESCONTROLADORURL8216", "658001"),
	
	 URL8217("SUBENOVEDADESCONTROLADORURL8217", "616003");

    private final String key;
    private final String value;

    private FrNovedadesFinanciablesControladorUrlEnum(String key, String value)
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
