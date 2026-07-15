package sysman.util.consumo.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Pojo que permite capturar los datos del predio desde el servicio expuesto en
 * la arquitectura C y poder exponerlo en el modulo de WorkFlow (Procesos
 * Judiciales)
 * 
 * @version 1.0, 01/12/2020
 * @author Jos&eacute; Pascual G&oacute;mez Blanco
 *
 */
public class Predio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * C&oacute;digo catastral del predio
	 */
	private String codCatastral;

	/**
	 * nombre del due&ntilde;o principal del predio
	 */
	private String nombre;

	/**
	 * nit del due&ntilde;o principal del predio
	 */
	private String nit;

	/**
	 * matr&iacute;cula inmobiliaria del predio
	 */
	private String matriculaInmobiliaria;

	/**
	 * C&oacute;digo equivalente del predio (normalmente a 30 d&iacute;gitos)
	 */
	private String codEquivalente;

	/**
	 * Direcci&oacute;n que registra el predio
	 */
	private String direccion;

	/**
	 * Barrio que registra el predio
	 */
	private String barrio;
	
	/**
	 * telefono que registra el predio
	 */
	private String telefono;

	/**
	 * total de deuda en base al rango de vigencias seleccionadas el predio
	 */
	private Double total;

	/**
	 * Listado de vigencias que entran en proceso
	 */
	private List<PredioFacturado> vigencia;
	
	/**
	 * @return the codCatastral
	 */
	public String getCodCatastral() {
		return codCatastral;
	}

	/**
	 * @param codCatastral the codCatastral to set
	 */
	public void setCodCatastral(String codCatastral) {
		this.codCatastral = codCatastral;
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
	 * @return the nit
	 */
	public String getNit() {
		return nit;
	}

	/**
	 * @param nit the nit to set
	 */
	public void setNit(String nit) {
		this.nit = nit;
	}

	/**
	 * @return the matriculaInmobiliaria
	 */
	public String getMatriculaInmobiliaria() {
		return matriculaInmobiliaria;
	}

	/**
	 * @param matriculaInmobiliaria the matriculaInmobiliaria to set
	 */
	public void setMatriculaInmobiliaria(String matriculaInmobiliaria) {
		this.matriculaInmobiliaria = matriculaInmobiliaria;
	}

	/**
	 * @return the codEquivalente
	 */
	public String getCodEquivalente() {
		return codEquivalente;
	}

	/**
	 * @param codEquivalente the codEquivalente to set
	 */
	public void setCodEquivalente(String codEquivalente) {
		this.codEquivalente = codEquivalente;
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
	 * @return the barrio
	 */
	public String getBarrio() {
		return barrio;
	}

	/**
	 * @param barrio the barrio to set
	 */
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Double total) {
		this.total = total;
	}

	/**
	 * @return the vigencias
	 */
	public List<PredioFacturado> getVigencia() {
		return vigencia;
	}

	/**
	 * @param vigencias the vigencias to set
	 */
	public void setVigencia(List<PredioFacturado> vigencia) {
		this.vigencia = vigencia;
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
	
}
