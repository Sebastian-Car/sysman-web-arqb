/*-
 * EjbContabilizarCeroGeneralLocal.java
 *
 * 1.0
 * 
 * 5/02/2018
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
 * 
 * @version 1.0, 18/03/2019
 * @author jgomez
 */
@Local
public interface EjbContabilizarJsonGeneralLocal {

      int contabilizarJson(
            String json) throws SystemException;	
}
