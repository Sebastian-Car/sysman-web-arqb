package com.sysman.workflow.enums;

public enum FrmRegistroProcesosControladorUrlEnum {

	  URL001("FRMREGISTROPROCESOSCONTROLADORURL","1042037"),
	  
	  URL002("FRMREGISTROPROCESOSCONTROLADORURL","1042039"),
	  
	  URL003("FRMREGISTROPROCESOSCONTROLADORURL","62105"),
	  
	  URL004("FRMREGISTROPROCESOSCONTROLADORURL","62107"),
	  
      URL005("FRMREGISTROPROCESOSCONTROLADORURL","47031"),
	  
	  URL006("FRMREGISTROPROCESOSCONTROLADORURL","47033");
	
    private final String key;
    private final String value;

    private FrmRegistroProcesosControladorUrlEnum(String key, String value) {
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
