/*-
 * EjbFacturacionGeneralDos.java
 *
 * 1.0
 * 
 * 7/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralDosLocal;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralDosRemote;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class FacturacionGeneralDos
 */
@Stateless
@LocalBean
public class EjbFacturacionGeneralDos implements EjbFacturacionGeneralDosRemote,
                EjbFacturacionGeneralDosLocal {
    /**
     * Default constructor.
     */
    public EjbFacturacionGeneralDos() {
        // Nothing to do
    }

    @Override
    public Date calcularFechaVencimiento(
        String compania,
        String tipoCobro,
        boolean aplicaRel,
        String codigoCobro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCOBRO         =>'", tipoCobro, "', ",
                                "UN_APLICA_REL        =>",
                                aplicaRel ? "-1" : "0", ", ",
                                "UN_CODIGO_COBRO      =>'", codigoCobro, "'"
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM2.FC_CALCULAR_FECHA_VENCIMIENTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public void validarConcInteresesFin(
        String compania,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania,
                                "' , UN_ANO => ", Integer.toString(ano), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM2.PR_VALIDAR_CONC_INTERESES_FIN",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String verificarFactura(
        String compania,
        String tipoFactura,
        long noFactura,
        BigDecimal tasa,
        int cuotas,
        BigDecimal efectivo,
        String tipoCobro,
        int ano,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOFACTURA       =>'", tipoFactura, "', ",
                                "UN_NOFACTURA         =>",
                                Long.toString(noFactura), ", ",
                                "UN_TASA              =>", tasa.toString(),
                                ", ", "UN_CUOTAS            =>",
                                Integer.toString(cuotas), ", ",
                                "UN_EFECTIVO          =>", efectivo.toString(),
                                ", ", "UN_TIPOCOBRO         =>'", tipoCobro,
                                "', ", "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM2.FC_VERFICARFACTURA",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void facturarConceptos(
        String compania,
        String tipoCobro,
        long codigoCobro,
        long nroFactura,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCOBRO         =>'", tipoCobro, "', ",
                                "UN_CODIGO_COBRO      =>",
                                Long.toString(codigoCobro), ", ",
                                "UN_NRO_FACTURA       =>",
                                Long.toString(nroFactura), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM2.PR_FACTURAR_CONCEPTOS",
                        SysmanFunciones.concatenar(parametros));
    }

    /**
     * Por favor al parametro cadena no agregarle las comillas
     * sencillas porque este parametro ya trae las comillas
     * concatenadas
     */
    @Override
    public String cargarArchivoAsobancaria(
        String compania,
        String banco,
        String usuario,
        String cadena)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_BANCO             =>'", banco, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CADENA            =>", cadena, ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM2.FC_CARGARARCHIVOASOBANCARIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal hallarConceptoFormula(
        String compania,
        String formula,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_FORMULA           =>'", formula, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM2.FC_HALLAR_CONCEP_FORM",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal retornarValorAnual(
        String compania,
        int anio,
        String campo,
        boolean salarioMen)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_CAMPO             =>'", campo, "', ",
                                "UN_SALARIOMEN        =>",
                                salarioMen ? "-1" : "0", ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM2.FC_RETORNAR_VLR_FORM",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void calculoFacturacionCorabastos(
        String compania,
        int anofacturar,
        int mesfacturar,
        String tipofactura,
        Date fechafacturacion,
        Date fechalimite,
        Date fechalimite1,
        String cci,
        String ccf,
        String descripcion,
        String strusuario,
        long facturaciontotal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANOFACTURAR       =>",
                                    String.valueOf(anofacturar), ", ",
                                    "UN_MESFACTURAR       =>",
                                    String.valueOf(mesfacturar), ", ",
                                    "UN_TIPOFACTURA       =>'", tipofactura,
                                    "', ", "UN_FECHAFACTURACION  =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafacturacion),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHALIMITE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechalimite),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHALIMITE1      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechalimite1),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CCI               =>'", cci, "', ",
                                    "UN_CCF               =>'", ccf, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_STRUSUARIO        =>'",
                                    strusuario, "', ",
                                    "UN_FACTURACIONTOTAL  =>",
                                    Long.toString(facturaciontotal)
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM2.PR_CALCULO_FACTURACION_ABASTOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
	@Override
	public Date calcularVencimiento(
			String compania, 
			String tercero, 
			String fechaSolicitud) 
						throws SystemException {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", 
									"UN_TERCERO         =>'",tercero, "', ", 
									"UN_FECHALSOLICITUD      =>'",fechaSolicitud, "'"
			};
			return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_FACT_GENERAL_COM2.FC_CALCULAR_VENCIMIENTO",
					SysmanFunciones.concatenar(parametros),
					Types.DATE);
		}
	public void calculoFacturacionContratos(String compania,
	        int anofacturar,
	        int mesfacturar,
	        String tipofactura,
	        Date fechafacturacion,
	        Date fechalimite,
	        Date fechalimite1,
	        String cci,
	        String ccf,
	        String descripcion,
	        String strusuario,
	        long facturaciontotal,
	        String checkDescuento,
	        Date fechaLimiteDescuento) throws SystemException {
		try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANOFACTURAR       =>",
                                    String.valueOf(anofacturar), ", ",
                                    "UN_MESFACTURAR       =>",
                                    String.valueOf(mesfacturar), ", ",
                                    "UN_TIPOFACTURA       =>'", tipofactura,
                                    "', ", "UN_FECHAFACTURACION  =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafacturacion),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHALIMITE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechalimite),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHALIMITE1      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechalimite1),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CCI               =>'", cci, "', ",
                                    "UN_CCF               =>'", ccf, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_STRUSUARIO        =>'",
                                    strusuario, "', ",
                                    "UN_FACTURACIONTOTAL  =>",
                                    Long.toString(facturaciontotal),", ",
                                    "UN_CHECKDESCUENTO  =>'",
                                    checkDescuento, "', ",
                                    "UN_FECHALIMITEDSCTO   =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                    		fechaLimiteDescuento),
                                    "','DD/MM/YYYY')"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM2.PR_CALC_FACT_CONTR",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
		
	}
	
	@Override
	public void calculoFacturacionSinContrato(String compania,
	        int anofacturar,
	        int mesfacturar,
	        String tipofactura,
	        Date fechafacturacion,
	        Date fechalimite,
	        Date fechalimite1,
	        String ubicacionInicial,
	        String ubicacionFinal,
	        String terceroInicial,
	        String terceroFinal,
	        String inmuebleInicial,
	        String inmuebleFinal,
	        String descripcion,
	        String strusuario,
	        long facturaciontotal) throws SystemException {
		try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANOFACTURAR       =>",
                                    String.valueOf(anofacturar), ", ",
                                    "UN_MESFACTURAR       =>",
                                    String.valueOf(mesfacturar), ", ",
                                    "UN_TIPOFACTURA       =>'", tipofactura,
                                    "', ", "UN_FECHAFACTURACION  =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafacturacion),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHALIMITE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechalimite),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHALIMITE1      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechalimite1),
                                    "','DD/MM/YYYY'), ",
                                    "UN_UBICACIONINI  =>'", ubicacionInicial, "', ",
                                    "UN_UBICACIONFIN  =>'", ubicacionFinal, "', ",
                                    "UN_TERCEROINI  =>'", terceroInicial, "', ",
                                    "UN_TERCEROFIN  =>'", terceroFinal, "', ",
                                    "UN_INMUEBLEINI  =>'", inmuebleInicial, "', ",
                                    "UN_INMUEBLEFIN  =>'", inmuebleFinal, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_STRUSUARIO        =>'",
                                    strusuario, "', ",
                                    "UN_FACTURACIONTOTAL  =>",
                                    Long.toString(facturaciontotal)
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM2.PR_CALCULO_FACT_SINCONTRATO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
		
	}
}