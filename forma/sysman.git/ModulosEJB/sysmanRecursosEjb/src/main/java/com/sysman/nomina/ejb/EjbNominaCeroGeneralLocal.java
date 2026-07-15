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

import java.util.Date;

import javax.ejb.Local;

/**
 * 
 * 
 * @version 1.0, 19/01/2018
 * @author ybecerra
 *
 */
@Local
public interface EjbNominaCeroGeneralLocal {

    Date getFechaPeriodoIniFin(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        boolean fechainicio,
        boolean total) throws SystemException;

    String getDatoEmpresa(
        String compania,
        int par) throws SystemException;

    boolean validarPeriodoActivoNomina(String compania, int proceso, int anio,
        int mes, int periodo) throws SystemException;

}
