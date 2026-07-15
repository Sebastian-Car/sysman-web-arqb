/*
* FileCommandImpl
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators.impl;

import org.apache.log4j.Logger;

import com.sysman.kernel.generators.Command;
import com.sysman.kernel.generators.beans.SourceReceiver;
import com.sysman.kernel.templates.util.constants.GeneralConstant;
import com.sysman.kernel.templates.util.files.FileUtility;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase que permite ejecutar todas las operaciones necesarias para
 * la generacion de servicios web basados en una configuracion de base de datos
 */
public class FileCommandImpl implements Command {
	
	private static final Logger logger = Logger.getLogger(FileCommandImpl.class);
	
	private SourceReceiver receiver;
	
	@Override
	public void execute() {
		String[] files = FileUtility.getFilesNameDirectory(receiver.getSourcePath());
		if (files != null) {
			for (String file : files) {
				StringBuffer content = FileUtility.readFileContent(receiver.getSourcePath().concat("\\").concat(file));
				receiver.setContent(content);
				Generator.build(receiver);
			}
		}
		
		setSourceReceiver(receiver);
	}

	@Override
	public void setSourceReceiver(SourceReceiver receiver) {
		this.receiver = receiver;
	}

}
