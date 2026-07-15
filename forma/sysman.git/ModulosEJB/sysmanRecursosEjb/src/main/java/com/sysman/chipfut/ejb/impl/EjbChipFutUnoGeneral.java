package com.sysman.chipfut.ejb.impl;

import com.sysman.chipfut.ejb.EjbChipFutUnoGeneralLocal;
import com.sysman.chipfut.ejb.EjbChipFutUnoGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class ChipFutUnoGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbChipFutUnoGeneral implements EjbChipFutUnoGeneralRemote,
                EjbChipFutUnoGeneralLocal {
    /**
     * Default constructor.
     */
    public EjbChipFutUnoGeneral() {
    }

    @Override
    public void subirSeguimientoReciprocas(
        String compania,
        String cambios,
        String usuario,
        String consecutivo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CAMBIOS           =>", cambios, ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CONSECUTIVO       =>'",
                                consecutivo, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CHIPFUT1.PR_LOADFILE_SEGU_RECIPROCAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void copiarAuxiliar(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO_DESTINO       =>",
                                Integer.toString(anoDestino), ", ",
                                "UN_ANO_ORIGEN        =>",
                                Integer.toString(anoOrigen), ", ",
                                "UN_COMPANIA_DESTINO  =>'", companiaDestino, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.PR_COPIAR_AUXILIAR",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void copiarFuenteRecurso(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO_DESTINO       =>",
                                Integer.toString(anoDestino), ", ",
                                "UN_ANO_ORIGEN        =>",
                                Integer.toString(anoOrigen), ", ",
                                "UN_COMPANIA_DESTINO  =>'", companiaDestino, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.PR_COPIAR_FUENTE_RECURSO",
                        SysmanFunciones.concatenar(parametros));
    }

}