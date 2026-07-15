/**
 * 
 */
package com.sysman.presupuesto.enums;

/**
 * @author avega
 *
 */
public enum LisauxpptalcontratosControladorUrlEnum {
	URL14008("LISAUXPPTALCONTRATOSCONTROLADORURLENUM14008", "14008"),
	
	URL73010("LISAUXPPTALCONTRATOSCONTROLADORURLENUM73010", "73010"),
	
	URL82120("LISAUXPPTALCONTRATOSCONTROLADORURLENUM82120", "82120"),
	
	URL45014("LISAUXPPTALCONTRATOSCONTROLADORURLENUM45014", "45014");

	private final String key;
    private final String value;

    private LisauxpptalcontratosControladorUrlEnum(String key, String value) {
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
