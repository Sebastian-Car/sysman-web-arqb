package com.sysman.servpublicos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbServiciosPublicosUnoGeneralLocal {

    boolean actualizarMedidores(String compania, int ciclo, String usuario)
                    throws SystemException;

}
