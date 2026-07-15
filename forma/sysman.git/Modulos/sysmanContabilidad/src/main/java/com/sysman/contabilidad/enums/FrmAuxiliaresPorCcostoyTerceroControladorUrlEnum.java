package com.sysman.contabilidad.enums;

public enum FrmAuxiliaresPorCcostoyTerceroControladorUrlEnum {
	URL001("BALANCEPORCCOSTOYTERCERO", "4013"),
	URL002("BALANCEPORCCOSTOYTERCERO","15005"),
	URL003("BALANCEPORCCOSTOYTERCERO","15003"),
	URL004("BALANCEPORCCOSTOYTERCERO","20017"),
    URL005("BALANCEPORCCOSTOYTERCERO","20019"),
    URL006("BALANCEPORCCOSTOYTERCERO","14001"),
    URL007("BALANCEPORCCOSTOYTERCERO","14033"),
    URL008("BALANCEPORCCOSTOYTERCERO", "16008"),
    URL009("BALANCEPORCCOSTOYTERCERO", "16010"); 
	

	private final String key;
	private final String value;

	private FrmAuxiliaresPorCcostoyTerceroControladorUrlEnum(String key, String value) {
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
