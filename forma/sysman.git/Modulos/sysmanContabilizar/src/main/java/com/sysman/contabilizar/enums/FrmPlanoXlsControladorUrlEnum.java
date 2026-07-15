package com.sysman.contabilizar.enums;

public enum FrmPlanoXlsControladorUrlEnum {

	 URL0001("FRMPLANOXLSCONTROLADORURLENUM","15007"),
	 
	 URL0002("FRMPLANOXLSCONTROLADORURLENUM","23015"),
	 
	 URL0003("FRMPLANOXLSCONTROLADORURLENUM","16203"),
	    
	 URL0004("FRMPLANOXLSCONTROLADORURLENUM","20074"),
	 
	 URL0005("FRMPLANOXLSCONTROLADORURLENUM","34059"),
	 
	 URL0006("FRMPLANOXLSCONTROLADORURLENUM","13041");
		
	    private final String key;
	    private final String value;

	    private FrmPlanoXlsControladorUrlEnum(String key, String value) {
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
