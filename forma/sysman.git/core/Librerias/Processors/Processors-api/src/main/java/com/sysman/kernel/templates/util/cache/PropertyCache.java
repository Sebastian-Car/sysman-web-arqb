/*
* PropertyServiceUtil
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.util.cache;

import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.regex.Matcher;

import com.sysman.kernel.api.commons.util.enums.SignEnum;
import com.sysman.kernel.templates.beans.DataPattern;
import com.sysman.kernel.templates.processors.StringProcessor;
import com.sysman.kernel.templates.util.enums.PatternEnum;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase que obtiene de un properties y mantiene en cache la configuracion con 
 * el listado de patrones a aplicar e informacion necesaria para realizar el
 * refactoring.
 */ 
public class PropertyCache {
	
	private static PropertyCache service = new PropertyCache();
	private static Map<String,Map<String, DataPattern>> patternsMap = new TreeMap<>();
		
	private PropertyCache() {
		 super();
	} 
	 
	/**
	 * Obtener instancia unica para recuperar informacion e servicios.
	 */
	public static PropertyCache getInstance() {
		return service;
	}
	
	/* Cargar de archivo de propiedades una configuración establecida*/
	private static void load(String bundle) {		
		Map<String, DataPattern> patterns = new TreeMap<>();
		ResourceBundle rb = ResourceBundle.getBundle(bundle);
		Enumeration<String> e = rb.getKeys();
		while (e.hasMoreElements()) {
			String uniqueKey = e.nextElement().trim();
			int pos = uniqueKey.lastIndexOf(SignEnum.POINT.getValue());
			String objectkey = uniqueKey.substring(0, pos).trim();
			String attribute = uniqueKey.substring(pos + 1).trim();
			DataPattern pBean = patterns.get(objectkey);
			if (pBean == null) {
				pBean = new DataPattern();
			}
			Matcher m = StringProcessor.findPattern(PatternEnum.REPLACEMENT.getPattern(), attribute); 
			if (m.find()) {
				if (PatternEnum.REPLACEMENT.getPattern().equals(attribute)) {
					pBean.setPatternKey(uniqueKey);
					pBean.setPattern(rb.getString(uniqueKey));
				} else if (PatternEnum.REPLACEMENT.getPatternHome().equals(attribute)) {
					pBean.setPatternHomeKey(uniqueKey);
					pBean.setPatternHome(rb.getString(uniqueKey));
				} else {
					pBean.addPattern(uniqueKey, rb.getString(uniqueKey));
				}
			} else if (PatternEnum.REPLACEMENT.getReplace().equals(attribute)) {
				pBean.setReplaceKey(uniqueKey);
				pBean.setReplace(rb.getString(uniqueKey));
			} else if (PatternEnum.REPLACEMENT.getParam().equals(attribute)) {
				pBean.setParamKey(uniqueKey);
				pBean.setParam(rb.getString(uniqueKey));
			} else if (PatternEnum.REPLACEMENT.getReplaceHome().equals(attribute)) {
				pBean.setPatternHomeKey(uniqueKey);
				pBean.setReplaceHome(rb.getString(uniqueKey));
			} 
			pBean.setObjectKey(objectkey);
			patterns.put(objectkey, pBean);
		}
		patternsMap.put(bundle, patterns);
	}
	
	/**
	 * Obtener listado de objetos PatternBean con informacion de patrones y reemplazos respectivos. 
	 */
	public Map<String, DataPattern> getPatterns(String bundle) {
		Map<String, DataPattern> patternsBean = patternsMap.get(bundle);
		if (patternsBean == null) {
			load(bundle);
			return  patternsMap.get(bundle);
		}
		return patternsBean;
	}
	
	/**
	 * Obtener objeto PatternBean con informacion del patron y reemplazo respectivo. 
	 */
	public DataPattern getPattern(String bundle, String key) {
		Map<String, DataPattern> patternsBean = patternsMap.get(bundle);
		if (patternsBean == null) {
			load(bundle);
			return  patternsMap.get(bundle).get(key);
		}
		return patternsBean.get(key);
	}
}
