/*-
 * EjbAuditoriaLocal.java
 *
 * 1.0
 * 
 * 13/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

/**
 * 
 * @version 1.0, 13/06/2018
 * @author ybecerra
 *
 */
@Local
public interface EjbAuditoriaLocal
{
    String generarTrigger(
        String compania,
        String tablas)
                    throws SystemException;
    
    void actTablasAuditoria(
            String compania)
                        throws SystemException;

}
