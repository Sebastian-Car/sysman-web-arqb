package com.sysman.facturaciongeneral.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralTresLocal;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralTresRemote;
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
 * Session Bean implementation class FacturacioGeneralTres
 */
@Stateless
@LocalBean

public class EjbFacturacionGeneralTres
                implements EjbFacturacionGeneralTresRemote,
                EjbFacturacionGeneralTresLocal {
    /**
     * Default constructor.
     */
    public EjbFacturacionGeneralTres() {
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
    public boolean manejarDerConexion(
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
                        "PCK_FACT_GENERAL_COM3.FC_MANEJADERCONEXION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean manejarInterfazaCntDerConexion(
        String compania,
        String tipo,
        BigInteger numero,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
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
                                    "UN_TERCERO           =>'", tercero,
                                    "', ", "UN_SUCURSAL          =>'", sucursal,
                                    "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM3.FC_INTERFAZCNTDERCONEXION",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean manejarInterfazContable(
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
                                    "', ",
                                    "UN_MANEJAINVENTARIO  =>",
                                    manejainventario ? "-1" : "0", ", ",
                                    "UN_USUARIO           =>'", usuario,
                                    "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM3.FC_INTERFAZCONTABLE",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public BigDecimal extraerSalarioMinimo(
        String compania,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM3.FC_SALARIOMINIMO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal reemplazarFormula(
        String formula)
                    throws SystemException {
        String[] parametros = { "UN_FORMULA           =>'", formula, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM3.FC_REEMPLAZARFORMULA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean verificarDescuentoCobro(
        String compania,
        String tipofactura,
        BigInteger numerofactura)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOFACTURA       =>'", tipofactura, "', ",
                                "UN_NUMEROFACTURA     =>",
                                numerofactura.toString()
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM3.FC_VERIFICARDESCCOBRO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
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
        String usuario,
        BigDecimal valorRecaudo)
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
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_VALORRECAUDO      =>", valorRecaudo.toString(), ""
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
    public void actualizarTotalFacturaxTRM(
            String compania,
            String tipoFactura,
            BigInteger numeroFactura,
            BigDecimal valorFactura,
            BigDecimal valorTRM,
            int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'" , compania ,"',",
		                        "UN_TIPOCOBRO       =>'", tipoFactura,"', ",
		                        "UN_NUMERO_FACTURA     =>", numeroFactura.toString(), ", ",
		                        "UN_VALORFACTURA      =>", valorFactura.toString(),", ",
		                        "UN_VALORTRM          =>", valorTRM.toString(),", " ,
		                        "UN_ANIO              =>", Integer.toString(anio), ""
		};
        
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                "PCK_FACT_GENERAL_COM3.PR_ACTUALIZARVLRTOTALFACXTRM",
                SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void actualizarSaldoActualConcepto(
            String compania,
            String tipoFactura,
            String concepto,
            BigInteger numeroContrato,
            BigDecimal saldoActual)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   		=>'" , compania ,"',",
		                        "UN_TIPO      		=>'", tipoFactura,"', ",
		                        "UN_CONCEPTO   		=>'", concepto,"', ",
		                        "UN_NUMERO_CONTRATO =>", numeroContrato.toString(), ", ",
		                        "UN_SALDO_ACTUAL    =>", saldoActual.toString(),""
		};
        
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                "PCK_FACT_GENERAL_COM3.PR_ACTUALIZARSALDOCONCEPTO",
                SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public BigDecimal obtenerSaldoConcepto(
    		String compania,
            String tipoFactura,
            String concepto,
            BigInteger numeroContrato)
                    throws SystemException {
    	String[] parametros = { "UN_COMPANIA   		=>'" , compania ,"',",
                				"UN_TIPO      		=>'", tipoFactura,"', ",
                				"UN_CONCEPTO   		=>'", concepto,"', ",
                				"UN_NUMERO_CONTRATO =>", numeroContrato.toString(), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM3.FC_OBTENERSALDOCONCEPTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }
    
    @Override
    public BigDecimal obtenerValorNetoAnterior(
    		String compania,
    		int anio,
            String tipoCobro,
            String concepto,
            BigInteger codigoCobro)
                    throws SystemException {
    	String[] parametros = { "UN_COMPANIA   		=>'" , compania ,"',",
    							"UN_ANIO            =>", Integer.toString(anio), ",",
                				"UN_TIPO      		=>'", tipoCobro,"', ",
                				"UN_CONCEPTO   		=>'", concepto,"', ",
                				"UN_CODIGO_COBRO =>", codigoCobro.toString(), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM3.FC_OBTENERVALORNETOANT",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

	@Override
	public boolean eliminarDetallesContratoParametrizado(String compania, String tipo, String numero) throws SystemException {
		byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO       =>'", tipo, "', ",
                                "UN_NUMERO     =>",
                                numero.toString()
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM3.FC_DEL_DESTALLESCONTRATO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
	}

}