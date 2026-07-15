package com.sysman.nomina.enums;

public enum FrmCambiarFondoControladorUrlEnum {
	
	  URL617001("FRMCAMBIARFONDOCONTROLADOR", "617001"),
	  
	  URL1033004("FRMCAMBIARFONDOCONTROLADOR", "1033004"),
	  
	  URL1033006("FRMCAMBIARFONDOCONTROLADOR", "1033006")
	  
	  ;

	  private final String key;
    private final String value;

    private FrmCambiarFondoControladorUrlEnum(String key,
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
