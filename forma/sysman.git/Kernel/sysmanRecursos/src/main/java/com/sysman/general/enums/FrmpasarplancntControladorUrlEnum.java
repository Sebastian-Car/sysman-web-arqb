package com.sysman.general.enums;

public enum FrmpasarplancntControladorUrlEnum {
	 URL4001("FRMPASARPLANCNTCONTROLADORURLENMUM4001", "4001"),
	
	URL59021("FRMPASARPLANCNTCONTROLADORURLENUM59021", "59021"),
	
	URL4014("FRMPASARPLANCNTCONTROLADORURLENUM4014", "4014"); 

    private final String key;
    private final String value;

    private FrmpasarplancntControladorUrlEnum(String key,
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
