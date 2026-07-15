package com.sysman.servpublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCeroGeneralLocal;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCeroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class EjbServiciosPublicosCeroGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbServiciosPublicosCeroGeneral
                implements EjbServiciosPublicosCeroGeneralRemote,
                EjbServiciosPublicosCeroGeneralLocal {

    /**
     * Default constructor.
     */
    public EjbServiciosPublicosCeroGeneral() {
        //
    }

    @Override
    public String prepararAnoPeriodoSiguiente(
        String compania,
        int ano,
        String periodo,
        String tipoRetorno,
        String frecuencia)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANO               =>", Integer.toString(ano), ", ",
                         "UN_PERIODO           =>'", periodo, "', ",
                         "UN_TIPO_RETORNO      =>'", tipoRetorno, "', ",
                         "UN_FRECUENCIA        =>", (frecuencia != null
                             ? SysmanFunciones.colocarComillas(frecuencia)
                             : null) };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public String prepararCritica(
        String compania,
        int modulo,
        int ciclo,
        String strcodigoini,
        String strcodigofin,
        String consumoMenor,
        int ano,
        int periodo,
        double porcMenor,
        double porcMayor,
        boolean normales,
        boolean manual,
        boolean iguales,
        boolean desviacion,
        String usuario, boolean reporte)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_MODULO            =>", Integer.toString(modulo),
                         ", ",
                         "UN_CICLO             =>", Integer.toString(ciclo),
                         ", ",
                         "UN_STRCODIGOINI      =>'", strcodigoini, "', ",
                         "UN_STRCODIGOFIN      =>'", strcodigofin, "', ",
                         "UN_CONSUMO_MENOR     =>'", consumoMenor, "', ",
                         "UN_ANO               =>", Integer.toString(ano), ", ",
                         "UN_PERIODO           =>", Integer.toString(periodo),
                         ", ",
                         "UN_PORC_MENOR        =>", Double.toString(porcMenor),
                         ", ",
                         "UN_PORC_MAYOR        =>", Double.toString(porcMayor),
                         ", ",
                         "UN_NORMALES          =>", normales ? "-1" : "0",
                         ", ", "UN_MANUAL            =>", manual ? "-1" : "0",
                         ", ", "UN_IGUALES           =>",
                         iguales ? "-1" : "0", ", ",
                         "UN_DESVIACION        =>", desviacion ? "-1" : "0",
                         ", UN_USUARIO => '", usuario, "' ",
                         ",UN_REPORTE => ", reporte ? "-1" : "0" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_PREPARAR_CRITICA",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public boolean generarMicroconsumos(
        String compania,
        int ciclo,
        String strperiodo,
        int intano)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CICLO             =>", Integer.toString(ciclo),
                         ", ", "UN_STRPERIODO        =>'", strperiodo, "', ",
                         "UN_INTANO            =>", Integer.toString(intano),
                         "" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_GENERA_CONSUMOS_MICRO",
                        SysmanFunciones.concatenar(par),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public boolean autorizarMicromedicion(
        String compania,
        String nit)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_NIT               =>'", nit, "'" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_AUTORIZACION_MICROMEDICION",
                        SysmanFunciones.concatenar(par),
                        Types.TINYINT);
        return rta != 0;
    }
}
