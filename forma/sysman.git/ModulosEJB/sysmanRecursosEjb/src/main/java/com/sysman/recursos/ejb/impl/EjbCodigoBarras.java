package com.sysman.recursos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbCodigoBarrasLocal;
import com.sysman.recursos.ejb.EjbCodigoBarrasRemote;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EjbSysmanUtil
 */
@Stateless
@LocalBean
public class EjbCodigoBarras
                implements EjbCodigoBarrasRemote, EjbCodigoBarrasLocal {
    /**
     * Default constructor.
     */
    public EjbCodigoBarras() {
    }

    @Override
    public String imprimirCodigoBarras(String textoParaConvertir)
                    throws SystemException {

        return (String) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CODIGODEBARRAS.FC_IMPRIMIRCODIGODEBARRAS",
                        "UN_TEXTOPARACONVERTIR          =>'"
                            + textoParaConvertir + "' ",
                        Types.VARCHAR);

    }

}