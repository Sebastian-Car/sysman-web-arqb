/*
* Command
*
* 1.0
*
* 30/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators;

import com.sysman.kernel.generators.beans.SourceReceiver;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Interface que define una accion o comando que puede ser ejecutado
 */  
public interface Command {
	
	/**
	 * Permite hace una solicitud para ejecutar una operacion que requiere ser realizada   
	 */
	void execute();
	
	/** 
	 * Permite guardar informacion necesaria para ser usada en la operacion del metodo execute()
	 */
	void setSourceReceiver(SourceReceiver receiver);
}
