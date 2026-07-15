/*
* Document
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.beans;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Java Bean para almacenar informacion sobre el contenido de un archivo 
 * y las plantillas asociadas al mismo.
 */
public class Document {

	private StringBuffer fileContent ;
	private TemplateClass parameter;
	private TemplateClass url ;
	
	public Document() {

	}
	
	public StringBuffer getFileContent() {
		return fileContent;
	}
	public void setFileContent(StringBuffer fileContent) {
		this.fileContent = fileContent;
	}
	public TemplateClass getParameter() {
		return parameter;
	}
	public void setParameter(TemplateClass parameter) {
		this.parameter = parameter;
	}
	public TemplateClass getUrl() {
		return url;
	}
	public void setUrl(TemplateClass url) {
		this.url = url;
	}
	
	
	
	
	
}
