/*-
 * EjbFacturacionGeneralCuatro.java
 *
 * 1.0
 * 
 * 11 oct. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroLocal;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
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
 * Session Bean implementation class FacturacionGeneralCuatro
 */
@Stateless
@LocalBean

public class EjbFacturacionGeneralCuatro
                implements EjbFacturacionGeneralCuatroRemote,
                EjbFacturacionGeneralCuatroLocal {
    /**
     * Default constructor.
     */
    public EjbFacturacionGeneralCuatro() {
    }

    @Override
    public boolean manejarInterfazContableNoFacturado(
        String compania,
        String tipo,
        BigInteger numero,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
        boolean manejainventario,
        String usuario)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_MANEJAINVENTARIO  =>",
                                    (manejainventario ? "-1" : "0"), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM4.FC_INTERFAZCONTABLENOFACT",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public void registrarMovimiento(
        String compania,
        String tipomov,
        long nromov,
        Date fecha,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOMOV           =>'", tipomov, "', ",
                                    "UN_NROMOV            =>",
                                    Long.toString(nromov), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM4.PR_REGISTRAMOVIMIENTO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void eliminarFacturaDiferida(
        String compania,
        String tipoabono,
        BigInteger abono,
        BigInteger numerofactura,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOABONO         =>'", tipoabono, "', ",
                                "UN_ABONO             =>", abono.toString(),
                                ", ", "UN_NUMEROFACTURA     =>",
                                numerofactura.toString(), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM4.PR_ELIMINARFACTDIFERIDA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String manejarInterfazContableAbono(
        String compania,
        String tipocobro,
        BigInteger numero,
        String tipoabono,
        BigInteger numeroabono,
        int cuotabono,
        Date fecha,
        String tercero,
        String sucursal,
        boolean manejainventario,
        String cuenta,
        BigDecimal vlrabono,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOCOBRO         =>'", tipocobro,
                                    "', ", "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_TIPOABONO         =>'", tipoabono,
                                    "', ", "UN_NUMEROABONO       =>",
                                    numeroabono.toString(), ", ",
                                    "UN_CUOTABONO         =>",
                                    Integer.toString(cuotabono), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_MANEJAINVENTARIO  =>",
                                    (manejainventario ? "-1" : "0"), ", ",
                                    "UN_CUENTA            =>'", cuenta, "', ",
                                    "UN_VLRABONO          =>",
                                    vlrabono.toString(), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM4.FC_INTERFAZCONTABLEABONO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
	public void cargarTarifasConceptos(String compania, String cadenaplan, String usuario)
			throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                				"UN_CADENAPLAN        =>", cadenaplan, ", ",
                				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_FACT_GENERAL_COM4.PR_CARGAR_TARIFASCONCEPTOS",
				SysmanFunciones.concatenar(parametros));
		
	}
    
    @Override
    public  String    devolverFacturas(
    		String compania, 
    		int anio, 
    		BigInteger factura, 
    		String tipocobro, 
    		BigInteger numerocobro, 
    		boolean facturado, 
    		boolean recaudo, 
    		String cuenta, 
    		Date fecha, 
    		String usuario) 
    				throws SystemException {
    	try {
    		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
    				, "UN_ANIO              =>" , Integer.toString(anio) , ", "
    				, "UN_FACTURA           =>" ,factura.toString() ,", "
    				, "UN_TIPOCOBRO         =>'" , tipocobro , "', "
    				, "UN_NUMEROCOBRO       =>" ,numerocobro.toString() ,", "
    				, "UN_FACTURADO         =>" , (facturado?"-1":"0")  , ", "
    				, "UN_RECAUDO           =>" , (recaudo?"-1":"0")  , ", "
    				, "UN_CUENTA            =>'" , cuenta , "', "
    				, "UN_FECHA             =>TO_DATE('",
    				SysmanFunciones.convertirAFechaCadena(fecha),"','DD/MM/YYYY'), "
    				, "UN_USUARIO           =>'" , usuario , "'"
    		};
    		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL_COM4.FC_DEVOLVER_FACTURAS",
    				SysmanFunciones.concatenar(parametros),
    				Types.VARCHAR);

    	}
    	catch (ParseException e) {
    		throw new SystemException(e);
    	}
    }
    
    
    @Override
    public String actualizarTablaFacturas(
    	   String facturas
    	) throws SystemException {

        return (String) AccionesImp.ejecutarFuncionConClob(
		                ConectorPool.ESQUEMA_SYSMAN,
		                "PCK_FACT_GENERAL_COM4.FC_ACT_FACTURAS_FRIDA",
		                facturas,
		                Types.VARCHAR);
    }
    
    @Override
    public BigDecimal consecutivoobj(
            String compania,
            int anio,
            String codigo) throws SystemException {

        String[] parametros = {
                "UN_COMPANIA =>'", compania, "', ",
                "UN_ANO     =>", Integer.toString(anio), ", ",
                "UN_CODIGO   =>'", codigo, "'"
        };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                ConectorPool.ESQUEMA_SYSMAN,
                "PCK_FACT_GENERAL_COM4.FC_CONSECUTIVOOBJ",
                SysmanFunciones.concatenar(parametros),
                Types.NUMERIC);
    }
    
    @Override
    public  String cargarCobros(
    		String compania, 
			int anio,
			String tipoCobro,
			String cadena,
			Date fechaComprobante,
			Date fechaVencimiento,
			String usuario) 
    				throws SystemException {
    	try {
    	String[] parametros ={ "UN_COMPANIA          =>'" , compania , "', "
							  ,"UN_ANIO              =>"  , Integer.toString(anio) , ", "
				              ,"UN_TIPOCOBRO         =>'" , tipoCobro , "', "
				              ,"UN_CADENA	         =>"  , cadena ,", "
				              ,"UN_FECHACOMP         =>TO_DATE('",
                              SysmanFunciones.convertirAFechaCadena(fechaComprobante),"','DD/MM/YYYY'), "
                              ,"UN_FECHAVEN          =>TO_DATE('",
                              SysmanFunciones.convertirAFechaCadena(fechaVencimiento),"','DD/MM/YYYY'), "
				              ,"UN_USUARIO           =>'" , usuario , "'"
		};
			return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL_COM4.FC_SUBIR_COBROS",
					SysmanFunciones.concatenar(parametros),
					Types.VARCHAR);
    	}
    	catch (ParseException e) {
    		throw new SystemException(e);
    	}
    }
    
    @Override
	public void factutacionLote(String compania, 
			int anio,
			String tipoCobro,
			String cobroInicial,
			String cobroFinal,
            String usuario)
			throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
								"UN_ANIO              =>"  , Integer.toString(anio) , ", ",
								"UN_TIPOCOBRO         =>'" , tipoCobro , "', ",
								"UN_COBRO_INI         =>'" , cobroInicial , "', ",
								"UN_COBRO_FIN         =>'" , cobroFinal , "', ",
                				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_FACT_GENERAL_COM4.PR_FACTURACIONLOTE",
				SysmanFunciones.concatenar(parametros));
		
	}
 


}