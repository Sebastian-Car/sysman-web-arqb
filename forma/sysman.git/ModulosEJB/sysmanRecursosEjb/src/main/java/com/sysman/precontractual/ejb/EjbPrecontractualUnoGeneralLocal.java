package com.sysman.precontractual.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbPrecontractualUnoGeneralLocal
{

    void subirCodigosUnspsc(
        String compania,
        String cambios,
        String usuario)
                    throws SystemException;
}