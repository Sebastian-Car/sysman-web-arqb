package com.sysman.precontractual.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbPrecontractualUnoGeneralRemote
{

    void subirCodigosUnspsc(
        String compania,
        String cambios,
        String usuario)
                    throws SystemException;
}