/*-
 * EjbNominaCeroGeneralLocal.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

/**
 * 
 * 
 * @version 1.0, 19/01/2018
 * @author ybecerra
 *
 */
@Local
public interface EjbNominaNueveGeneralLocal {
    void actIndCuneEmpleado(
        String compania,
        boolean opcion)
                    throws SystemException;
    
void updateCptoPeriodoNomina(
    		String compania,
            int proceso,
            int anio,
            int mes,
            int periodo)
                    throws SystemException;
    
void deleteCptoPeriodoNomina(
    		String compania,
            int proceso,
            int anio,
            int mes,
            int periodo,
            int concepto,
            int empleado)
                    throws SystemException;

void verificarcalculofondosolidaridad(
		String compania,
		int periodo,
        int proceso,
        int anio,
        int mes,
        String usuario)
                throws SystemException;

void verificarcarencargospersonalhistorico(String compania, String anio, int empleado) throws SystemException;

void revisarCalculoParafiscales(
		String compania, 
		int periodo, 
		int proceso, 
		int anio, 
		int mes, 
		String usuario)
				throws SystemException;

}
