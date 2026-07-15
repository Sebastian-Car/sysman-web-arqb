package com.sysman.contabilidad.enums;

public enum EgresosAuxiliaresControladorUrlEnum {
	
	URL20013("EGRESOSAUXILIARESCONTROLADORURL20013", "20013"),
	
	URL20015("EGRESOSAUXILIARESCONTROLADORURL20015", "20015"),
	
	URL23006("EGRESOSAUXILIARESCONTROLADORURL23006", "23006"),
	
	URL23008("EGRESOSAUXILIARESCONTROLADORURL23008", "23008"),
	
	URL13001("EGRESOSAUXILIARESCONTROLADORURL13001", "13001"),
	
	URL13035("EGRESOSAUXILIARESCONTROLADORURL13035", "13035"),
	
	URL34001("EGRESOSAUXILIARESCONTROLADORURL34001", "34001"),
	
	URL34003("EGRESOSAUXILIARESCONTROLADORURL34003", "34003");
	
	private final String key;
    private final String value;

    private EgresosAuxiliaresControladorUrlEnum(String key,
        String value)
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
