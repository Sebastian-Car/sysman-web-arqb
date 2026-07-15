package com.sysman.chipfut.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbChipFutUnoGeneralRemote {

    void subirSeguimientoReciprocas(
        String compania,
        String cambios,
        String usuario,
        String consecutivo)
                    throws SystemException;

    void copiarAuxiliar(String compania, int anoDestino, int anoOrigen,
        String companiaDestino) throws SystemException;

    void copiarFuenteRecurso(String compania, int anoDestino, int anoOrigen,
        String companiaDestino) throws SystemException;
}