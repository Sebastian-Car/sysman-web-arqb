/*-
 * EjbContabilizarDosGeneralLocal.java
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

import javax.ejb.Local;

/**
 * 
 * @version 1.0, 25/07/2018
 * @author ybecerra
 *
 */
@Local
public interface EjbContabilizarNominaDosGeneralLocal
{
    void revisarCuentasPlancontable(
        String companiaNomina,
        String companiaDs,
        int ano,
        int mes,
        int periodo,
        int proceso,
        String usuario)
                    throws SystemException;
}
