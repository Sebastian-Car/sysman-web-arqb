package sysman.util.consumo.pojo;

import java.io.Serializable;
import java.util.List;

public class Comercio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * identificacion del contribuyente 
	 */
	private String identificacion;

	/**
	 * nombre del contribuyente
	 */
	private String nombre;

	/**
	 * tipo de identificacion del contribuyente 
	 */
	private String tipoIdentificacion;

	/**
	 * Direccion del contribuyente
	 */
	private String direccion;
	
	/**
	 * telefono del contribuyente
	 */
	private String telefono;
	/**
	 * placaEstablecimiento
	 */
	private String placaEstablecimiento;
	/**
	 * razonSocial
	 */
	private String razonSocial;
	/**
	 * municipio
	 */
	private String municipio;
	/**
	 * departamento
	 */
	private String departamento;
	
	/**
	 * recalcula los conceptos de la declaracion
	 */
	private String recalcula;

	/**
	 * Listado de vigencias que entran en proceso
	 */
	private List<DeclaracionComercio> declaraciones;

	/**
	 * @return the identificacion
	 */
	public String getIdentificacion() {
		return identificacion;
	}

	/**
	 * @param identificacion the identificacion to set
	 */
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the tipoIdentificacion
	 */
	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	/**
	 * @param tipoIdentificacion the tipoIdentificacion to set
	 */
	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	/**
	 * @return the direccion
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * @param direccion the direccion to set
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * @return the telefono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * @param telefono the telefono to set
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * @return the placaEstablecimiento
	 */
	public String getPlacaEstablecimiento() {
		return placaEstablecimiento;
	}

	/**
	 * @return the razonSocial
	 */
	public String getRazonSocial() {
		return razonSocial;
	}

	/**
	 * @return the municipio
	 */
	public String getMunicipio() {
		return municipio;
	}

	/**
	 * @return the departamento
	 */
	public String getDepartamento() {
		return departamento;
	}

	/**
	 * @param placaEstablecimiento the placaEstablecimiento to set
	 */
	public void setPlacaEstablecimiento(String placaEstablecimiento) {
		this.placaEstablecimiento = placaEstablecimiento;
	}

	/**
	 * @param razonSocial the razonSocial to set
	 */
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	/**
	 * @param municipio the municipio to set
	 */
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	/**
	 * @param departamento the departamento to set
	 */
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	/**
	 * @return the recalcula
	 */
	public String getRecalcula() {
		return recalcula;
	}

	/**
	 * @param recalcula the recalcula to set
	 */
	public void setRecalcula(String recalcula) {
		this.recalcula = recalcula;
	}

	/**
	 * @return the declaraciones
	 */
	public List<DeclaracionComercio> getDeclaraciones() {
		return declaraciones;
	}

	/**
	 * @param declaraciones the declaraciones to set
	 */
	public void setDeclaraciones(List<DeclaracionComercio> declaraciones) {
		this.declaraciones = declaraciones;
	}



}
