package com.sysman.nomina.enums;

public enum FrmHorasExtrasControladorUrlEnum {
	
    URL4001("FRMHORASEXTRASCONTROLADORURL","4001"),
    
    URL151001("FRMHORASEXTRASCONTROLADORURL","151001");
	
	 private final String key;
	    private final String value;
	        
	    private  FrmHorasExtrasControladorUrlEnum(String key, String value) {
	        this.key   = key; 
	        this.value = value;
	    }
	        
	    public String getKey() {
	        return key;
	    }
	        
	    public String getValue() {
	        return value;
	    }
}
