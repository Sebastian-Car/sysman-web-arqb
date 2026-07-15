package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbCodigoBarrasRemote {
    String imprimirCodigoBarras(String textoParaConvertir)
                    throws SystemException;
}