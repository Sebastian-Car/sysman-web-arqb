/*
* DataPattern
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/

package com.sysman.kernel.templates.beans;

import java.util.Map;
import java.util.TreeMap;

import com.sysman.kernel.api.commons.util.ObjectUtility;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Java Bean para almacenar información de patrones y codigo de reemplazo.
 */
public class DataPattern {
	
	private String objectKey;
	private String patternKey;
	private String replaceKey;
	private String paramKey;
	private String patternHomeKey;
	private String replaceHomeKey;
	private String pattern;
	private String replace;
	private String param;
	private String patternHome;
	private String replaceHome;
	Map<String,String> patternsMap;

	public DataPattern() {
		super();
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getPatternKey() {
		return patternKey;
	}

	public void setPatternKey(String patternKey) {
		this.patternKey = patternKey;
	}

	public String getReplaceKey() {
		return replaceKey;
	}

	public void setReplaceKey(String replaceKey) {
		this.replaceKey = replaceKey;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getPatternHomeKey() {
		return patternHomeKey;
	}

	public void setPatternHomeKey(String patternHomeKey) {
		this.patternHomeKey = patternHomeKey;
	}

	public String getReplaceHomeKey() {
		return replaceHomeKey;
	}

	public void setReplaceHomeKey(String replaceHomeKey) {
		this.replaceHomeKey = replaceHomeKey;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getReplace() {
		return replace;
	}

	public void setReplace(String replace) {
		this.replace = replace;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getPatternHome() {
		return patternHome;
	}

	public void setPatternHome(String patternHome) {
		this.patternHome = patternHome;
	}

	public String getReplaceHome() {
		return replaceHome;
	}

	public void setReplaceHome(String replaceHome) {
		this.replaceHome = replaceHome;
	}
	
	public Map<String, String> getPatternsMap() {
		return patternsMap;
	}

	public void setPatternsMap(Map<String, String> patternsMap) {
		this.patternsMap = patternsMap;
	}

	public void addPattern(String key, String pattern) {
		if (!ObjectUtility.isObjecNotNullOrEmpty(patternsMap, true)) {
			patternsMap = new TreeMap<>();
		}
		if (!patternsMap.containsKey(key)) {
			patternsMap.put(key, pattern);
		}
	}

}
