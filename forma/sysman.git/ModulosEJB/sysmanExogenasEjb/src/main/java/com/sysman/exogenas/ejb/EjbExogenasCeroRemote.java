package com.sysman.exogenas.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbExogenasCeroRemote {

    void actualizarConceptoExogena(String compania, int ano, String formato, String usuario) throws SystemException;

    void migrarPlanCuentas(String compania, String ano, String formato, String usuario) throws SystemException;
    
    String actConceptoExogenaMasivo(String compania, String anio, String cadenaplan, String usuario) throws SystemException;

  	String copiarConceptoExogenas(String compania, String anioDestino, String ano, String companiaDestino,
  			String formato) throws SystemException;
}