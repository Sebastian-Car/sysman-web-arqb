package com.sysman.presupuesto.enums;

public enum FrSolicDispRubroControladorUrlEnum {
	 URL4828("LISRESULTADOSCONTROLADORURL4828", "4007"), // Año
	URL536("EJECUCIONGASTOSCAQCONTROLADORURL536", "45018"), //RubroInicial
	URL557("EJECUCIONGASTOSCAQCONTROLADORURL557", "45020"), //RubroFinal
	URL003("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL003", "20013"), //CentroInicial
	URL004("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL004", "20015"), //CentroFinal
	URL007("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL007", "23006"), //AuxiliarInicial
	URL008("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL008", "23008"), // AuxiliarFinal
	URL009("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL009", "13001"), //ReferenciaInicial
	URL010("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL010", "13035"), // ReferenciaFinal
	URL011("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL011", "34001"), // FuenteIncial
	URL012("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL012", "34003"), //FuenteFinal
	;

	 private final String key;
    private final String value;

    private FrSolicDispRubroControladorUrlEnum(String key,
        String value) {
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
