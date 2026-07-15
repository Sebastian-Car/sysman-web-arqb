package com.sysman.bancoproyectos.enums;

public enum CdpAfectaSolicitudDisponibilidadUrlEnum {

	URL4828("LISRESULTADOSCONTROLADORURL4828", "4007"), // Año
	URL0001("LISTADOPORCODIGOYOBJETO","130054"), // Solicitud
	URL0002("LISTADOTIPOTYNOMBRE218", "218001") // Tipo
	;
    private final String key;
    private final String value;

    private CdpAfectaSolicitudDisponibilidadUrlEnum(String key, String value)
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
