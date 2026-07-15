/*
* ObjectUtility
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.api.commons.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import com.sysman.kernel.api.commons.util.exceptions.SysmanException;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase utilitaria que permite hacer validaciones basicas de objetos.
 */
public class ObjectUtility {

	private static final int ZERO = BigInteger.ZERO.intValue();
	
	/*
	 * Valida si un objeto es diferente de nulo
	 */
	private static boolean isObjectNotNullOrEmpty(Object o) {
		if (o != null) {
			return true;
		}
		return false;
	}

	/**
	 * Valida sin un objeto es diferente de nulo. Si se especifica true en
	 * el segundo parametro, valida si una coleccion o un mapa es diferente 
	 * de null o vacio.
	 */
	public static boolean isObjecNotNullOrEmpty(Object o, boolean isCollections) {
		if (isCollections) {
			if (o instanceof Collection) {
				Collection<?> c = (Collection<?>) o;
				if (c.size() > ZERO) {
					return true;
				}
			} else if (o instanceof Map) {
				Map<?,?> m = (Map<?,?>) o;
				if (m.size() > ZERO) {
					return true;
				}
			}
		} else {
			return isObjectNotNullOrEmpty(o);
		}
	
		return false;
	}
}
