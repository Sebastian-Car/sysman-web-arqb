package com.sysman.ejb.report;

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.logica.Compania;
import com.sysman.logica.Usuario;

import java.util.Map;

import javax.ejb.Local;

@Local
public interface ParameterProviderLocal {

    Map<String, Object> getParametrosModulo(int modulo, Usuario user,
        Compania compania, Map<String, Object> sessionVars, String reporte)
                    throws SysmanException;

    Map<String, Object> getReemplazosModulo(int modulo, Usuario user,
        Compania compania, Map<String, Object> sessionVars, String reporte,
        String menu)
                    throws SysmanException;

}
