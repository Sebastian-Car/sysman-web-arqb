/*-
 * EjbContabilizarNominaGeneralLocal.java
 *
 * 1.0
 * 
 * 25/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Local;

/**
 * 
 * @version 1.0, 25/07/2018
 * @author ybecerra
 *
 */
@Local
public interface EjbContabilizarNominaGeneralLocal
{
    String contabilizarNomina(
        String compania,
        String companiaNomina,
        int ano,
        int mes,
        int periodo,
        int proceso,
        Date fechaInter,
        String tipoComprobante,
        boolean compporEmpleado,
        boolean compporTercero,
        boolean compporCentroCosto,
        String empleado,
        boolean patronoT,
        boolean empleadoT,
        boolean provisionesT,
        String centroCostoUnico,
        String usuario) throws SystemException;
}
