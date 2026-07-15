/**
 * 
 */
package com.sysman.nomina.enums;

/**
 * @author dcastiblanco
 *
 */
public enum FactorescesantiasdefinitivasControladorUrlEnum {
	 URL4061("FACTORESCESANTIASDEFINITIVASCONTROLADORURL4061", "471002"),

     URL4062("FACTORESCESANTIASDEFINITIVASCONTROLADORURL4062", "7024"),

     URL4063("FACTORESCESANTIASDEFINITIVASCONTROLADORURL4063", "471003");

     private final String key;
     private final String value;

     private FactorescesantiasdefinitivasControladorUrlEnum(String key, String value)
     {
         this.key = key;
         this.value = value;
     }

     public String getKey()
     {
         return key;
     }

     public String getValue()
     {
         return value;
     }

 }

