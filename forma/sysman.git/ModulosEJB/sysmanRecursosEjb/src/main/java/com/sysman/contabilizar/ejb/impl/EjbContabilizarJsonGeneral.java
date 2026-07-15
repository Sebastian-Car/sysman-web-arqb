/*-
 * EjbContabilizarCeroGeneral.java
 *
 * 1.0
 * 
 * 5/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarJsonGeneralLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarJsonGeneralRemote;
import com.sysman.exception.SystemException;

import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * 
 * 
 * @version 1.0, 18/03/2019
 * @author jgomez
 *
 */
@Stateless
@LocalBean

public class EjbContabilizarJsonGeneral
implements EjbContabilizarJsonGeneralLocal,
EjbContabilizarJsonGeneralRemote {

	/**
	 * Default constructor.
	 */
	public EjbContabilizarJsonGeneral() {
		// constructor sin parametros
	}
	
	@Override
	public int contabilizarJson(
			String json) throws SystemException{
		String[] parametros = { "UN_JSON          =>", json, " "};
		return (int) AccionesImp.ejecutarFuncion(
		        ConectorPool.ESQUEMA_SYSMAN,
		        "PCK_CONTABILIZAR_JSON.FC_CONTABILIZARJSON",
		        SysmanFunciones.concatenar(parametros),
		        Types.INTEGER);
	}
}
