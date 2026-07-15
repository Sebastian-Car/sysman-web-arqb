package com.sysman.servpublicos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosUnoGeneralRemote {

    boolean actualizarMedidores(String compania, int ciclo, String usuario)
                    throws SystemException;

}
