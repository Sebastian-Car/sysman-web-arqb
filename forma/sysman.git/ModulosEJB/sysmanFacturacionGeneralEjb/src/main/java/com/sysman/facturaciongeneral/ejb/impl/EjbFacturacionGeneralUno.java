/*-
 * EjbFacturacionGeneralUno.java
 *
 * 1.0
 * 
 * 1/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralUnoLocal;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralUnoRemote;
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
 * Session Bean implementation class FacturacionGeneralUno
 */
@Stateless
@LocalBean
public class EjbFacturacionGeneralUno implements EjbFacturacionGeneralUnoRemote,
                EjbFacturacionGeneralUnoLocal {
    /**
     * Default constructor.
     */
    public EjbFacturacionGeneralUno() {
    }

    @Override
    public void cargarConceptos(
        String compania,
        int anio,
        String tipocobro,
        String codigoCobro,
        int grupoConceptos,
        Date fechaSolicitud,
        int cantidadGrupo,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIO              =>",
                                    Integer.toString(anio), ", ",
                                    "UN_TIPOCOBRO         =>'", tipocobro,
                                    "', ", "UN_CODIGO_COBRO      =>'",
                                    codigoCobro, "', ",
                                    "UN_GRUPO_CONCEPTOS   =>",
                                    Integer.toString(grupoConceptos), ", ",
                                    "UN_FECHA_SOLICITUD   =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaSolicitud),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CANTIDAD_GRUPO    =>",
                                    Integer.toString(cantidadGrupo), ", ",
                                    "UN_USUARIO           =>'", usuario, "' "
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_FACT_GENERAL_COM1.PR_CARGAR_CONCEPTOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void ejecutarAnulacionFacturas(
        String compania,
        String tipocobro,
        int anio,
        BigInteger facturainicial,
        BigInteger facturafinal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCOBRO         =>'", tipocobro, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_FACTURAINICIAL    =>",
                                facturainicial.toString(), ", ",
                                "UN_FACTURAFINAL      =>",
                                facturafinal.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM1.PR_EJECUTARANULACION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal cargarValorConceptoCant(
        double valor,
        int cantidad,
        int digito)
                    throws SystemException {
        String[] parametros = { "UN_VALOR             =>",
                                Double.toString(valor), ", ",
                                "UN_CANTIDAD          =>",
                                Integer.toString(cantidad), ", ",
                                "UN_DIGITO            =>",
                                Integer.toString(digito), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR_CANT",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal cargarValorConceptoIndica(
        boolean indicador,
        BigDecimal valor,
        int digito)
                    throws SystemException {
        String[] parametros = { "UN_INDICADOR         =>",
                                indicador ? "-1" : "0", ", ",
                                "UN_VALOR             =>", valor.toString(),
                                ", ",
                                "UN_DIGITO            =>",
                                Integer.toString(digito), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_INDICA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }
    
	@Override
	public String cambiarFechaFactura(
			String compania, 
			String anio, 
			String tipoFactura, 
			String solicitud,
			String FacturaInicial,
			String FacturaFinal,
			Date fecha, 
			String usuario) throws SystemException {
		try {
			String[] parametro = { "UN_COMPANIA  =>'", compania,
					"' ", ", UN_ANIO     =>", anio,
					", UN_TIPO     => '", tipoFactura,
					"' ", ", UN_SOLICITUD     =>", solicitud,
					" ", ", UN_FACT_INICIAL     =>", FacturaInicial,
					" ", ", UN_FACT_FINAL     =>", FacturaFinal,
					", UN_FECHA    =>TO_DATE('",SysmanFunciones.convertirAFechaCadena(fecha),"' ,'DD/MM/YYYY'), ",
                    "  UN_USUARIO  =>'",usuario ,"' "};
			
			return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_FACT_GENERAL_COM1.FC_CAMBIARFECHAFACTURA", SysmanFunciones.concatenar(parametro), Types.VARCHAR);
		
		}
	    catch (ParseException e) {
	        throw new SystemException(e);
	    }
	}

}