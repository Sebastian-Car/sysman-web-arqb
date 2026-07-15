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
public class RespuestaCargarOcupacion {
	
	String Codigo;
	String Nombre;
	
	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return Codigo;
	}
	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		Codigo = codigo;
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
