package com.sysman.mantenimientoactivos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Local;

@Local
public interface EjbMantenimientoActivosCeroLocal {

    boolean getExistenAccesorios(
        String compania,
        String placa,
        long serie)
                    throws SystemException;

    void insertarParteFuncional(
        String compania,
        String placa,
        String usuario)
                    throws SystemException;

    boolean actualizarSolicitudMantenimiento(
        String compania,
        int ano,
        String tipo,
        long solicitud,
        long numero,
        String usuario)
                    throws SystemException;

    BigInteger enumerarMantenimiento(
        String compania,
        int anio,
        String tipo,
        int numero)
                    throws SystemException;

    void mantEstacion(
        String compania,
        int ano,
        String tipoCpte,
        long comprobante,
        String tipo,
        int componente,
        String observacion,
        String tarea,
        String usuario) throws SystemException;
}