package com.sysman.contabilidad.enums;

public enum CausacioningresosautControladorUrlEnum {
	
	URL14001("CAUSACIONINGRESOSAUTCONTROLADOR", "14001"),
	
	URL20026("CAUSACIONINGRESOSAUTCONTROLADOR", "20026"),
	
	URL23015("CAUSACIONINGRESOSAUTCONTROLADOR", "23015"),
	
	URL34001("CAUSACIONINGRESOSAUTCONTROLADOR", "34001"),
	
	URL13001("CAUSACIONINGRESOSAUTCONTROLADOR", "13001"),
	
	URL1997003("CAUSACIONINGRESOSAUTCONTROLADOR", "1997003");

    private final String key;
    private final String value;

    private CausacioningresosautControladorUrlEnum(String key,
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
