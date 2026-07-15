/*-
 * EjbFacturacionGeneralCero.java
 *
 * 1.0
 * 
 * 10/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralLocal;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralRemote;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class FacturacionGeneralCero
 */
@Stateless
@LocalBean
public class EjbFacturacionGeneral
implements EjbFacturacionGeneralRemote,
EjbFacturacionGeneralLocal {
	/**
	 * Default constructor.
	 */
	public EjbFacturacionGeneral() {
	}

	@Override
	public boolean validarManejaInventario(
			String compania,
			int ano,
			String tipocobro)
					throws SystemException {
		byte salida;
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TIPOCOBRO         =>'", tipocobro, "'"
		};
		salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_FACT_GENERAL.FC_MANEJAINVENTARIO",
				SysmanFunciones.concatenar(parametros),
				Types.TINYINT);
		return salida == 0 ? false : true;
	}

	@Override
	public String cargarRecaudoCausacion(
			String compania,
			String cadenaplano,
			String tipocobro,
			int anio,
			String tercero,
			String usuario,
			int loteNum)
					throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_CADENAPLANO       =>", cadenaplano,
					", ",
					"UN_TIPOCOBRO         =>'", tipocobro,
					"', ",
					"UN_ANIO              =>",
					Integer.toString(anio), ", ",
					"UN_TERCERO           =>'", tercero, "', ",
					"UN_USUARIO           =>'", usuario, "',",
					"UN_LOTE           =>", Integer.toString(loteNum), ""
			};

			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_FACT_GENERAL.FC_CARGAR_RECUADOCAUSACION",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String recaudarFactura(
			String compania,
			String tipoFactura,
			BigInteger numeroFactura,
			String observacion,
			String cuenta,
			Date fecha,
			int anio,
			boolean diferida,
			String usuario)
					throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_TIPOFACTURA       =>'", tipoFactura,
					"', ",
					"UN_NUMEROFACTURA     =>",
					numeroFactura.toString(), ", ",
					"UN_OBSERVACION       =>'", observacion,
					"', ", "UN_CUENTA            =>'", cuenta,
					"', ", "UN_FECHA             =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							fecha),
					"','DD/MM/YYYY'), ",
"UN_ANIO              =>",
Integer.toString(anio), ", ",

"UN_DIFERIDA          =>",
diferida ? "-1" : "0", ", ",
		"UN_USUARIO           =>'", usuario, "'"
			};
			return (String) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_FACT_GENERAL_COM3.FC_RECAUDARFACTURA",
					SysmanFunciones.concatenar(parametros),
					Types.VARCHAR);
		}
		catch (ParseException e) {
			throw new SystemException(e);
		}
	}


	@Override
	public boolean interfazarFactura(
			String compania,
			String tipofactura,
			BigInteger nofactura,
			Date fechapago,
			boolean vermensaje,
			boolean manejainventario,
			String usuario)
					throws SystemException {
		byte salida;
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_TIPOFACTURA       =>'", tipofactura,
					"', ",
					"UN_NOFACTURA         =>",
					nofactura.toString(), ", ",
					"UN_FECHAPAGO         =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							fechapago),
					"','DD/MM/YYYY'), ",
"UN_VERMENSAJE        =>",
vermensaje ? "-1" : "0", ", ",
		"UN_MANEJAINVENTARIO  =>",
		manejainventario ? "-1" : "0", ", ",
				"UN_USUARIO           =>'", usuario, "'"
			};
			salida = (byte) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_FACT_GENERAL_COM3.FC_INTERFAZAR_FACTURA",
					SysmanFunciones.concatenar(parametros),
					Types.TINYINT);
		}
		catch (ParseException e) {
			throw new SystemException(e);
		}
		return salida == 0 ? false : true;
	}

	
	@Override
	public String  facturarConceptosLiquida(
			String compania,
	        int ano,
		    String tipoFactura,
		    String tercero,
		    String concepto,
		    String descripcion,	
	    	String usuario)
					throws SystemException {
	  String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
		        "UN_ANIO              =>",Integer.toString(ano), ", ",
		        "UN_TIPOFACTURA       =>'", tipoFactura, "', ",
		        "UN_TERCERO           =>'", tercero, "', ",
		        "UN_CONCEPTO          =>'", concepto, "', ",
		        "UN_DESCRIPCION       =>'", descripcion, "', ",
		        "UN_USUARIO           =>'", usuario, "'"
		};
		return (String) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_FACT_GENERAL.FC_FACTURAR_CONCEPTOSLIQUIDA",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
		
	}
	
	
	
	
	
	
}
