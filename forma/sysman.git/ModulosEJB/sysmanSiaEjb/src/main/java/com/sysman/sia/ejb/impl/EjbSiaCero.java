package com.sysman.sia.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.sia.ejb.EjbSiaCeroLocal;
import com.sysman.sia.ejb.EjbSiaCeroRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class SiaCero
 */
@Stateless
@LocalBean

public class EjbSiaCero implements EjbSiaCeroRemote, EjbSiaCeroLocal {
    /**
     * Default constructor.
     */
    public EjbSiaCero() {
    }

    @Override
    public void configurarCuentasDesc(
        String compania,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SIA.PR_TRANSLADAR_ANIO",
                        SysmanFunciones.concatenar(parametros));
    }
    
    
    @Override
    public String subirConsolidadoSia(
        String tabla, 
        String datos, 
        String usuario, 
        String sobrescribir
    ) throws SystemException {
    	
        String[] parametros = { 
            "UN_TABLA    =>'", tabla, "', ",
            "UN_DATOS    => ", datos, ", ",
            "UN_USUARIO  =>'", usuario, "', ",
            "UN_SOBRESCRIBIR   => '", sobrescribir, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
        		"PCK_SIA.FC_SUBIR_CONSOLIDADO_SIA",
        		 SysmanFunciones.concatenar(parametros),
                 Types.VARCHAR);
    }

}