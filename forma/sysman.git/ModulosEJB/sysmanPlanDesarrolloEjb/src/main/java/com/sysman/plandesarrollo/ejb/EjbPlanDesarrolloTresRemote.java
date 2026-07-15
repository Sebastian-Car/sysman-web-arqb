package com.sysman.plandesarrollo.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbPlanDesarrolloTresRemote {

	void prepararInfPlanIndicador(
			String compania,
			int vigencia,
			int vigenciafinalcuatrenio) throws SystemException;

	void prepararAvanceFisico(String compania, 
			int vigencia, 
			boolean indcuatrenio) throws SystemException;

	void prepararAvanceFinanciero(String compania, 
			int vigencia, 
			boolean indcuatrenio, 
			String tipoinforme)
			throws SystemException;

	void prepararAvanceIndResultados(String compania, 
			int vigencia, 
			boolean indcuatrenio) 
					throws SystemException;
}