package com.sysman.servpublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosSieteGeneralLocal;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosSieteGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class EjbServPublicosSieteGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbServiciosPublicosSieteGeneral
                implements EjbServiciosPublicosSieteGeneralLocal,
                EjbServiciosPublicosSieteGeneralRemote {

    /**
     * Default constructor.
     */
    public EjbServiciosPublicosSieteGeneral() {
        //
    }

    @Override
    public String calcularFacturacion(
        String compania,
        int intciclo,
        String strcodigoinicial,
        String strcodigofinal,
        boolean enserie,
        boolean finall,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                "UN_INTCICLO  =>", Integer.toString(intciclo),
                                ", ",
                                "UN_STRCODIGOINICIAL  =>'", strcodigoinicial,
                                "', ",
                                "UN_STRCODIGOFINAL=>'", strcodigofinal, "', ",
                                "UN_ENSERIE    =>", enserie ? "-1" : "0", ", ",
                                "UN_FINAL      =>", finall ? "-1" : "0", ", ",
                                "UN_USUARIO    =>'", usuario, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.FC_CALCULOFACTURACION",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void actualizarRangos(
        String compania)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM7.PR_RANGOUSUARIOSCICLO",
                        "UN_COMPANIA          =>'" + compania + "'");
    }

}
