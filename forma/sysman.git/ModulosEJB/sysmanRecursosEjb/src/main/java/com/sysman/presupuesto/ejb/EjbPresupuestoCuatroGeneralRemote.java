package com.sysman.presupuesto.ejb;

import javax.ejb.Remote;

import com.sysman.exception.SystemException;

@Remote
public interface EjbPresupuestoCuatroGeneralRemote {
	
	String cargarAuxiliares(
			String tabla, 
			String cadena, 
			String usuario) throws SystemException;

}
