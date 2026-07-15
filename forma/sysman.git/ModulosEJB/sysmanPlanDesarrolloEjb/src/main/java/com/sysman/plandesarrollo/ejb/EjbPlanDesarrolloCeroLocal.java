/*-
 * EjbPlanDesarrolloCeroLocal.java
 *
 * 1.0
 * 
 * 26/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo.ejb;


import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Local;

@Local
public interface EjbPlanDesarrolloCeroLocal
{

    long cargarNivel(String compania,
        int vigencia,
        String codigo)
                    throws SystemException;

    int obtenerDigitosMetaProduccion()
                    throws SystemException;

    int obtenerDigitosMetaResultado()
                    throws SystemException;

    int obtenerDigitosAccion()
                    throws SystemException;

    void cuadrarSaldos(
        String compania,
        int vigencia) throws SystemException;

    long generarTransaccion(
        String compania,
        String tipo,
        int vigencia,
        String dependencia,
        String responsable,
        String sucursal,
        String usuario)
                    throws SystemException;

    String prepararInfPlanAccion(
        String compania,
        int vigenciaConsultaI,
        int vigenciaConsultaF) throws SystemException;

    String cargarInformacionPresupuestal(
        String compania,
        BigInteger constante,
        int vigenciaGuber,
        int vigenciaPres,
        String nombreDependencia,
        String usuario,
        String datosExcel) throws SystemException;

    boolean generarPredecesor(
        String compania,
        int vigencia,
        String usuario)
                    throws SystemException;

    void mayorizarCompPagMetas(
        String compania,
        int vigencia,
        int digNivel,
        String tipot,
        BigInteger numerot,
        String usuario)
                    throws SystemException;

    void mayorizarCompPagPlan(
        String compania,
        int vigencia,
        int digNivel,
        String tipot,
        BigInteger numerot,
        String usuario)
                    throws SystemException;
    
    public boolean obtenerManejaDependencia(
        String digitos) throws SystemException;

    void guardarPoblacionBenef(
			int valorLgtb, 
			int valorDiscap, 
			int valorAfro, 
			int valorIndig, 
			int valorInfancia,
			int valorTotalCond, 
			String usuario, 
			String compania, 
			String tipo, 
			int numero, 
			String idPlan,
			String vigenciaInicial, 
			int valorPriminfancia, 
			int valorAdolescencia, 
			int valorJuventud, 
			int valorAdulto,
			int valorAdultoMayor, 
			int valorTotal, 
			int valorMujeres, 
			int valorHombres, 
			int valorTotgenero, 
			int valorVca)
					throws SystemException;

}
