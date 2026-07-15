/*-
 * EjbAuditoriaCuentasMedicasCero.java
 *
 * 1.0
 * 
 * 18/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.auditoriacuentasmedicas.ejb.impl;

import com.sysman.auditoriacuentasmedicas.ejb.EjbAuditoriaCuentasMedicasCeroLocal;
import com.sysman.auditoriacuentasmedicas.ejb.EjbAuditoriaCuentasMedicasCeroRemote;
import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import co.com.sysman.acciones.Acciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class AuditoriaCuentasMedicasCero
 */
@Stateless
@LocalBean
public class EjbAuditoriaCuentasMedicasCero
implements EjbAuditoriaCuentasMedicasCeroRemote,
EjbAuditoriaCuentasMedicasCeroLocal {
    /**
     * Default constructor.
     */
    public EjbAuditoriaCuentasMedicasCero() {
    }

    @Override
    public void cargarRips(
        String compania,
        int consecutivo,
        String cadenarip,
        String tiporip,
        String usuario)
                        throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONSECUTIVO          =>", Integer.toString(consecutivo), ", ",
                                "UN_CADENARIP         =>", cadenarip, ", ",
                                "UN_TIPORIP           =>'", tiporip, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_AUDITORIACUENTASMEDICAS.PR_CARGAR_RIPS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void eliminarRips(
        String compania, 
        int consecutivo)
                        throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONSECUTIVO          =>", Integer.toString(consecutivo)

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_AUDITORIACUENTASMEDICAS.PR_ELIMINAR_RIPS",
                        SysmanFunciones.concatenar(parametros));

    }
    
    
    @Override
    public String causacionCuentasMedicas(
        String compania, 
        String factura, 
        String codigoPrestadorServicio,
        String tipoComprobante,
        int ano,
        Date fecha,
        String codigoInterfaz,
        String usuario,
        String radicado,
        int consecutivo,
        int agrupado)
                        throws SystemException {
        try {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_FACTURA           =>'", factura, "', ",
                                "UN_CODIGO_PRESTADOR  =>'", codigoPrestadorServicio, "', ",
                                "UN_TIPO_COMPROBANTE  =>'", tipoComprobante, "', ",
                                "UN_ANO          =>", Integer.toString(ano), ", ",
                                "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(fecha),
                                    "','DD/MM/YYYY'), ",
                                "UN_CODIGO_INTERFASE  =>'", codigoInterfaz, "', ",
                                "UN_USUARIO           =>'", usuario, "',",
                                "UN_RADICADO          =>'", radicado, "',",
                                "UN_CONSECUTIVORIP    =>", Integer.toString(consecutivo), ", ",
                                "UN_AGRUPADO          =>", Integer.toString(agrupado), ""
                                
                               
        };
        return (String) AccionesImp.ejecutarFuncion
                        (ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_AUDITORIACUENTASMEDICAS.FC_CAUSACION_CUENTAS_MEDICAS",
                        SysmanFunciones.concatenar(parametros), Types.VARCHAR);

        
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public void generarProximoAnio(String codigoTransaccion,
        int ano, int anoDesde, String usuario)
                        throws SystemException {
        String[] parametros = { "UN_COD_TRASACCION           =>'", codigoTransaccion, "',",
                                "UN_ANO_DESDE          =>", Integer.toString(anoDesde), ", ",
                                "UN_ANO          =>", Integer.toString(ano), ", ",
                                "UN_USUARIO           =>'", usuario, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_AUDITORIACUENTASMEDICAS.PR_GENERAR_PROXIMO_ANIO",
                        SysmanFunciones.concatenar(parametros));

    }

    @Override
    public void cargarRipsJson(
            String compania,
            int consecutivo,
            String clobUsuarios,
            String clobConsultas,
            String clobProcedimientos,
            String clobMedicamentos,
            String clobUrgencias,
            String clobRecienNacidos,
            String clobOtrosServicios,
            String clobHospitalizacion,
            String clobValFactura,
            String clobValFacturaDetalle,
            String clobArchivos,
            String clobTransaccion,
            String usuario) throws SystemException {
        
        String[] parametros = {
            "UN_COMPANIA              =>'", compania, "', ",
            "UN_CONSECUTIVO          =>", Integer.toString(consecutivo), " , " ,
            "UN_CLOB_USUARIOS        =>'", clobUsuarios, "', ",
            "UN_CLOB_CONSULTAS       =>'", clobConsultas, "', ",
            "UN_CLOB_PROCEDIMIENTOS  =>'", clobProcedimientos, "', ",
            "UN_CLOB_MEDICAMENTOS    =>'", clobMedicamentos, "', ",
            "UN_CLOB_URGENCIAS       =>'", clobUrgencias, "', ",
            "UN_CLOB_RECIEN_NACIDOS   =>'", clobRecienNacidos, "', ",
            "UN_CLOB_OTROS_SERVICIOS  =>'", clobOtrosServicios, "', ",
            "UN_CLOB_HOSPITALIZACION  =>'", clobHospitalizacion, "', ",
            "UN_CLOB_VAL_FACTURA      =>'", clobValFactura , "',",
            "UN_CLOB_VAL_FACTDETALLE  =>'", clobValFacturaDetalle ,"',",
            "UN_CLOB_ARCHIVOS_CONTROL =>'", clobArchivos ,"',",
            "UN_CLOB_TRANSACCION      =>'", clobTransaccion ,"',",
            "UN_USUARIO              =>'", usuario, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                "PCK_AUDITORIACUENTASMEDICAS.PR_CARGAR_RIPS_JSON",
                SysmanFunciones.concatenar(parametros));
    }
    

    @Override
    public String informeFAC120_plano(
        String compania,
        Date fechaInicial,
        Date fechaFinal,
        String claseCuentaInicial,
        String claseCuentaFinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA              =>'", compania,        "', ",
                                    "UN_FECHAINICIAL          =>TO_DATE('",
                                            SysmanFunciones.convertirAFechaCadena(fechaInicial),
                                            "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL            =>TO_DATE('",
		                                    SysmanFunciones.convertirAFechaCadena(fechaFinal),
		                                    "','DD/MM/YYYY'), ",
                                    "UN_CLASECUENTAINICIAL    =>",   claseCuentaInicial, ", ",
                                    "UN_CLASECUENTAFINAL      =>",   claseCuentaFinal
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_AUDITORIACUENTASMEDICAS.FC_INFFAC120_PLANO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        } catch (SQLException | IOException | ParseException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public String informeFT_033_plano(
        String compania,
        Date fechaInicial,
        Date fechaFinal,
        String claseCuentaInicial,
        String claseCuentaFinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA              =>'", compania,        "', ",
                                    "UN_FECHAINICIAL          =>TO_DATE('",
                                            SysmanFunciones.convertirAFechaCadena(fechaInicial),
                                            "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL            =>TO_DATE('",
		                                    SysmanFunciones.convertirAFechaCadena(fechaFinal),
		                                    "','DD/MM/YYYY'), ",
                                    "UN_CLASECUENTAINICIAL    =>",   claseCuentaInicial, ", ",
                                    "UN_CLASECUENTAFINAL      =>",   claseCuentaFinal
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_AUDITORIACUENTASMEDICAS.FC_INF_FT033_PLANO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        } catch (SQLException | IOException | ParseException e) {
            throw new SystemException(e);
        }
    }


}