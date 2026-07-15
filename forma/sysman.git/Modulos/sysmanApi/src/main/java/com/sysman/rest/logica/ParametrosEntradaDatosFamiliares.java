/*-
 * ParametrosEntradaDatosFamiliares.java
 *
 * 1.0
 * 
 * 23 jul. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.logica;

/**
 * Recibidor de par&aacute;metros para las solicitudes de
 * actualizaci&oacute;n de datos de familiares de un empleado
 * determinado.
 * 
 * @version 1.1, 31 ago. 2018
 * @author jrodrigueza
 *
 */
public class ParametrosEntradaDatosFamiliares {
    /**
     * C&oacute;digo que identifica a la compa&ntilde;&iacute;a o
     * sucursal.
     */
    private String compania;
    /**
     * C&oacute;digo de la solicitud, ya sea consulta de
     * informaci&oacute;n o solicitud de vacaciones, permisos, entre
     * otros.Este par&aacute;metro ¡NO SE EST&Aacute; UTLIZANDO!
     * (sirve para tres cosas), se implementa por caprichos de otra
     * persona...
     */
    private int clase;
    /**
     * N&uacute;mero de documento del empleado que realiza la
     * petici&oacute;n.
     */
    private String cedulaEmpleado;
    /**
     * C&oacute;digo del parentesco que identifica la relaci&oacute;n
     * que tiene el familiar con el empleado.
     */
    private String parentesco;
    /**
     * Identificador del tipo de documento de identidad del familiar.
     */
    private String tipoDocumento;
    /**
     * N&uacute;mero de documento del familiar.
     */
    private String numeroDocumento;
    /**
     * Nombre del familiar.
     */
    private String nombre;
    /**
     * Primer apellido del familiar.
     */
    private String primerApellido;
    /**
     * Segundo apellido del familiar.
     */
    private String segundoApellido;
    /**
     * Direcci&oacute;n de residencia.
     */
    private String direccion;
    /**
     * Estado del familiar: activo, pensionado o retirado.
     */
    private String estado;
    /**
     * Fecha de nacimiento del familiar. Formato date-time definido
     * por RFC3339.
     */
    private String fechaNacimiento;
    /**
     * Char que representa el g&eacute;nero del familiar: (M)
     * Masculino o (F) Femenino.
     */
    private String genero;
    /**
     * N&uacute;mero de tel&eacute;fono del familiar.
     */
    private String telefono;
    /**
     * Indicador de afiliado a salud como beneficiario.
     */
    private boolean beneficiaroSalud;
    /**
     * Texto que describe la ocupaci&oacute;n del familiar.
     */
    private String ocupacion;
    /**
     * Observaciones asociadas al familiar del empleado.
     */
    private String observaciones;
    /**
     * NIT de la entidad que consume la API. No se utiliza, lo mismo
     * que el par&aacute;metro clase.
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
     * @return the cedulaEmpleado
     */
    public String getCedulaEmpleado() {
        return cedulaEmpleado;
    }

    /**
     * @param cedulaEmpleado
     * the cedulaEmpleado to set
     */
    public void setCedulaEmpleado(String cedulaEmpleado) {
        this.cedulaEmpleado = cedulaEmpleado;
    }

    /**
     * @return the parentesco
     */
    public String getParentesco() {
        return parentesco;
    }

    /**
     * @param parentesco
     * the parentesco to set
     */
    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    /**
     * @return the tipoDocumento
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento
     * the tipoDocumento to set
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * @return the numeroDocumento
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * @param numeroDocumento
     * the numeroDocumento to set
     */
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre
     * the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the primerApellido
     */
    public String getPrimerApellido() {
        return primerApellido;
    }

    /**
     * @param primerApellido
     * the primerApellido to set
     */
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    /**
     * @return the segundoApellido
     */
    public String getSegundoApellido() {
        return segundoApellido;
    }

    /**
     * @param segundoApellido
     * the segundoApellido to set
     */
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion
     * the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado
     * the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return the fechaNacimiento
     */
    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * @param fechaNacimiento
     * the fechaInicial to set
     */
    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * @return the genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * @param genero
     * the genero to set
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono
     * the telefono to set
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return the beneficiaroSalud
     */
    public boolean isBeneficiaroSalud() {
        return beneficiaroSalud;
    }

    /**
     * @param beneficiaroSalud
     * the beneficiaroSalud to set
     */
    public void setBeneficiaroSalud(boolean beneficiaroSalud) {
        this.beneficiaroSalud = beneficiaroSalud;
    }

    /**
     * @return the ocupacion
     */
    public String getOcupacion() {
        return ocupacion;
    }

    /**
     * @param ocupacion
     * the ocupacion to set
     */
    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones
     * the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
     * @return the entidad
     */
    public String getEntidad() {
        return entidad;
    }

    /**
     * @param entidad the entidad to set
     */
    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

}
