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
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroLocal;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
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
public class EjbFacturacionGeneralCero
                implements EjbFacturacionGeneralCeroRemote,
                EjbFacturacionGeneralCeroLocal {
    /**
     * Default constructor.
     */
    public EjbFacturacionGeneralCero() {
    }

    @Override
    public boolean validarConceptoAfectaInventario(
        String compania,
        int ano,
        String tipocobro,
        String concepto)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPOCOBRO         =>'", tipocobro, "', ",
                                "UN_CONCEPTO          =>'", concepto, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL.FC_CONCEPTOAFINVENTARIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
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
    public long afectarInventario(
        String compania,
        String tipoasociado,
        BigInteger numeroasociado,
        String tipomovimiento,
        Date fechamov,
        String dependencia,
        String tipocobro,
        String bodegaorigen,
        String bodegadestino,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOASOCIADO      =>'", tipoasociado,
                                    "', ", "UN_NUMEROASOCIADO    =>",
                                    numeroasociado.toString(), ", ",
                                    "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                    "', ", "UN_FECHAMOV          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechamov),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DEPENDENCIA       =>'", dependencia,
                                    "', ", "UN_TIPOCOBRO         =>'",
                                    tipocobro, "', ",
                                    "UN_BODEGAORIGEN      =>'", bodegaorigen,
                                    "', ", "UN_BODEGADESTINO     =>'",
                                    bodegadestino, "', ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return (long) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL.FC_AFECTARINVENTARIO",
                            SysmanFunciones.concatenar(parametros),
                            Types.BIGINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void eliminarRecaudo(
        String compania,
        String tipofactura,
        BigInteger numeroFactura,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOFACTURA       =>'", tipofactura, "', ",
                                "UN_NUMEROFACTURA     =>",
                                numeroFactura.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL.PR_ELIMINAR_RECAUDO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void reversarFacturacion(
        String compania,
        int anio,
        String tipoCobro,
        BigInteger factura,
        BigInteger codCobro,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPOCOBRO         =>'", tipoCobro, "', ",
                                "UN_FACTURA           =>", factura.toString(),
                                ", ", "UN_CODCOBRO          =>",
                                codCobro.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL.PR_REVERSARFACTURACION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String consultarAbono(
        String compania,
        String tipoAbono,
        BigInteger abono)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOABONO         =>'", tipoAbono, "', ",
                                "UN_ABONO             =>", abono.toString()
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL.FC_CONSULTARABONO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void actualizarPagosAbono(
        String compania,
        String tipoabono,
        BigInteger abono,
        String tipofactura,
        BigInteger nofactura,
        BigInteger codigocobro,
        Date fechapago,
        String tipocptpago,
        BigInteger cptepago,
        String bancopago,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOABONO         =>'", tipoabono, "', ",
                                "UN_ABONO             =>", abono.toString(),
                                ", ", "UN_TIPOFACTURA       =>'", tipofactura,
                                "', ", "UN_NOFACTURA         =>",
                                nofactura.toString(), ", ",
                                "UN_CODIGOCOBRO       =>",
                                codigocobro.toString(), ", ",
                                "UN_FECHAPAGO         =>",
                                SysmanFunciones.formatearFecha(fechapago), ", ",
                                "UN_TIPOCPTPAGO       =>'", tipocptpago, "', ",
                                "UN_CPTEPAGO          =>", cptepago.toString(),
                                ", ", "UN_BANCOPAGO         =>'", bancopago,
                                "', ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL.PR_ACTPAGOSABONOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public long insertarElementoConcepto(
        String compania,
        int anio,
        String tipocobro,
        String elemento,
        double porcUtilidad,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPOCOBRO         =>'", tipocobro, "', ",
                                "UN_ELEMENTO          =>'", elemento, "', ",
                                "UN_PORC_UTILIDAD     =>",
                                Double.toString(porcUtilidad), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL.FC_ACT_VALOR_ELEMENTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public void facturarEnSerie(
        String compania,
        int anio,
        String tipocobro,
        BigInteger contratoinicial,
        BigInteger contratofinal,
        String lugar,
        Date fechafactura,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPOCOBRO         =>'", tipocobro, "', ",
                                "UN_CONTRATOINICIAL   =>",
                                contratoinicial.toString(), ", ",
                                "UN_CONTRATOFINAL     =>",
                                contratofinal.toString(), ", ",
                                "UN_LUGAR             =>'", lugar, "', ",
                                "UN_FECHAFACTURA      =>",
                                SysmanFunciones.formatearFecha(fechafactura),
                                ", ", "UN_USUARIO           =>'", usuario, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL.PR_FACTURAR_SERIE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void financiarPredial(
        String compania,
        int anio,
        String tipocobro,
        String codigopredio,
        String aniosfinanciar,
        String tercero,
        String sucursal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPOCOBRO         =>'", tipocobro, "', ",
                                "UN_CODIGOPREDIO      =>'", codigopredio, "', ",
                                "UN_ANIOSFINANCIAR    =>'", aniosfinanciar,
                                "', ", "UN_TERCERO           =>'", tercero,
                                "', ", "UN_SUCURSAL          =>'", sucursal,
                                "', ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL.PR_FINANCIARPREDIAL",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public  BigDecimal     retonarTasaAnual(
		String compania, 
		int anio, 
		String tipocobro, 
		Date fechacorte) 
		                      throws SystemException {
		         String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
		                 , "UN_ANIO              =>" , Integer.toString(anio) , ", "
		                 , "UN_TIPOCOBRO         =>'" , tipocobro , "', "
		                 , "UN_FECHACORTE        =>" , SysmanFunciones.formatearFecha(fechacorte) , 
		};
		         return (BigDecimal) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL.FC_TASAANUAL",
		        		 SysmanFunciones.concatenar(parametros),
		                Types.DECIMAL);
		    }
    
    @Override
    public  BigDecimal     retonarTasaDiaria(
		String compania, 
		int anio, 
		String tipocobro, 
		Date fechacorte) 
		                      throws SystemException {
		         String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
		                 , "UN_ANIO              =>" , Integer.toString(anio) , ", "
		                 , "UN_TIPOCOBRO         =>'" , tipocobro , "', "
		                 , "UN_FECHACORTE        =>" , SysmanFunciones.formatearFecha(fechacorte) , 
		};
		         return (BigDecimal) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL.FC_TASADIARIA",
		        		 SysmanFunciones.concatenar(parametros),
		                Types.DECIMAL);
		    }


}
