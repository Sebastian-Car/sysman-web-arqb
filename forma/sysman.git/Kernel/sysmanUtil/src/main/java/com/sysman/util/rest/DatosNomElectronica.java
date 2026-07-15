/**
 * 
 */
package com.sysman.util.rest;

import java.util.Map;

/**
 * @author avega
 *
 */
public class DatosNomElectronica {

	private String pruebaDeRegistro;    
	private String nitEmpleador;
    private String nitProveedor;
    private String version;
    private String testId;
    private String softwarePin;    
    private String usuarioAccion;
    private Map<String, Object> datosNomina;

	/**
	 * @return the datosNomina
	 */
	public Map<String, Object> getDatosNomina() {
		return datosNomina;
	}

	/**
	 * @param datosNomina the datosNomina to set
	 */
	public void setDatosNomina(Map<String, Object> datosNomina) {
		this.datosNomina = datosNomina;
	}
	
	public String getPruebaDeRegistro() {
		return pruebaDeRegistro;
	}

	public void setPruebaDeRegistro(String pruebaDeRegistro) {
		this.pruebaDeRegistro = pruebaDeRegistro;
	}

	public String getNitEmpleador() {
		return nitEmpleador;
	}

	public void setNitEmpleador(String nitEmpleador) {
		this.nitEmpleador = nitEmpleador;
	}

	public String getNitProveedor() {
		return nitProveedor;
	}

	public void setNitProveedor(String nitProveedor) {
		this.nitProveedor = nitProveedor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getSoftwarePin() {
		return softwarePin;
	}

	public void setSoftwarePin(String softwarePin) {
		this.softwarePin = softwarePin;
	}

	public String getUsuarioAccion() {
		return usuarioAccion;
	}

	public void setUsuarioAccion(String usuarioAccion) {
		this.usuarioAccion = usuarioAccion;
	}
    
    
}
