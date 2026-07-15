/*-
 * EjbContabilizarCeroLocal.java
 *
 * 1.0
 * 
 * 4/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 4/01/2018
 * @author ybecerra
 *
 */
@Remote
public interface EjbContabilizarFacGenLocal {
    String enviarConceptosSinConfiguracion(
        String compania,
        int anio)
                    throws SystemException;
}
