package com.sysman.contabilidad.ejb;

import java.math.BigInteger;

import javax.ejb.Local;

import com.sysman.exception.SystemException;

@Local
public interface EjbContabilidadCpteGeneralLocal {
	
    BigInteger enumerarComprobanteCnt(
    		String compania, 
    		int anio, 
    		String tipo,
            BigInteger numero, 
            String centroCosto)
                        throws SystemException;

}
