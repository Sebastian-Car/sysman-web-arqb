/*
* MatcherBean
*
* 1.0
*
* 30/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators.beans;

import java.io.Serializable;
import java.util.List;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * La clase MatcherBean permite guardar informacion relacionada a 
 * las coincidencia que se vayan encontrando con el uso de patrones
 * de busqueda (Pattern).
 */  
public class MatcherBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String objectKey;
	private String patternKey;
	private String replaceKey;
	private String type;
	private String value;
	private String content;
	private List<MatcherBean> matcherBeanList;
	
	public MatcherBean() {
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

	public List<MatcherBean> getMatcherBeanList() {
		return matcherBeanList;
	}

	public void setMatcherBeanList(List<MatcherBean> matcherBeanList) {
		this.matcherBeanList = matcherBeanList;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
