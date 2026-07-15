package com.sysman.contratos.ejb;

import com.sysman.exception.SystemException;
import javax.ejb.Local;

@Local
public interface EjbContratosGeneralLocal {

	void actualizarPlanDeCompras(
			String compania, 
			String claseOrden, 
			long numeroIni, 
			long numeroFin, 
			int modulo,
			int ano, 
			String usuario) throws SystemException;

	void cambiarConsecutivoContrato(
			String compania, 
			String claseContrato, 
			int anioVigencia, 
			long anteriorConsec,
			long nuevoConsec, String usuario) throws SystemException;
}