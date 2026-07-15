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
 * Clase que guardar&aacute; los parametros que se reciben por parametro en los servicios de consulta y solicitud de autoservicio.
 *
 * @version 2.0, 13/11/2018
 * @author mzanguna
 *
 */
public class ParametrosEntradaInventario
{
    /**
     * C&oacute;digo que identifica a la compa&ntilde;&iacute;a o sucursal.
     */
    private String compania;

    /**
     * Cedula a consultar el inventario.
     */
    private String cedula;

    /**
     *
     * Nit de la entidad que consume el servicio
     */
    private String nitEntidad;

    public String getCompania()
    {
        return compania;
    }

    public void setCompania(String compania)
    {
        this.compania = compania;
    }

    public String getCedula()
    {
        return cedula;
    }

    public void setCedula(String cedula)
    {
        this.cedula = cedula;
    }

    public String getNitEntidad()
    {
        return nitEntidad;
    }

    public void setNitEntidad(String nitEntidad)
    {
        this.nitEntidad = nitEntidad;
    }

    public ParametrosEntradaInventario(String compania, String cedula, String nitEntidad)
    {
        super();
        this.compania = compania;
        this.cedula = cedula;
        this.nitEntidad = nitEntidad;
    }

}