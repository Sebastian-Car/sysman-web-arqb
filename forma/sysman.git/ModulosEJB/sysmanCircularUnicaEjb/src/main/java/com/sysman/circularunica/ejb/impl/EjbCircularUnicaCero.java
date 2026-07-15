package com.sysman.circularunica.ejb.impl;

import com.sysman.circularunica.ejb.EjbCircularUnicaCeroGeneralRemote;
import com.sysman.circularunica.ejb.EjbCircularUnicaCeroLocal;
import com.sysman.circularunica.ejb.EjbCircularUnicaCeroRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class CircularUnicaCero
 */
@Stateless
@LocalBean

public class EjbCircularUnicaCero implements EjbCircularUnicaCeroRemote, EjbCircularUnicaCeroLocal
{

    @EJB
    private EjbCircularUnicaCeroGeneralRemote ejbCircularUnicaCeroGeneral;

    /**
     * Default constructor.
     */
    public EjbCircularUnicaCero()
    {
    }

    @Override
    public void prepararCodigos(
        String compania,
        int anioIni,
        int anioFin,
        String usuario,
        int opcion)
                    throws SystemException
    {
        ejbCircularUnicaCeroGeneral.prepararCodigos(compania, anioIni, anioFin, usuario, opcion);
    }

    @Override
    public String actualizarVigenciaPlanPptal(
        String compania,
        int anioIni,
        int anioFin,
        String usuario)
                    throws SystemException
    {
        return ejbCircularUnicaCeroGeneral.actualizarVigenciaPlanPptal(compania, anioIni, anioFin, usuario);
    }
    
    @Override
    public String generarProcesoSiaSql(
        String  strsql)
                    throws SystemException {
        try {
            String[] parametros = { "UN_STRSQL          =>'", strsql, "' "
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CHIPFUT1.FC_GENERARPROCESOSIA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch ( IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

}