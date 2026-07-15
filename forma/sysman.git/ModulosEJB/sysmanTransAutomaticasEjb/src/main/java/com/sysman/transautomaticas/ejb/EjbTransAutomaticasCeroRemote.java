/*-
 * EjbTransAutomaticasCeroRemote.java
 *
 * 1.0
 * 
 * 26 sept. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.transautomaticas.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Remote;

@Remote
public interface EjbTransAutomaticasCeroRemote {

    void validarOrden(
        String compania,
        int anio,
        String tipo,
        String numero,
        String orden)
                    throws SystemException;

    String generarConsecutivoOrden(
        String compania,
        int anio,
        String tipo,
        String numero,
        String tabla) throws SystemException;

    void copiarTransaccionModelo(
        String compania,
        int ano,
        String tipo,
        String numero,
        String usuario)
                    throws SystemException;

    String validarTransaccion(
        String compania,
        int ano,
        String tipo,
        String numeroModelo,
        BigInteger numero,
        String usuario) throws SystemException;

    String modificarTransaccionesRete(
        String compania,
        int ano,
        String tipo,
        String numeroModelo,
        BigInteger numero,
        String usuario)
                    throws SystemException;

    String calcularTransaccion(
        String compania,
        int ano,
        String tipo,
        String numeroModelo,
        BigInteger numero,
        String usuario) throws SystemException;

    void controlarTransaccion(
        String compania,
        int ano,
        String tipo,
        String numeroModelo,
        BigInteger numero)
                    throws SystemException;
    
    void guardarAfectacion(
        String compania,
        int anio,
        String tipo,
        String numeroModelo,
        BigInteger numero,
        String detalles,
        String usuario)
     throws SystemException;
    
}