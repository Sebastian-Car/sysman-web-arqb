package com.sysman.recursos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbAuditoriaLocal;
import com.sysman.recursos.ejb.EjbAuditoriaRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class Auditoria
 */
@Stateless
@LocalBean
public class EjbAuditoria implements EjbAuditoriaRemote, EjbAuditoriaLocal {
    /**
     * Default constructor.
     */
    public EjbAuditoria() {
    }

    @Override
    public String generarTrigger(
        String compania,
        String tablas)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TABLAS            =>'", tablas, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_AUDITORIA.FC_TRIGGER",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
    
    @Override
    public void actTablasAuditoria(
        String compania)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "'"};
        
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_AUDITORIA.PR_ACT_TABLASAU",
                        SysmanFunciones.concatenar(parametro));
    }
    
}