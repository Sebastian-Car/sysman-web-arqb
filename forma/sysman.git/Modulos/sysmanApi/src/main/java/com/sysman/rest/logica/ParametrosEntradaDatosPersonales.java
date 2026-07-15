/*-
 * ParametrosEntradaDatosPersonales.java
 *
 * 1.0
 * 
 * 17 jul. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.logica;

/**
 * Recibidor de par&aacute;metros para las solicitudes de
 * actualizaci&oacute;n de datos personales.
 * 
 * @version 1.1, 31 ago. 2018
 * @author jrodrigueza
 *
 */
public class ParametrosEntradaDatosPersonales {
    /**
     * C&oacute;digo que identifica a la compa&ntilde;&iacute;a o
     * sucursal.
     */
    private String compania;
    /**
     * C&oacute;digo de la solicitud, ya sea consulta de
     * informaci&oacute;n o solicitud de vacaciones, permisos, entre
     * otros. Este par&aacute;metro ¡NO SE EST&Aacute; UTLIZANDO!
     * (sirve para tres cosas), se implementa por caprichos de otra
     * persona...
     */
    private int clase;
    /**
     * N&uacute;mero de documento del empleado que realiza la
     * petici&oacute;n.
     */
    private String cedula;
    /**
     * Direcci&oacute;n de correo electr&oacute;nico del empleado.
     */
    private String correoElectronico;
    /**
     * Direcci&oacute;n de residencia
     */
    private String direccion;
    /**
     * Número de teléfono.
     */
    private String telefono;
    /**
     * C&oacute;digo del país de residencia.
     */
    private String pais;
    /**
     * C&oacute;digo del departamento de residencia.
     */
    private String departamento;
    /**
     * C&oacute;digo de la ciudad de residencia.
     */
    private String ciudad;
    /**
     * Talla de pantal&oacute;n.
     */
    private String tallaPantalon;
    /**
     * Talla de camisa.
     */
    private String tallaCamisa;
    /**
     * Talla de calzado.
     */
    private String tallaCalzado;
    /**
     * Talla de chaqueta.
     */
    private String tallaChaqueta;
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
     * @return the correoElectronico
     */
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    /**
     * @param correoElectronico
     * the correoElectronico to set
     */
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
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
     * @return the pais
     */
    public String getPais() {
        return pais;
    }

    /**
     * @param pais
     * the pais to set
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * @return the departamento
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * @param departamento
     * the departamento to set
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * @return the ciudad
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * @param ciudad
     * the ciudad to set
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * @return the tallaPantalon
     */
    public String getTallaPantalon() {
        return tallaPantalon;
    }

    /**
     * @param tallaPantalon
     * the tallaPantalon to set
     */
    public void setTallaPantalon(String tallaPantalon) {
        this.tallaPantalon = tallaPantalon;
    }

    /**
     * @return the tallaCamisa
     */
    public String getTallaCamisa() {
        return tallaCamisa;
    }

    /**
     * @param tallaCamisa
     * the tallaCamisa to set
     */
    public void setTallaCamisa(String tallaCamisa) {
        this.tallaCamisa = tallaCamisa;
    }

    /**
     * @return the tallaCalzado
     */
    public String getTallaCalzado() {
        return tallaCalzado;
    }

    /**
     * @param tallaCalzado
     * the tallaCalzado to set
     */
    public void setTallaCalzado(String tallaCalzado) {
        this.tallaCalzado = tallaCalzado;
    }

    /**
     * @return the tallaChaqueta
     */
    public String getTallaChaqueta() {
        return tallaChaqueta;
    }

    /**
     * @param tallaChaqueta
     * the tallaChaqueta to set
     */
    public void setTallaChaqueta(String tallaChaqueta) {
        this.tallaChaqueta = tallaChaqueta;
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
