package com.sysman.almacen.enums;

public enum InventarioSumVencimientoControladorUrlEnum {

	URL3619("INVENTARIOSUMDEVAFECHACONTROLADORURL3619",
			"112032"),

	URL4263("INVENTARIOSUMDEVAFECHACONTROLADORURL4263",
			"112034");

	private final String key;
	private final String value;

	private InventarioSumVencimientoControladorUrlEnum(String key, String value) {
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
