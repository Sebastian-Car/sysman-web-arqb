/*-
 * ParametrosEntradaSolicitudes.java
 *
 * 1.0
 * 
 * 7/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.rest.logica;

/**
 * Clase que guardar&aacute; los parametros que se reciben por
 * parametro en los servicios de consulta y solicitud de autoservicio.
 * 
 * @version 2.0, 05/07/2018
 * @author jgomez
 * @author jrodrigueza
 *
 */
public class ParametrosEntradaSolicitudes {

    /**
     * C&oacute;digo que identifica a la compa&ntilde;&iacute;a o
     * sucursal.
     */
    private String compania;
    /**
     * C&oacute;digo de la solicitud, ya sea consulta de
     * informaci&oacute;n o solicitud de vacaciones, permisos, entre
     * otros.
     */
    private int clase;
    /**
     * N&uacute;mero de documento del empleado que realiza la
     * petici&oacute;n.
     */
    private String cedula;
    /**
     * Proceso de la n&oacute;mina.
     */
    private int proceso;
    /**
     * A&ntilde;o de trabajo.
     */
    private int ano;
    /**
     * Mes de trabajo.
     */
    private int mes;
    /**
     * Periodo de trabajo.
     */
    private int periodo;
    /**
     * Observaciones, destino u objeto de la consulta/solicitud.
     */
    private String observacion;
    /**
     * C&oacute;digo del tipo de solicitud. No aplica para consultas
     * de autoservicio.
     */
    private int tipoSolicitud;
    /**
     * Cadena que representa la fecha y hora inicial. Formato
     * date-time definido por RFC3339. No aplica para consultas de
     * autoservicio.
     */
    private String fechaInicial;
    /**
     * Cadena que representa la fecha y hora final. Formato date-time
     * definido por RFC3339. No aplica para consultas de autoservicio.
     */
    private String fechaFinal;
    /**
     * NIT de la entidad que consume la API.
     */
    private String entidad;

    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * @param compania
     * the compania to set
     */
    public void setCompania(String compania) {
        this.compania = compania;
    }

    /**
     * @return the clase
     */
    public int getClase() {
        return clase;
    }

    /**
     * @param clase
     * the clase to set
     */
    public void setClase(int clase) {
        this.clase = clase;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula
     * the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the proceso
     */
    public int getProceso() {
        return proceso;
    }

    /**
     * @param proceso
     * the proceso to set
     */
    public void setProceso(int proceso) {
        this.proceso = proceso;
    }

    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * @param ano
     * the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes
     * the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the periodo
     */
    public int getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo
     * the periodo to set
     */
    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion
     * the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /**
     * @return the tipoSolicitud
     */
    public int getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * @param tipoSolicitud
     * the tipoSolicitud to set
     */
    public void setTipoSolicitud(int tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * @return the fechaInicial
     */
    public String getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial
     * the fechaInicial to set
     */
    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * @return the fechaFinal
     */
    public String getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal
     * the fechaFinal to set
     */
    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * @return the entidad
     */
    public String getEntidad() {
        return entidad;
    }

    /**
     * @param entidad
     * the entidad to set
     */
    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

}