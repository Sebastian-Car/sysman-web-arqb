/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.logica;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author cmanrique
 */
public class Usuario implements Serializable {

    private String codigo;
    private String tipoCuenta;
    private String cedula;
    private String sucursal;
    private String nombre1;
    private String nombre2;
    private String apellido1;
    private String apellido2;
    private String pais;
    private String region;
    private String ciudad;
    private String correoElectronico;
    private Date fechaNacimiento;
    private String tituloProfesional;
    private String estilo;
    private String password;
    private String fraseDelDia;
    private String rutaImagen;
    private String estado;
    private String idioma;
    private String genero;
    private String direccion;
    private String celular;
    private String identificador;
    private String opcionesMenu;
    /**
     * Permite guardar los minutos que se espera para cerrar la
     * sessión cuando hay inactividad
     */
    private int minutosBloqueo;

    private Map<String, Formulario> permisos;
    private Map<String, Grupo> grupos;

    private Dependencia dependencia;
    private ResponsableAso responsableAso;

    private Aplicacion aplicacionGeneral;

    public Usuario() {
        //
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTituloProfesional() {
        return tituloProfesional;
    }

    public void setTituloProfesional(String tituloProfesional) {
        this.tituloProfesional = tituloProfesional;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFraseDelDia() {
        return fraseDelDia;
    }

    public void setFraseDelDia(String fraseDelDia) {
        this.fraseDelDia = fraseDelDia;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getOpcionesMenu() {
        return opcionesMenu;
    }

    public void setOpcionesMenu(String opcionesMenu) {
        this.opcionesMenu = opcionesMenu;
    }

    public Map<String, Formulario> getPermisos() {
        return permisos;
    }

    public void setPermisos(Map<String, Formulario> permisos) {
        this.permisos = permisos;
    }

    public Map<String, Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(Map<String, Grupo> grupos) {
        this.grupos = grupos;
    }

    public Dependencia getDependencia() {
        return dependencia;
    }

    public void setDependencia(Dependencia dependencia) {
        this.dependencia = dependencia;
    }

    public Aplicacion getAplicacionGeneral() {
        return aplicacionGeneral;
    }

    public void setAplicacionGeneral(Aplicacion aplicacionGeneral) {
        this.aplicacionGeneral = aplicacionGeneral;
    }

    public ResponsableAso getResponsableAso() {
        return responsableAso;
    }

    public void setResponsableAso(ResponsableAso responsableAso) {
        this.responsableAso = responsableAso;
    }

    /**
     * @return the minutosBloqueo
     */
    public int getMinutosBloqueo() {
        return minutosBloqueo;
    }

    /**
     * @param minutosBloqueo
     * the minutosBloqueo to set
     */
    public void setMinutosBloqueo(int minutosBloqueo) {
        this.minutosBloqueo = minutosBloqueo;
    }

    @Override
    public String toString() {
        return codigo;
    }

}
