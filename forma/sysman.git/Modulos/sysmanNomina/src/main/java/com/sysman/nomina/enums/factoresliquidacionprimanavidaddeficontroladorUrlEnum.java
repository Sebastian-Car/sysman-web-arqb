/**
 * 
 */
package com.sysman.nomina.enums;

/**
 * @author dcastiblanco
 *
 */
public enum factoresliquidacionprimanavidaddeficontroladorUrlEnum {
	 URL4061("FACTORESLIQUIDACIONPRIMANAVIDADDEFICONTROLADORURL4061", "471002"),

     URL4062("FACTORESLIQUIDACIONPRIMANAVIDADDEFICONTROLADORURL4062", "7024"),

     URL4063("FACTORESLIQUIDACIONPRIMANAVIDADDEFICONTROLADORURL4063", "471003");

     private final String key;
     private final String value;

     private factoresliquidacionprimanavidaddeficontroladorUrlEnum(String key, String value)
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


