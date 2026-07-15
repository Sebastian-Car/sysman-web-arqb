package com.sysman.almacen.enums;

public enum ReportePorTerceroControladorUrlEnum {


	URL62013("REPORTEPORTERCEROCONTROLADORURL013",
			"62013"),

	URL62015("ReportePorTerceroControladorUrl015",
			"62015"),
	
	URL32003("ReportePorTerceroControladorUrl015",
			"32003"),
	
	URL32013("ReportePorTerceroControladorUrl015",
			"32013");

	private final String key;
	private final String value;

	private ReportePorTerceroControladorUrlEnum(String key, String value) {
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
