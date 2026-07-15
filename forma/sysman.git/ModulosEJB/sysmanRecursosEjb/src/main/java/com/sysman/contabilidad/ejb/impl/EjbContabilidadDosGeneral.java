package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadDosGeneralLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadDosGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class EjbContabilidadDosGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbContabilidadDosGeneral
                implements EjbContabilidadDosGeneralRemote,
                EjbContabilidadDosGeneralLocal {

    /**
     * Default constructor.
     */
    public EjbContabilidadDosGeneral() {
        //
    }

    @Override
    public String crearCompaniaNiifLotes(
        String compania,
        String companiaNiif)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_COMPANIA_NIIF     =>'", companiaNiif, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_CREARCOMPANIA_NIIF",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String contabilizarComprobantesContablesNiif(
        String compania,
        int mesInicial,
        int mesFinal,
        String tipoInicial,
        String tipoFinal,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MES_INICIAL       =>",
                                Integer.toString(mesInicial), ", ",
                                "UN_MES_FINAL         =>",
                                Integer.toString(mesFinal), ", ",
                                "UN_TIPO_INICIAL      =>'", tipoInicial, "', ",
                                "UN_TIPO_FINAL        =>'", tipoFinal, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD2.FC_NIIF_LOTES",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

}
