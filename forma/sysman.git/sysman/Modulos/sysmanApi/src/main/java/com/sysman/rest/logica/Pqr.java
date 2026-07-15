package com.sysman.rest.logica;

import static com.sysman.rest.EnumRole.RECEIVER;

import java.io.Serializable;
import java.util.Date;

import com.sysman.rest.Ejecutor;


/**
 * Pojo requerido para obtener los datos requeridos del Pqr
 * 
 * @version 1.0, 04/03/2019
 * @author  mochoa
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
   
    public Pqr(String compania, String tipoTramite,Date fecha , String cedula,
        String nombre, String descripcion, String direccion, String correo,String anexo1,
        String anexo2,String anexo3) {
        this.setCompania(compania);
        this.setTipoTramite(tipoTramite);
        this.setFecha(fecha);
        this.setCedula(cedula);
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setDireccion(direccion);
         this.setCorreo(correo);
        this.setAnexo1(anexo1);
        this.setAnexo2(anexo2);
        this.setAnexo3(anexo3);

    }
    /**
     * Parametro para el proceso
     */
    private String tipoConsumo;
    /**
     * Parametro que almacena num de radicado para una consulta
     */
    private String numRadicado;
	/**
     * Parámetro que establecera el tipo de proceso que se va ha realizar en el caso va a ser siempre pqr
     */
    private String compania;
    /**
     * Parámetro que establecera el tipo de proceso que se va ha realizar en el caso va a ser siempre pqr
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
     * Parametro que alamacena el correo del tramitante de la PQR
     */
    private String anexo1;
    /**
     * Parametro que alamacena el correo del tramitante de la PQR
     */
    private String anexo2;
    /**
     * Parametro que alamacena el correo del tramitante de la PQR
     */
    private String anexo3;
    
    /**
	 * @return the descripcion
	 */
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
	 * @return the anexos
	 */
	public String getAnexo1() {
		return anexo1;
	}
	/**
	 * @param anexos the anexos to set
	 */
	public void setAnexo1(String anexos) {
		this.anexo1 = anexos;
	}
	/**
	 * @return the anexos
	 */
	public String getAnexo2() {
		return anexo2;
	}
	/**
	 * @param anexos the anexos to set
	 */
	public void setAnexo2(String anexos) {
		this.anexo2 = anexos;
	}
	/**
	 * @return the anexos
	 */
	public String getAnexo3() {
		return anexo3;
	}
	/**
	 * @param anexos the anexos to set
	 */
	public void setAnexo3(String anexos) {
		this.anexo3 = anexos;
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
	public String getNumRadicado() {
		return numRadicado;
	}

	/**
	 * @param numRadicado the numRadicado to set
	 */
	public void setNumRadicado(String numRadicado) {
		this.numRadicado = numRadicado;
	}
}
    
