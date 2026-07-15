package com.sysman.cgr.enums;

public enum FrmCircular09cgrEjPptalGastosControladorUrlEnum {
	URL0001("FRMCIRCULAR09CGREJPPTALGASTOSCONTROLADORURL0001", "4001"),
	
	URL0002("FRMCIRCULAR09CGREJPPTALGASTOSCONTROLADORURL0002", "7001"),
	
	URL0003("FRMCIRCULAR09CGREJPPTALGASTOSCONTROLADORURL0003", "94052"),
	
	URL0004("FRMCIRCULAR09CGREJPPTALGASTOSCONTROLADORURL0004", "94066");

    private final String key;
    private final String value;

    private FrmCircular09cgrEjPptalGastosControladorUrlEnum(String key, String value) {
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
