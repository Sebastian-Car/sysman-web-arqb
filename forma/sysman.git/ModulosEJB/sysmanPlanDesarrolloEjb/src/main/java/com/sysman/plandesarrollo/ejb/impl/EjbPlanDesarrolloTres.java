package com.sysman.plandesarrollo.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloTresLocal;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloTresRemote;
import com.sysman.util.SysmanFunciones;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PlanDesarrolloTres
 */
@Stateless
@LocalBean
public class EjbPlanDesarrolloTres implements EjbPlanDesarrolloTresRemote, EjbPlanDesarrolloTresLocal {
	/**
	 * Default constructor.
	 */
	public EjbPlanDesarrolloTres() {
	}

	@Override
	public void prepararInfPlanIndicador(String compania, int vigencia, int vigenciafinalcuatrenio)
			throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ", "UN_VIGENCIAFINALCUATRENIO =>",
				Integer.toString(vigenciafinalcuatrenio), "" };
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO3.PR_PREPARAR_INF_PLAN_INDICADOR", SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void prepararAvanceFisico(String compania, int vigencia, boolean indcuatrenio) throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ", "UN_INDCUATRENIO      =>", (indcuatrenio ? "-1" : "0"), "" };
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO3.PR_PREPARAR_INFAVANCE_FISICO", SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void prepararAvanceFinanciero(String compania, int vigencia, boolean indcuatrenio, String tipoinforme)
			throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ", "UN_INDCUATRENIO      =>", (indcuatrenio ? "-1" : "0"), ", ",
				"UN_TIPOINFORME       =>'", tipoinforme, "'" };
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO3.PR_PREPARAR_INFAV_FINANCIERO", SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void prepararAvanceIndResultados(String compania, int vigencia, boolean indcuatrenio)
			throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ", "UN_INDCUATRENIO      =>", (indcuatrenio ? "-1" : "0"), "" };
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO3.PR_PREPARAR_INFAV_INDRESULT", SysmanFunciones.concatenar(parametros));
	}

}