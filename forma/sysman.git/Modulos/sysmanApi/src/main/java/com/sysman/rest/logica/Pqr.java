package com.sysman.rest.logica;

import static com.sysman.rest.EnumRole.RECEIVER;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.sysman.rest.Ejecutor;

/**
 * Pojo requerido para obtener los datos requeridos del Pqr
 * 
 * @version 1.0, 04/03/2019
 * @author mochoa
 */
@Ejecutor(tipo = RECEIVER)
public class Pqr implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor vacio
	 */
	public Pqr() {

	}

	public Pqr(String compania, String tipoTramite, Date fecha, String cedula, String nombre, String descripcion,
		String direccion, String correo, String anexo1, String anexo2, String anexo3, String nombrecompania, String telefono,String genero,String codigoTramite, String rangoEdad,
		String tipoPersona, String tipoPoblacion, String vulnerabilidad, String ocupacion, String escolaridad) {	
		this.setCompania(compania);
		this.setTipoTramite(tipoTramite);
		this.setFecha(fecha);
		this.setCedula(cedula);
		this.setNombre(nombre);
		this.setDescripcion(descripcion);
		this.setDireccion(direccion);
		this.setCorreo(correo);
		this.setNombreCompania(nombrecompania);
		this.setTelefono(telefono);
		this.setGenero(genero);
		this.setCodigoTramite(codigoTramite);
		this.setRangoEdad(rangoEdad);
		this.setTipoPersona(tipoPersona);
		this.setTipoPoblacion(tipoPoblacion);
		this.setVulnerabilidad(vulnerabilidad);
		this.setOcupacion(ocupacion);
		this.setEscolaridad(escolaridad);
	}

	/**
	 * Parametro para el proceso
	 */
	private String tipoConsumo;
	/**
	 * Parametro que almacena num de radicado para una consulta
	 */
	private BigInteger numRadicado;
	/**
	 * Parámetro que establecera el tipo de proceso que se va ha realizar en el caso
	 * va a ser siempre pqr
	 */
	private String compania;
	/**
	 * Parámetro que establecera el tipo de proceso que se va ha realizar en el caso
	 * va a ser siempre pqr
	 */
	private String tipoTramite;
	/**
	 * parametro que establecera la fecha en la que se esta realizando la pqr
	 */
	private Date fecha;
	/**
	 * Parametro que alamacena la cedula del tramitante de la PQR
	 */
	private String cedula;
	/**
	 * Parametro que alamacena el nombre del tramitante de la PQR
	 */
	private String nombre;
	/**
	 * parametro que establecera el motivo por el cual se esta realizando la pqr
	 */
	private String descripcion;
	/**
	 * Parametro que alamacena la direccion del tramitante de la PQR
	 */
	private String direccion;
	/**
	 * Parametro que alamacena el correo del tramitante de la PQR
	 */
	private String correo;
	/**
	 * Parametro con los valores correspondientes a los anexos
	 */
	private List<PqrAnexo> anexos;
	/**
	 * parametro que almacena el nombre de la compania
	 */
	private String nombreCompania;
	/**
	 * parametro que almacena el número de telefono
	 */
	private String telefono;
	/**
	 * parametro que almacena el género 
	 */
	private String genero;
	/**
	 * @return the descripcion
	 */
	private String codigoTramite;
	
	private String rangoEdad;
	
	private String tipoPersona;
	
	private String vulnerabilidad;
	
	private String ocupacion;
	
	private String escolaridad;
	
	private String tipoPoblacion;
	
	private String desOcupacion;
	
	private String desTipoPoblacion;
	
	private Boolean anonimo;
				
	
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}

	/**
	 * @param compania the compania to set
	 */
	public void setCompania(String compania) {
		this.compania = compania;
	}

	/**
	 * @return the tipoTramite
	 */
	public String getTipoTramite() {
		return tipoTramite;
	}

	/**
	 * @param tipoTramite the tipoTramite to set
	 */
	public void setTipoTramite(String tipoTramite) {
		this.tipoTramite = tipoTramite;
	}

	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the cedula
	 */
	public String getCedula() {
		return cedula;
	}

	/**
	 * @param cedula the cedula to set
	 */
	public void setCedula(String cedula) {
		this.cedula = cedula;
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
	 * @return the correo
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * @param correo the correo to set
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	/**
	 * @return the tipoConsumo
	 */
	public String getTipoConsumo() {
		return tipoConsumo;
	}

	/**
	 * @param tipoConsumo the tipoConsumo to set
	 */
	public void setTipoConsumo(String tipoConsumo) {
		this.tipoConsumo = tipoConsumo;
	}

	/**
	 * @return the numRadicado
	 */
	public BigInteger getNumRadicado() {
		return numRadicado;
	}

	/**
	 * @param numRadicado the numRadicado to set
	 */
	public void setNumRadicado(BigInteger numRadicado) {
		this.numRadicado = numRadicado;
	}

	/**
	 * @return the nombreComapania
	 */
	public String getNombreCompania() {
		return nombreCompania;
	}

	/**
	 * @param nombreComapania the nombreComapania to set
	 */
	public void setNombreCompania(String nombreCompania) {
		this.nombreCompania = nombreCompania;
	}

	/**
	 * @return the anexos
	 */
	public List<PqrAnexo> getAnexos() {
		return anexos;
	}

	/**
	 * @param anexos the anexos to set
	 */
	public void setAnexos(List<PqrAnexo> anexos) {
		this.anexos = anexos;
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
	 * @return the genero
	 */
	public String getGenero() {
		return genero;
	}
	/**
	 * @param genero the genero to set
	 */
	public void setGenero(String genero) {
		this.genero = genero;
	}
	/**
	 * @return the codigoTramite
	 */
	public String getCodigoTramite() {
		return codigoTramite;
	}
	/**
	 * @param genero the codigoTramite to set
	 */
	public void setCodigoTramite(String codigoTramite) {
		this.codigoTramite = codigoTramite;
	}

	/**
	 * @return the rangoedad
	 */
	public String getRangoEdad() {
		return rangoEdad;
	}

	/**
	 * @param rangoedad the rangoedad to set
	 */
	public void setRangoEdad(String rangoEdad) {
		this.rangoEdad = rangoEdad;
	}

	/**
	 * @return the tipoPersona
	 */
	public String getTipoPersona() {
		return tipoPersona;
	}

	/**
	 * @param tipoPersona the tipoPersona to set
	 */
	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}

	/**
	 * @return the vulnerabilidad
	 */
	public String getVulnerabilidad() {
		return vulnerabilidad;
	}

	/**
	 * @param vulnerabilidad the vulnerabilidad to set
	 */
	public void setVulnerabilidad(String vulnerabilidad) {
		this.vulnerabilidad = vulnerabilidad;
	}

	/**
	 * @return the ocupacion
	 */
	public String getOcupacion() {
		return ocupacion;
	}

	/**
	 * @param ocupacion the ocupacion to set
	 */
	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	/**
	 * @return the escolaridad
	 */
	public String getEscolaridad() {
		return escolaridad;
	}

	/**
	 * @param escolaridad the escolaridad to set
	 */
	public void setEscolaridad(String escolaridad) {
		this.escolaridad = escolaridad;
	}

	/**
	 * @return the tipoPoblacion
	 */
	public String getTipoPoblacion() {
		return tipoPoblacion;
	}

	/**
	 * @param tipoPoblacion the tipoPoblacion to set
	 */
	public void setTipoPoblacion(String tipoPoblacion) {
		this.tipoPoblacion = tipoPoblacion;
	}
	
	/**
	 * @return the desTipoPoblacion
	 */
	public String getDesTipoPoblacion() {
		return desTipoPoblacion;
	}

	/**
	 * @param desTipoPoblacion the desTipoPoblacion to set
	 */
	public void setDesTipoPoblacion(String desTipoPoblacion) {
		this.desTipoPoblacion = desTipoPoblacion;
	}

	/**
	 * @return the desOcupacion
	 */
	public String getDesOcupacion() {
		return desOcupacion;
	}

	/**
	 * @param desOcupacion the desOcupacion to set
	 */
	public void setDesOcupacion(String desOcupacion) {
		this.desOcupacion = desOcupacion;
	}

	/**
	 * @return the anonimo
	 */
	public Boolean getAnonimo() {
		return anonimo;
	}

	/**
	 * @param anonimo the anonimo to set
	 */
	public void setAnonimo(Boolean anonimo) {
		this.anonimo = anonimo;
	}
	
	
}
