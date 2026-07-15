package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarFacGenLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarFacGenRemote;
import com.sysman.exception.SystemException;
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

/**
 * Session Bean implementation class ContabilizarCero
 */
@Stateless
@LocalBean
public class EjbContabilizarFacGen
                implements EjbContabilizarFacGenRemote,
                EjbContabilizarFacGenLocal {

    /**
     * Default constructor.
     */
    public EjbContabilizarFacGen() {
    }

    @Override
    public String enviarConceptosSinConfiguracion(
        String compania,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA => '", compania, "',",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ""
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR.FC_CONCEPTOSINCONFIGURACION",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);

        }
    }

}