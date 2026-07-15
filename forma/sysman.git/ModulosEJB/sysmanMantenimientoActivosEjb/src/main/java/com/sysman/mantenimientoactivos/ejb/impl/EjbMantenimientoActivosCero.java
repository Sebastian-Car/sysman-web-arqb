package com.sysman.mantenimientoactivos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.mantenimientoactivos.ejb.EjbMantenimientoActivosCeroLocal;
import com.sysman.mantenimientoactivos.ejb.EjbMantenimientoActivosCeroRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class MantenimientoActivosCero
 */
@Stateless
@LocalBean
public class EjbMantenimientoActivosCero
                implements EjbMantenimientoActivosCeroRemote,
                EjbMantenimientoActivosCeroLocal {
    /**
     * Default constructor.
     */
    public EjbMantenimientoActivosCero() {
    }

    @Override
    public boolean getExistenAccesorios(
        String compania,
        String placa,
        long serie)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_PLACA            =>'", placa, "', ",
                                "UN_SERIE            =>", Long.toString(serie)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_MANT.FC_SERIEACCESORIOSVEHICULO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void insertarParteFuncional(
        String compania,
        String placa,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PLACA             =>'", placa, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_MANT.PR_AGREGARVEHICULOPARTES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean actualizarSolicitudMantenimiento(
        String compania,
        int ano,
        String tipo,
        long solicitud,
        long numero,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_SOLICITUD         =>",
                                Long.toString(solicitud), ", ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_MANT.FC_ACTUALIZARSOLICITUD",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public BigInteger enumerarMantenimiento(
        String compania,
        int anio,
        String tipo,
        int numero)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>",
                                Integer.toString(numero), ""
        };
        return new BigInteger((String) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_MANT.FC_ENUMERARMANTENIMIENTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR));
    }

    @Override
    public void mantEstacion(
        String compania,
        int ano,
        String tipoCpte,
        long comprobante,
        String tipo,
        int componente,
        String observacion,
        String tarea,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPOCPTE          =>'", tipoCpte, "', ",
                                "UN_COMPROBANTE       =>",
                                Long.toString(comprobante), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_COMPONENTE        =>",
                                Integer.toString(componente), ", ",
                                "UN_OBSERVACION       =>'", observacion, "', ",
                                "UN_TAREA             =>'", tarea, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_MANT.PR_MANT_COMPONENTES",
                        SysmanFunciones.concatenar(parametros));
    }

}