/*-
 * EjbContabilizarNominaTresRemote.java
 *
 * 1.0
 * 
 * 25 jun. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 25 jun. 2019
 * @author eamaya
 *
 */
public interface EjbContabilizarNominaTresRemote {

    String contabilizarNominaHBucarama(String companianomina, int proceso,
        int ano, int mes, int periodo, String usuario) throws SystemException;

}
