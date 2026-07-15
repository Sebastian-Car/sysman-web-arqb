package com.sysman.servpublicos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosCuatroGeneralRemote {

    boolean estarBloqueado(String compania, int ciclo) throws SystemException;

}
