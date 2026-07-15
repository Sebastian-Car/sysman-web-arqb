/*-
 * EjbServiciosPublicosAbonosLocal.java
 *
 * 1.0
 * 
 * 11/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbServiciosPublicosAbonosLocal {

    void actualizarRangos(String compania) throws SystemException;

}