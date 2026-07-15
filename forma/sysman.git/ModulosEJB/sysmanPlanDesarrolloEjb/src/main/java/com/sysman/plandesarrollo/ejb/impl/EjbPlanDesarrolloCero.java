/*-
 * EjbPlanDesarrolloCero.java
 *
 * 1.0
 * 
 * 26/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroGeneralRemote;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroLocal;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PlanDesarrollo
 */
@Stateless
@LocalBean
public class EjbPlanDesarrolloCero
implements EjbPlanDesarrolloCeroLocal,
EjbPlanDesarrolloCeroRemote {

    @EJB
    private EjbPlanDesarrolloCeroGeneralRemote ejbPlanDesarrolloCeroGeneral;

    /**
     * Default constructor.
     */
    public EjbPlanDesarrolloCero() {
    }

    @Override
    public long cargarNivel(
        String compania,
        int vigencia,
        String codigo)
                    throws SystemException {
        return ejbPlanDesarrolloCeroGeneral.cargarNivel(compania, vigencia,
                        codigo);

    }

    @Override
    public int obtenerDigitosMetaProduccion()
                    throws SystemException {

        return ejbPlanDesarrolloCeroGeneral.obtenerDigitosMetaProduccion();

    }

    @Override
    public int obtenerDigitosMetaResultado()
                    throws SystemException {

        return ejbPlanDesarrolloCeroGeneral.obtenerDigitosMetaResultado();

    }

    @Override
    public int obtenerDigitosAccion()
                    throws SystemException {

        return ejbPlanDesarrolloCeroGeneral.obtenerDigitosAccion();

	}

    @Override
    public void cuadrarSaldos(
        String compania,
        int vigencia)
                    throws SystemException {

        ejbPlanDesarrolloCeroGeneral.cuadrarSaldos(compania, vigencia);

	}

	@Override
	public long generarTransaccion(
			String compania,
			String tipo,
			int vigencia,
			String dependencia,
			String responsable,
			String sucursal,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_TIPO              =>'", tipo, "', ",
				"UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ",
				"UN_DEPENDENCIA       =>'", dependencia, "', ",
				"UN_RESPONSABLE       =>'", responsable, "', ",
				"UN_SUCURSAL          =>'", sucursal, "', ",

				"UN_USUARIO           =>'", usuario, "'"
		};
		return (long) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO.FC_GENERARTRANSACCION",
				SysmanFunciones.concatenar(parametros),
				Types.BIGINT);
	}

    @Override
    public String prepararInfPlanAccion(
        String compania,
        int vigenciaConsultaI,
        int vigenciaConsultaF)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA_CONSULTA_I =>'",
                                Integer.toString(vigenciaConsultaI),
                                "', ", "UN_VIGENCIA_CONSULTA_F =>'",
                                Integer.toString(vigenciaConsultaF), "'"
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PLAN_DESARROLLO.FC_PREPARARINFPLANACCION",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

	@Override
	public String cargarInformacionPresupuestal(
			String compania,
			BigInteger constante,
			int vigenciaGuber,
			int vigenciaPres,
			String nombreDependencia,
			String usuario,
			String datosExcel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_CONSTANTE         =>", constante.toString(),
				", ", "UN_VIGENCIAGUBER     =>",
				Integer.toString(vigenciaGuber), ", ",
				"UN_VIGENCIAPRES      =>",
				Integer.toString(vigenciaPres), ", ",
				"UN_NOMBREDEPENDENCIA =>'", nombreDependencia,
				"', ", "UN_USUARIO           =>'", usuario,
				"', ", "UN_DATOSEXCEL        =>'", datosExcel,
				"'"
		};
		try {
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_PLAN_DESARROLLO.FC_CARGARINFOPRESUPUESTAL",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public boolean generarPredecesor(
			String compania,
			int vigencia,
			String usuario)
					throws SystemException {
		byte salida;
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO.FC_GENERA_PREDECESOR",
				SysmanFunciones.concatenar(parametros),
				Types.TINYINT);
		return salida == 0 ? false : true;
	}

	@Override
	public void mayorizarCompPagMetas(
			String compania,
			int vigencia,
			int digNivel,
			String tipot,
			BigInteger numerot,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ",
				"UN_DIGNIVEL          =>",
				Integer.toString(digNivel), ", ",
				"UN_TIPOT             =>'", tipot, "', ",
				"UN_NUMEROT           =>", numerot.toString(),
				", ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO.PR_MAYORIZA_COMP_PAG_METAS",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void mayorizarCompPagPlan(
			String compania,
			int vigencia,
			int digNivel,
			String tipot,
			BigInteger numerot,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ",
				"UN_DIGNIVEL          =>",
				Integer.toString(digNivel), ", ",
				"UN_TIPOT             =>'", tipot, "', ",
				"UN_NUMEROT           =>", numerot.toString(),
				", ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO.PR_MAYORIZA_COMP_PAG_PLAN",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public boolean obtenerManejaDependencia(
			String digitos)
					throws SystemException {
		byte salida;
		String[] parametros = { "UN_DIGITOS          =>'", digitos, "'"
		};
		salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PLAN_DESARROLLO.FC_GETM_DEPENDENCIA",
				SysmanFunciones.concatenar(parametros),
				Types.TINYINT);
		return salida == 0 ? false : true;
	}

	

	@Override
	public  void     guardarPoblacionBenef (
			int valorLgtb, 
			int valorDiscap, 
			int valorAfro, 
			int valorIndig, 
			int valorInfancia, 
			int valorTotalCond, 
			String usuario, 
			String compania, 
			String tipo, 
			int numero, 
			String idPlan, 
			String vigenciaInicial, 
			int valorPriminfancia, 
			int valorAdolescencia, 
			int valorJuventud, 
			int valorAdulto, 
			int valorAdultoMayor, 
			int valorTotal, 
			int valorMujeres, 
			int valorHombres, 
			int valorTotgenero, 
			int valorVca) 
					throws SystemException {
		String[] parametros ={                   "UN_VALOR_LGTB        =>" , Integer.toString(valorLgtb) , ", "
				, "UN_VALOR_DISCAP       =>" , Integer.toString(valorDiscap) , ", "
				, "UN_VALOR_AFRO         =>" , Integer.toString(valorAfro) , ", "
				, "UN_VALOR_INDIG        =>" , Integer.toString(valorIndig) , ", "
				, "UN_VALOR_INFANCIA     =>" , Integer.toString(valorInfancia) , ", "
				, "UN_VALOR_TOTAL_COND   =>" , Integer.toString(valorTotalCond) , ", "
				, "UN_USUARIO            =>'" , usuario , "', "
				, "UN_COMPANIA           =>'" , compania , "', "
				, "UN_TIPO               =>'" , tipo , "', "
				, "UN_NUMERO             =>" , Integer.toString(numero) , ", "
				, "UN_ID_PLAN            =>'" , idPlan , "', "
				, "UN_VIGENCIA_INICIAL   =>'" , vigenciaInicial , "', "
				, "UN_VALOR_PRIMINFANCIA =>" , Integer.toString(valorPriminfancia) , ", "
				, "UN_VALOR_ADOLESCENCIA =>" , Integer.toString(valorAdolescencia) , ", "
				, "UN_VALOR_JUVENTUD     =>" , Integer.toString(valorJuventud) , ", "
				, "UN_VALOR_ADULTO       =>" , Integer.toString(valorAdulto) , ", "
				, "UN_VALOR_ADULTO_MAYOR =>" , Integer.toString(valorAdultoMayor) , ", "
				, "UN_VALOR_TOTAL       =>" , Integer.toString(valorTotal) , ", "
				, "UN_VALOR_MUJERES     =>" , Integer.toString(valorMujeres) , ", "
				, "UN_VALOR_HOMBRES     =>" , Integer.toString(valorHombres) , ", "
				, "UN_VALOR_TOTGENERO   =>" , Integer.toString(valorTotgenero) , ", "
				, "UN_VALOR_VCA         =>" , Integer.toString(valorVca) , ""
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_PLAN_DESARROLLO.PR_GUARDAR_POBLACION_BENEF",
				SysmanFunciones.concatenar(parametros));
	}


}