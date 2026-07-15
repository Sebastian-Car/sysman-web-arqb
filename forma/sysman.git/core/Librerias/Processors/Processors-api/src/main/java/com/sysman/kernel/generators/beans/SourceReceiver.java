/*
* SourceReceiver
*
* 1.0
*
* 30/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators.beans;

import java.io.Serializable;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * La clase SourceReceiver permite guardar informacion relacionada con 
 * la accion que se quiere ejecutar (Clase Command)
 */  
public class SourceReceiver implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String sourcePath;
	private String targetPath;
	private StringBuffer content;

	public SourceReceiver() {
		super();
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public StringBuffer getContent() {
		return content;
	}

	public void setContent(StringBuffer content) {
		this.content = content;
	}
}
