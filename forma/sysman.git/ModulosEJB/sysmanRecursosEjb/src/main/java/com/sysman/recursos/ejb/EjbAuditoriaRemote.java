/*-
 * EjbAuditoriaRemote.java
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

import javax.ejb.Remote;

/**
 * 
 * @version 1.0, 13/06/2018
 * @author ybecerra
 *
 */
@Remote
public interface EjbAuditoriaRemote
{
    String generarTrigger(
        String compania,
        String tablas)
                    throws SystemException;
    
    void actTablasAuditoria(
            String compania)
                        throws SystemException;

}
