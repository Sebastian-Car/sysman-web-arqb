package com.sysman.presupuesto.ejb;
import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Remote;
@Remote
public interface EjbPresupuestoCierreRemote {

	String cierrePresupuestalCb(
			String compania, 
			int anoCierre, 
			String usuario,
			boolean cierreNormal,
			boolean cierrePasivo, 
			boolean cierreVigFuturas, 
			boolean cierreVigFutPasivo, 
			boolean cierreRegalias,
			Date fechaCierre,
	        boolean cierreCofinanciados)
			throws SystemException;

	String validarCierrePlan(
			String compania, 
			int anoCierre, 
			boolean cierreNormal, 
			boolean cierrePasivo,
			boolean cierreVigFuturas, 
			boolean cierreVigFutPasivo, 
			boolean cierreRegalias,
	        boolean cierreCofinanciados) throws SystemException;
	

	String eliminarCierrePlan(
			String compania, 
			int anoCierre, 
			String usuario, 
			boolean cierreNormal,
			boolean cierrePasivo, 
			boolean cierreVigFuturas, 
			boolean cierreVigFutPasivo, 
			boolean cierreRegalias,
			boolean cierreCofinanciados)
			throws SystemException;
	
	void crearTipoDefectoCierre(
            String compania, 
            String usuario)
            throws SystemException;
	
	String validarReversarCierre(
			String compania, 
			int anoCierre, 
			boolean cierreNormal, 
			boolean cierrePasivo,
			boolean cierreVigFuturas, 
			boolean cierreVigFutPasivo, 
			boolean cierreRegalias,
	        boolean cierreCofinanciados) throws SystemException;
	

}