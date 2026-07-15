package com.sysman.servpublicos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosSieteGeneralRemote {

    String calcularFacturacion(String compania, int intciclo,
        String strcodigoinicial, String strcodigofinal, boolean enserie,
        boolean finall, String usuario) throws SystemException;

    void actualizarRangos(String compania) throws SystemException;

}
