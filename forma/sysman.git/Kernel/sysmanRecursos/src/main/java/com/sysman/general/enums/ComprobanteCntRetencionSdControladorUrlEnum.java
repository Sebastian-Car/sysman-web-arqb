package com.sysman.general.enums;

public enum ComprobanteCntRetencionSdControladorUrlEnum {
	
	 URL24799("COMPROBANTECNTRETENCIONSCONTROLADORURL24799", "69002"),

	    URL6758("COMPROBANTECNTRETENCIONSCONTROLADORURL6758", "15019"),

	    URL7072("COMPROBANTECNTRETENCIONSCONTROLADORURL7072", "8005"),

	    URL7597("COMPROBANTECNTRETENCIONSCONTROLADORURL7597", "12005"),
	    
	    URL7593("COMPROBANTECNTRETENCIONSCONTROLADORURL7593", "1861001");

	    private final String key;
	    private final String value;

	    private ComprobanteCntRetencionSdControladorUrlEnum(String key,
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
