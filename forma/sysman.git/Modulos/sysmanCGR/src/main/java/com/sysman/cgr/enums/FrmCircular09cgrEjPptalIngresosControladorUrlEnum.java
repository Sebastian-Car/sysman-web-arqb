package com.sysman.cgr.enums;

public enum FrmCircular09cgrEjPptalIngresosControladorUrlEnum {
	URL0001("FRMCIRCULAR09CGREJPPTALINGRESOSCONTROLADORURL0001", "4001"),
	
	URL0002("FRMCIRCULAR09CGREJPPTALINGRESOSCONTROLADORURL0002", "7001"),
	
	URL0003("FRMCIRCULAR09CGREJPPTALINGRESOSCONTROLADORURL0003", "45002"),
	
	URL0004("FRMCIRCULAR09CGREJPPTALINGRESOSCONTROLADORURL0004", "45004");

    private final String key;
    private final String value;

    private FrmCircular09cgrEjPptalIngresosControladorUrlEnum(String key, String value) {
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
