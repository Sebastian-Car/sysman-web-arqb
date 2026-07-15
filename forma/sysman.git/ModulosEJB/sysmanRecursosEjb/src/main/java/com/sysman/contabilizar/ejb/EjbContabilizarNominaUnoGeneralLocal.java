/*-
 * EjbContabilizarNominaUnoGeneralLocal.java
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
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 25/07/2018
 * @author ybecerra
 *
 */
@Local
public interface EjbContabilizarNominaUnoGeneralLocal
{
    String contabilizarRetenciones(
        String companiaDs,
        String companiaNomina,
        int proceso,
        int ano,
        int mes,
        String centroCostoUnico,
        Date fechaInter,
        String usuario) throws SystemException;

    String contabilizarCuentasporpagar(
        String companiaNomina,
        String companiaDs,
        int proceso,
        int ano,
        int mes,
        Date fechaInter,
        String usuario) throws SystemException;
}
