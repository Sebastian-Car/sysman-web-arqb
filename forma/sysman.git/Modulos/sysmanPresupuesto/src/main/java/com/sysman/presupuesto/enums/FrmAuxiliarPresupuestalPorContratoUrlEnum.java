package com.sysman.presupuesto.enums;

public enum FrmAuxiliarPresupuestalPorContratoUrlEnum {

	URL001("FrmAuxiliarPresupuestalPorContrato4001", "4001"),
	URL002("FrmAuxiliarPresupuestalPorContrato14036", "14036"),
	URL003("FrmAuxiliarPresupuestalPorContrato14038", "14038"),
	URL004("FrmAuxiliarPresupuestalPorContrato73021", "73021"),
	URL005("FrmAuxiliarPresupuestalPorContrato82121", "82121");

	private final String key;
	private final String value;

	private FrmAuxiliarPresupuestalPorContratoUrlEnum(String key, String value) {
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
