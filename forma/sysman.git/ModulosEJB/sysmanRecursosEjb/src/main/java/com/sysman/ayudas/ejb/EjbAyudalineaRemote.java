/*-
 * EjbAyudalineaRemote.java
 *
 * 1.0
 * 
 * 29/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */     

package com.sysman.ayudas.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

/**
  * TODO Ingrese una descripcion para la clase.
  * 
  * @version 1.0, 29/06/2023
  * @author grojas
  *
  */
@Remote
public interface EjbAyudalineaRemote
{
    
    String cargarTareas(
        int proceso, 
        String cadenaplan, 
        String usuario) throws SystemException; 
    

}
