package com.sysman.recursos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbMenukLocal;
import com.sysman.recursos.ejb.EjbMenukRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EjbMenuk Permite la gestion de
 * opciones de menu y permisos a estas
 */
@Stateless
@LocalBean
public class EjbMenuk implements EjbMenukRemote, EjbMenukLocal {

    /**
     * Default constructor.
     */
    public EjbMenuk() {
        //
    }

    @Override
    public String retornarXMLMenus(String compania, String usuario)
                    throws SystemException {
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMANK,
                            "MENUK.MENUS",
                            "UN_COMPANIA              =>'" + compania + "', "
                                + "UN_USUARIO              =>'" + usuario
                                + "'",
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void asignarAccesoMenus(
        String compania,
        String grupo,
        String menu,
        String usuario,
        boolean indVer,
        int modulo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA     =>'", compania, "', ",
                                "UN_GRUPO        =>'", grupo, "', ",
                                "UN_MENU         =>'", menu, "', ",
                                "UN_USUARIO      =>'", usuario, "', ",
                                "UN_IND_VER      =>", indVer ? "-1" : "0",
                                ",UN_MODULO => ", Integer.toString(modulo), ""
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMANK,
                        "MENUK.PR_ASIGNARACCESOMENUS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String generarClave(int lon)
                    throws SystemException {
        String[] parametros = { "UN_LON =>", Integer.toString(lon)
        };
        return (String) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMANK,
                        "MENUK.FC_GENERAR_CLAVE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean generarClaveUsuario(
        String usuario,
        int tiempo,
        int lon)
                    throws SystemException {

        byte salida;

        String[] parametros = { "UN_USUARIO           =>'", usuario, "', ",
                                "UN_TIEMPO            =>",
                                Integer.toString(tiempo), ", ",
                                "UN_LON               =>", Integer.toString(lon)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMANK,
                        "MENUK.FC_GENERAR_CLAVE_USUARIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean autorizarAccesoUsuario(String compania, String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA     =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMANK,
                        "MENUK.FC_AUTORIZAR_ACCESO_USUARIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

}
