package com.sysman.contratos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.contratos.ejb.EjbContratosGeneralLocal;
import com.sysman.contratos.ejb.EjbContratosGeneralRemote;
import com.sysman.util.SysmanFunciones;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class ContratosGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbContratosGeneral implements EjbContratosGeneralRemote, EjbContratosGeneralLocal {
	/**
	 * Default constructor.
	 */
	public EjbContratosGeneral() {
	}

	@Override
	public void actualizarPlanDeCompras(
			String compania, 
			String claseOrden, 
			long numeroIni, 
			long numeroFin, 
			int modulo,
			int ano, 
			String usuario) throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", 
								"UN_CLASEORDEN        =>'", claseOrden,	"', ",
								"UN_NUMEROINI         =>", Long.toString(numeroIni), ", ",
								"UN_NUMEROFIN         =>", Long.toString(numeroFin), ", ", 
								"UN_MODULO            =>", Integer.toString(modulo), ", ",
								"UN_ANO               =>", Integer.toString(ano), ", ", 
								"UN_USUARIO           =>'", usuario, "'" };
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTRATOS.PR_ACTUALIZARPLANCOMPRAS",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void cambiarConsecutivoContrato(
			String compania, 
			String claseContrato, 
			int anioVigencia, 
			long anteriorConsec,
			long nuevoConsec, 
			String usuario) throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", 
								"UN_CLASECONTRATO     =>'", claseContrato,"', ", 
								"UN_ANIOVIGENCIA      =>", Integer.toString(anioVigencia), ", ", 
								"UN_ANTERIORCONSEC    =>",Long.toString(anteriorConsec), ", ", 
								"UN_NUEVOCONSEC       =>", Long.toString(nuevoConsec), ", ",
								"UN_USUARIO           =>'", usuario, "'" };
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTRATOS.PR_CAMBIARCONSECUTIVO",
				SysmanFunciones.concatenar(parametros));
	}

}