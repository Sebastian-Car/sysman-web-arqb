package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbCodigoBarrasLocal {

    String imprimirCodigoBarras(String textoParaConvertir)
                    throws SystemException;

}