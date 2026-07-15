/*
* TemplateClass
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.beans;

import java.util.HashSet;
import java.util.Set;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Java Bean para almacenar informacion de metadatos necesarioss para generar una clase.
 */
public class TemplateClass {
	
	private int counter;
	private String pack;
	private String className;
	private String template;
	private StringBuffer content;
	private Set<String> params;
	private Set<UrlBean> ulrs;
	private String type ;
	
	public TemplateClass() {
		super();
	}
	
	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public String getPack() {
		return pack;
	}
	
	public void setPack(String pack) {
		this.pack = pack;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public StringBuffer getContent() {
		return content;
	}

	public void setContent(StringBuffer content) {
		this.content = content;
	}

	public Set<String> getParams() {
		return params;
	}
	
	public void setParams(Set<String> params) {
		this.params = params;
	}
	
	public Set<UrlBean> getUlrs() {
		return ulrs;
	}

	public void setUlrs(Set<UrlBean> ulrs) {
		this.ulrs = ulrs;
	}

	public void addParameter (String parametro ) {
		if ( params != null ) {
			params.add(parametro);
		} else {
			params = new HashSet<String>();
			params.add(parametro);
		}			
	} 
	
	public void addUrl (UrlBean url ) {
		if (ulrs != null) {
			ulrs.add(url);
		}else {
			ulrs = new HashSet<UrlBean>() ;
			ulrs.add(url);
		}	
	}
	
	
	public void increment() {
		counter++;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
}
