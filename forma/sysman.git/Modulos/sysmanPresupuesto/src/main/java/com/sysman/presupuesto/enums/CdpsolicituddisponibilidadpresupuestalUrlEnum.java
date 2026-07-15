package com.sysman.presupuesto.enums;

public enum CdpsolicituddisponibilidadpresupuestalUrlEnum {

	URL4828("LISRESULTADOSCONTROLADORURL4828", "4007"), // Año
	URL0001("CDPSOLICITUDDISPONIBILIDADPRESUPUESTALURL", "431010"), // Solicitud
	URL0002("CDPSOLICITUDDISPONIBILIDADPRESUPUESTALURL", "1032006") // Tipo
	
	;
    
	private final String key;
    private final String value;

    private CdpsolicituddisponibilidadpresupuestalUrlEnum(String key, String value)
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
