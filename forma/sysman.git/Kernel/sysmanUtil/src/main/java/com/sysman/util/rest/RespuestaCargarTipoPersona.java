/**
 * 
 */
package com.sysman.util.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author bcardenas
 *
 */
@XmlRootElement
public class RespuestaCargarTipoPersona {
	
	String id;
	String Nombre;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return Nombre;
	}
	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	
}
