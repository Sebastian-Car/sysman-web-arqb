package com.sysman.util;

import java.io.Serializable;

public class Sentencia implements Serializable  {
	private static final long serialVersionUID = 666L;
	private String llave;
	private String sentencia;
	
	public Sentencia() {
		
	}

	/**
	 * @return the llave
	 */
	public String getLlave() {
		return llave;
	}

	/**
	 * @param llave the llave to set
	 */
	public void setLlave(String llave) {
		this.llave = llave;
	}

	/**
	 * @return the sentencia
	 */
	public String getSentencia() {
		return sentencia;
	}

	/**
	 * @param sentencia the sentencia to set
	 */
	public void setSentencia(String sentencia) {
		this.sentencia = sentencia;
	}
}
