package com.sysman.contabilidad.ejb.impl;

import java.math.BigInteger;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.sysman.contabilidad.ejb.EjbContabilidadCpteGeneralLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadCpteGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

/**
 * Session Bean implementation class ContabilidadCpte
 * 
 * @version 2.0 asana Refactorización de concatenados.
 */
@Stateless
@LocalBean
public class EjbContabilidadCpteGeneral
implements EjbContabilidadCpteGeneralRemote, EjbContabilidadCpteGeneralLocal {
	/**
	 * Default constructor.
	 */
	public EjbContabilidadCpteGeneral() {
		// No tiene sentencias
	}

	@Override
	public BigInteger enumerarComprobanteCnt(
			String compania,
			int anio,
			String tipo,

			BigInteger numero,
			String centroCosto)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANIO              =>",
				Integer.toString(anio), ", ",
				"UN_TIPO              =>'", tipo, "', ",
				"UN_NUMERO            =>", numero.toString(),
				", ", "UN_CENTRO_COSTO      =>'", centroCosto,
				"'"
		};
		return new BigInteger((String) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR));
	}


}
