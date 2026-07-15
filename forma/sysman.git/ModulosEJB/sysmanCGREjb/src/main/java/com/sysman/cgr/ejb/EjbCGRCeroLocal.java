/*-
 * EjbCGRCeroLocal.java
 *
 * 1.0
 * 
 * 17/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Local;

@Local
public interface EjbCGRCeroLocal {

    String generarConfiguracion(String compania, int anio)
                    throws SystemException;

    String generarPlanoEjecucionGastosRegalias(
        String compania,
        int ano,
        int trimestre,
        String codigoContaduria,
        boolean anticipos,
        String codigoSgr,
        boolean excel) throws SystemException;

    String generarPlanoEjecucionIngresosRegalias(String compania, int ano,
        int trimestre, String codigoContaduria, String codigoSgr, boolean excel)
                    throws SystemException;

    String generarPlanoProgramacionGastosRegalias(
        String compania,
        int ano,
        int trimestre,
        String codigoContaduria,
        String codigoSgr,
        boolean excel) throws SystemException;

    String generarPlanoProgramacionIngresosRegalias(String compania, int ano,
        int trimestre, String codigoContaduria, String codigoSgr, boolean excel)
                    throws SystemException;

    String generarPlanoProgramacionGastos(String compania, int ano,
        int trimestre, String codigoContaduria, boolean excel)
                    throws SystemException;

    String consultarCodigoSChip(String compania) throws SystemException;

    BigDecimal actualizarConfiguracionPptal(String compania, int anio,
        String usuario) throws SystemException;

    String validaConfPptal(
        String cadena,
        String compania,
        int anio,
        int entidad,
        String regalias,
        String naturaleza,
        String usuario)
                    throws SystemException;

    String generarPlanoProgramacionIngresos(
        String compania,
        int ano,
        int trimestre,
        String codigocontaduria,
        boolean excel)
                    throws SystemException;

    String generarPlanoEjecucionIngresos(
        String compania,
        int ano,
        int trimestre,
        String codigoentidad,
        String codigocontaduria,
        boolean excel) throws SystemException;

    String generarPlanoEjecucionGastos(
        String compania,
        int ano,
        int trimestre,
        String codigoentidad,
        String codigocontaduria,
        boolean anticipos,
        boolean excel) throws SystemException;

    void actualizarCodigoCCEPT(String compania, String usuario)
                    throws SystemException;

    void actCamposCuipo(String compania,
        int anio,
        String campo,
        String usuario)
                    throws SystemException;

    void actualizarCamposCuipoAfect(String compania, String usuario)
                    throws SystemException;

	void cargarTipoRecurso(
			String compania, 
			String cadena, 
			String usuario) throws SystemException;
	
	boolean actualizarClasificadoresPptalCuipo(
	            String compania,
	            int ano,
	            String usuario)
	                        throws SystemException;

	void cargarTipoClasificador(String compania, String cadenaplan, String usuario) throws SystemException;
	
	String actclaarbol(
	    		String compania, 
	    		String cadenaplan, 
	    		String usuario) throws SystemException; 
	
    String actualizarTipoClasificador(
	    	    		String compania, String cadenaplan, String usuario) throws SystemException;
    
    void cargarTablaTempActualizaciones(String compania, String ano, String codigo)
			throws SystemException;
    
    public void actualizarTipoClasificadoresAnuevos(String compania, String ano, String codigo);

    String actualizarTipoClasificadorRubro(
    				String compania,int ano, String naturaleza,  String cadenaplan, String usuario) throws SystemException;
}