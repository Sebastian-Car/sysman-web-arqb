/*-
 * EjbPlanDesarrolloDosRemote.java
 *
 * 1.0
 * 
 * 11/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.ejb.Remote;

@Remote
public interface EjbPlanDesarrolloDosRemote {

    boolean verificarPlanAdquisiciones(
        String compania,
        int ano,
        String valor)
                    throws SystemException;

    void eliminarPlanIndicativo(
        String compania,
        int ano,
        String tipo,
        Long numero,
        String usuario) throws SystemException;

    boolean validarEliminacionPlanIndicativo(
        String compania,
        int vigenciainicial,
        String tipo,
        BigInteger numero,
        String idplan,
        BigInteger digitosaccion) throws SystemException;

    void verificarSaldoDisponible(
        String compania,
        String id,
        String idPlan,
        int vigenciaPlan,
        int vigenciaMeta,
        int vigenciaInicial,
        String tipo,
        String numero,
        String fuente,
        BigDecimal valorfuente) throws SystemException;

    boolean mayorizarMetasyFuentes(
        String compania,
        int vigencia,
        String tipo,
        String numero,
        String usuario) throws SystemException;

    void actualizarPresupuestoPlanAccion(
        String compania,
        int anio,
        String usuario) throws SystemException;
    
    void actualizarPresupuestoPlanDesarrollo(
    	String compania,
        int anio,
        String usuario,
        int mes) throws SystemException;
}
