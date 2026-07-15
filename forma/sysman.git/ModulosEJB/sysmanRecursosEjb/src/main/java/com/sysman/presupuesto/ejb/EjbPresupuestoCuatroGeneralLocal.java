package com.sysman.presupuesto.ejb;

import javax.ejb.Local;

import com.sysman.exception.SystemException;


@Local
public interface EjbPresupuestoCuatroGeneralLocal {

	String cargarAuxiliares(
			String tabla, 
			String cadena, 
			String usuario) throws SystemException;

}
