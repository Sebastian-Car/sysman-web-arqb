/**
 * 
 */
package com.sysman.contabilidad.enums;

/**
 * @author lvega
 *
 */
public enum GenerarcptepptalCausacionControladorUrlEnum {

    URL38069("GENERARCPTEPPTALCAUSACIONCONTROLADORURLENUM38069", "38069"),
    
    URL38072("GENERARCPTEPPTALCAUSACIONCONTROLADORURLENUM38072", "38072");

    private final String key;
    private final String value;

    private GenerarcptepptalCausacionControladorUrlEnum(String key,
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
