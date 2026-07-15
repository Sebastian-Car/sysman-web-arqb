/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose Tools | Templates and open the template in the editor.
 */
package com.sysman.logica;

import java.io.Serializable;

/**
 *
 * @author cmanrique
 */
public class ResponsableAso implements Serializable
{

    private String responsable;
    private String sucursal;
    private String nombre;
    private String cargo;
    private boolean jefeUnidad;
    private boolean activoRecep;

    public ResponsableAso(String responsable, String sucursal, String nombre, String cargo, boolean jefeUnidad,
                    boolean activoRecep)
    {
        this.responsable = responsable;
        this.sucursal = sucursal;
        this.nombre = nombre;
        this.cargo = cargo;
        this.jefeUnidad = jefeUnidad;
        this.activoRecep = activoRecep;
    }

    public String getResponsable()
    {
        return responsable;
    }

    public void setResponsable(String responsable)
    {
        this.responsable = responsable;
    }

    public String getSucursal()
    {
        return sucursal;
    }

    public void setSucursal(String sucursal)
    {
        this.sucursal = sucursal;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getCargo()
    {
        return cargo;
    }

    public void setCargo(String cargo)
    {
        this.cargo = cargo;
    }

    public boolean isJefeUnidad()
    {
        return jefeUnidad;
    }

    public void setJefeUnidad(boolean jefeUnidad)
    {
        this.jefeUnidad = jefeUnidad;
    }

    public boolean isActivoRecep()
    {
        return activoRecep;
    }

    public void setActivoRecep(boolean activoRecep)
    {
        this.activoRecep = activoRecep;
    }

}