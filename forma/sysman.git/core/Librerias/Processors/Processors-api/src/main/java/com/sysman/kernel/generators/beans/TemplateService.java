/*
* TemplateClass
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators.beans;

import java.util.List;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Java Bean para almacenar informacion de metadatos necesarios para generar una clase.
 */
public class TemplateService {
	
	private String pack;
	private String template;
	private List<MatcherBean> matcherBeanList;
	
	public TemplateService() {
		super();
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public List<MatcherBean> getMatcherBeanList() {
		return matcherBeanList;
	}

	public void setMatcherBeanList(List<MatcherBean> matcherBeanList) {
		this.matcherBeanList = matcherBeanList;
	}
}
