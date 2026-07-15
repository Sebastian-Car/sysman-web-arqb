/*
* DateUtility
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.exc.kernel.api.commons.util;

import com.sysman.exc.kernel.api.commons.util.enums.DateFormatEnum;

import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase utilitaria que permite hacer validaciones, conversiones y formateo de fechas.
 */
public class DateUtility {
	
	/**
	 * Formatea una fecha suministrada para ser convertida de acuerdo al Timezone origen y
	 * al Timezone de destino que se haya suminisitrado. Dicho metodo toma en cuenta no
	 * solo la zona horaria sino el cambio de horario de las mismas.
	 */
	public static Date toDateByTimezone(Date date, TimeZone source , TimeZone target) {  
		System.out.println("Current Date is: " +date);
        long sourceDst = 0;  
        if(source.inDaylightTime(date)) {  
            sourceDst = source.getDSTSavings();  
        }  
  
        long sourceOffset = source.getRawOffset() + sourceDst;  
  
        long targetDst = 0;  
        if(target.inDaylightTime(date)) {  
            targetDst = target.getDSTSavings();  
        }  
        long targetOffset = target.getRawOffset() + targetDst;  
  
        return new Date(date.getTime() + (targetOffset - sourceOffset));  
    }
	
	/**
	 * Valida si la cadena de caracteres (String) suministrado cumple con un 
	 * patron de fecha especificado en el segundo parametro (DateFormatEnum)
	 */
	public static boolean isDate(String inputDate, DateFormatEnum dfe) {
		Matcher m = Pattern.compile(dfe.getRegExp()).matcher(inputDate);
		if (m.matches()) {
			return true;
		}
		return false;
	}
}
