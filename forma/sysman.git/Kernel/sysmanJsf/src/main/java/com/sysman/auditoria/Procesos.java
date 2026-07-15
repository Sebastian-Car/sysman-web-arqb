package com.sysman.auditoria;

public class Procesos {
	
	private String nombre;
    private String codigo;
    private String codCompania;
    private String codEntidad;
    private String codAplicacion;
    private String nomAplicacion;
    private boolean auditable;
    private String fechaCreacion;
    
    public Procesos() {}

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
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return the codCompania
	 */
	public String getCodCompania() {
		return codCompania;
	}

	/**
	 * @param codCompania the codCompania to set
	 */
	public void setCodCompania(String codCompania) {
		this.codCompania = codCompania;
	}

	/**
	 * @return the codEntidad
	 */
	public String getCodEntidad() {
		return codEntidad;
	}

	/**
	 * @param codEntidad the codEntidad to set
	 */
	public void setCodEntidad(String codEntidad) {
		this.codEntidad = codEntidad;
	}

	/**
	 * @return the codAplicacion
	 */
	public String getCodAplicacion() {
		return codAplicacion;
	}

	/**
	 * @param codAplicacion the codAplicacion to set
	 */
	public void setCodAplicacion(String codAplicacion) {
		this.codAplicacion = codAplicacion;
	}

	/**
	 * @return the nomAplicacion
	 */
	public String getNomAplicacion() {
		return nomAplicacion;
	}

	/**
	 * @param nomAplicacion the nomAplicacion to set
	 */
	public void setNomAplicacion(String nomAplicacion) {
		this.nomAplicacion = nomAplicacion;
	}

	/**
	 * @return the auditable
	 */
	public boolean isAuditable() {
		return auditable;
	}

	/**
	 * @param auditable the auditable to set
	 */
	public void setAuditable(boolean auditable) {
		this.auditable = auditable;
	}

	/**
	 * @return the fechaCreacion
	 */
	public String getFechaCreacion() {
		return fechaCreacion;
	}

	/**
	 * @param fechaCreacion the fechaCreacion to set
	 */
	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

}
