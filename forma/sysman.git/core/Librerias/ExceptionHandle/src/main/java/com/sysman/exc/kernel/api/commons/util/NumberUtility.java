/*
* NumberUtility
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.exc.kernel.api.commons.util;

import com.sysman.exc.kernel.api.commons.util.enums.NumberEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.regex.Pattern;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase utilitaria que permite hacer conversiones a tipos numericos.
 */
public class NumberUtility {

	/**
	 * Valida si una cadena de caracteres (String) posee un
	 * patron numerico para ser convertido en dicho tipo.
	 */
	public static boolean isInteger(String inputNumber) {
		if ("0".equals(inputNumber)) {
			return true;
		}
		return Pattern.compile("^(-)?([1-9])+([0-9])*$").matcher(inputNumber).find();
	}

	/**
	 * Valida si una cadena de caracteres (String) posee un
	 * patron numerico decimal para ser convertido en dicho tipo.
	 */
	public static boolean isDecimal(String inputNumber) {
		return Pattern.compile("^(-)?([0-9\\.,])*$").matcher(inputNumber).find();
	}

	/**
	 * Convierte una cadena de caracteres (String) en un tipo numerico entero,
	 * que depemdiendo del tamanio puede llegar a convertirse en un Integer, Long 
	 * o BigInteger.
	 */
	public static Object toInteger(String inputNumber) throws SysmanException {
		if (StringUtility.isNullOrEmpty(inputNumber)) {
			throw new SysmanException("El objeto es nulo");
		}

		if (inputNumber.length() <= NumberEnum.INTEGER.getValue()) {
			return StringUtility.toInteger(inputNumber);
		} else if (inputNumber.length() > NumberEnum.INTEGER.getValue()) {
			return StringUtility.toLong(inputNumber);
		} else {
			return StringUtility.toBigInteger(inputNumber);
		}
	}

	/**
	 * Convierte una cadena de caracteres (String) en un tipo numerico decimal,
	 * que depemdiendo del tamanio puede llegar a convertirse en un Float, Double
	 * o un BigDecimal.
	 */
	public static Object toDecimal(String inputNumber) throws SysmanException {
		if (StringUtility.isNullOrEmpty(inputNumber)) {
			throw new SysmanException("El objeto es nulo");
		}

		if (inputNumber.length() <= NumberEnum.DOUBLE.getValue()) {
			return StringUtility.toFloat(inputNumber);
		} else if (inputNumber.length() > NumberEnum.DOUBLE.getValue()) {
			return StringUtility.toDouble(inputNumber);
		} else {
			return StringUtility.toBigDecimal(inputNumber);
		}
	}
}
