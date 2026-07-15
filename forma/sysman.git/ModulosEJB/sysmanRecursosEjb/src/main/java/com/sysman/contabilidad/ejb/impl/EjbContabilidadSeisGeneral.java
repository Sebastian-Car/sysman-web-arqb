package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadSeisGeneralLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisGeneralRemote;
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
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class EjbContabilidadSeisGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbContabilidadSeisGeneral
                implements EjbContabilidadSeisGeneralRemote,
                EjbContabilidadSeisGeneralLocal {

    @Override

    public String generarRtaConciliadosXPlano(
        String compania,
        String cuenta,
        String cadena,
        boolean eliminarpartidas,
        boolean maningpartidasconc,
        boolean validadoc,
        int colvalingreso,
        int colvalcredito,
        int coldocumento,
        int colerror,
        int colfecha,
        Date fecha,
        String formatofecha,
        int filaini,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_CUENTA            =>'", cuenta, "', ",
                             "UN_CADENA            =>", cadena, ", ",
                             "UN_ELIMINARPARTIDAS  =>",
                             (eliminarpartidas ? "-1" : "0"), ", ",
                             "UN_MANINGPARTIDASCON =>",
                             (maningpartidasconc ? "-1" : "0"), ", ",
                             "UN_VALIDADOC         =>",
                             (validadoc ? "-1" : "0"),
                             ", ",
                             "UN_COLVALINGRESO     =>",
                             Integer.toString(colvalingreso), ", ",
                             "UN_COLVALCREDITO     =>",
                             Integer.toString(colvalcredito), ", ",
                             "UN_COLDOCUMENTO      =>",
                             Integer.toString(coldocumento), ", ",
                             "UN_COLERROR          =>",
                             Integer.toString(colerror),
                             ", ",
                             "UN_COLFECHA          =>",
                             Integer.toString(colfecha),
                             ", ",
                             "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY HH24:MI:SS'), ",
                             "UN_FORMATOFECHA      =>'", formatofecha, "', ",
                             "UN_FILAINI           =>",
                             Integer.toString(filaini), ",",
                             "UN_USUARIO           =>'", usuario, "'" };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD6.FC_GENERARRTACONCILIADOSXPLANO",
                                            SysmanFunciones.concatenar(par),
                                            Types.CLOB));
        } catch (SQLException | IOException | ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarRtaConciliadosXTipo(
        String compania,
        String cuenta,
        String cadena,
        int colValDebito,
        int colValCredito,
        int colTipo,
        int colComprobante,
        int colConsecutivo,
        int colFecha,
        Date fecha,
        int filaini,
        String usuario) throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_CUENTA            =>'", cuenta, "', ",
                             "UN_CADENA            =>", cadena, ", ",
                             "UN_COLVALDEBITO      =>",
                             Integer.toString(colValDebito), ", ",
                             "UN_COLVALCREDITO     =>",
                             Integer.toString(colValCredito), ", ",
                             "UN_COLTIPO           =>",
                             Integer.toString(colTipo), ", ",
                             "UN_COLCOMPROBANTE    =>",
                             Integer.toString(colComprobante), ", ",
                             "UN_COLCONSECUTIVO    =>",
                             Integer.toString(colConsecutivo), ", ",
                             "UN_COLFECHA         =>",
                             Integer.toString(colFecha), ", ",
                             "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY HH24:MI:SS'), ",
                             "UN_FILAINI           =>",
                             Integer.toString(filaini), ",",
                             "UN_USUARIO           =>'", usuario, "'" };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD6.FC_GENERARRTACONCILIADOSXTIPO",
                                            SysmanFunciones.concatenar(par),
                                            Types.CLOB));
        } catch (SQLException | IOException | ParseException e) {
            throw new SystemException(e);
        }
    }

}
