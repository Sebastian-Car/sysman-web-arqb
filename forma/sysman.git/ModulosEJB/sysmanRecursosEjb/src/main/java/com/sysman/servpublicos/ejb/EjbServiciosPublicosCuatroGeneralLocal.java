package com.sysman.servpublicos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbServiciosPublicosCuatroGeneralLocal {

    boolean estarBloqueado(String compania, int ciclo) throws SystemException;

}
