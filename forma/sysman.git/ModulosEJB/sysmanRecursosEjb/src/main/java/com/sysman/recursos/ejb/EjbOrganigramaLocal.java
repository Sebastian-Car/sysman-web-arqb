/*-
 * EjbOrganigramaLocal.java
 *
 * 1.0
 * 
 * 2 jul. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 2 jul. 2019
 * @author eamaya
 *
 */
public interface EjbOrganigramaLocal {

    String jsonOrganigramaBmanga(String compania) throws SystemException;

    String jsonOrgBmangaRecu(String compania, String codigopadre)
                    throws SystemException;

}
