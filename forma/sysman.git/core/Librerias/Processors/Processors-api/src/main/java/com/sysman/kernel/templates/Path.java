/*
* Path
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates;

import java.io.File;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion para clasificar las variables generales usadas en
 * el tratamiennto de archivos.
 */  
public enum Path {
	
	HOME;
	 
	private final String USER_DIR = "user.dir";
	private final String BASE = "src"+ File.separator + "main"+ File.separator +"java" + File.separator;
	private final String path;
	
	private Path() {
		path = System.getProperty(USER_DIR) + File.separator + BASE;
	}
	
	public String getPath() {
		return path;
	} 
} 
