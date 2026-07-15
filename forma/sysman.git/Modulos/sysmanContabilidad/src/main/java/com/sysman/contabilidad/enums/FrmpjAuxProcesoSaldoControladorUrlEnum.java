package com.sysman.contabilidad.enums;

public enum FrmpjAuxProcesoSaldoControladorUrlEnum {


	URL0001("FRMPJAUXPROCESOSALDOCONTROLADORURL", "16008"),
	URL0002("FRMPJAUXPROCESOSALDOCONTROLADORURL", "16003"),
	URL0003("FRMPJAUXPROCESOSALDOCONTROLADORURL", "14036"),
	URL0004("FRMPJAUXPROCESOSALDOCONTROLADORURL", "14038"),
	URL0005("FRMPJAUXPROCESOSALDOCONTROLADORURL", "1935001"),
	URL0006("FRMPJAUXPROCESOSALDOCONTROLADORURL", "1935003");

	private final String key;
	private final String value;

	private FrmpjAuxProcesoSaldoControladorUrlEnum(String key, String value) {
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
