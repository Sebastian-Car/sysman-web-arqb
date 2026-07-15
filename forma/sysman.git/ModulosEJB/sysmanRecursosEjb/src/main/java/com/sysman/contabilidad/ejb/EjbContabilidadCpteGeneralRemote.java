package com.sysman.contabilidad.ejb;

import java.math.BigInteger;

import javax.ejb.Remote;

import com.sysman.exception.SystemException;

@Remote
public interface EjbContabilidadCpteGeneralRemote {
	
    BigInteger enumerarComprobanteCnt(
    		String compania, 
    		int anio, 
    		String tipo,
            BigInteger numero, 
            String centroCosto)
                        throws SystemException;

}
