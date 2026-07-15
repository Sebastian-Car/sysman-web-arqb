package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarNominaDosGeneralLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaDosGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilizarDosGeneral
 */
@Stateless
@LocalBean

public class EjbContabilizarNominaDosGeneral implements EjbContabilizarNominaDosGeneralRemote, EjbContabilizarNominaDosGeneralLocal
{
    /**
     * Default constructor.
     */
    public EjbContabilizarNominaDosGeneral()
    {
    }

    @Override
    public void revisarCuentasPlancontable(
        String companiaNomina,
        String companiaDs,
        int ano,
        int mes,
        int periodo,
        int proceso,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIANOMINA    =>'", companiaNomina, "', ",
                                "UN_COMPANIADS        =>'", companiaDs, "', ",
                                "UN_ANNO              =>", Integer.toString(ano), ", ",
                                "UN_MESS              =>", Integer.toString(mes), ", ",
                                "UN_PERIODOO          =>", Integer.toString(periodo), ", ",
                                "UN_PROCESO           =>", Integer.toString(proceso), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTABILIZAR_NOMINA2.PR_REVISARCUENTASPLANCONTABLE",
                        SysmanFunciones.concatenar(parametros));
    }

}