package com.sysman.bancoproyectos.enums;

public enum DisponibilidadPorActividadControladorUrlEnum {

	URL4828("LISRESULTADOSCONTROLADORURL4828", "4001"), // Año
	URL0001("DISPONIBILIDADPORACTIVIDADCONTROLADORURL", "558009"),// Actividad inicial
	URL0002("DISPONIBILIDADPORACTIVIDADCONTROLADORURL", "558011"),// Actividad final
	;
    private final String key;
    private final String value;

    private DisponibilidadPorActividadControladorUrlEnum(String key, String value)
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
