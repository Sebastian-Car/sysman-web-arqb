package com.sysman.circularunica.ejb.impl;

import com.sysman.circularunica.ejb.EjbCircularUnicaCeroGeneralLocal;
import com.sysman.circularunica.ejb.EjbCircularUnicaCeroGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class CircularUnicaCero
 */
@Stateless
@LocalBean

public class EjbCircularUnicaCeroGeneral implements EjbCircularUnicaCeroGeneralRemote, EjbCircularUnicaCeroGeneralLocal
{

    /**
     * Default constructor.
     */
    public EjbCircularUnicaCeroGeneral()
    {
    }

    @Override
    public void prepararCodigos(
        String compania,
        int anioIni,
        int anioFin,
        String usuario,
        int opcion)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO_INI          =>", Integer.toString(anioIni), ", ",
                                "UN_ANIO_FIN          =>", Integer.toString(anioFin), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_OPCION            =>", Integer.toString(opcion), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CIRCULAR_UNICA.PR_PREPARAR_CODIGOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String actualizarVigenciaPlanPptal(
        String compania,
        int anioIni,
        int anioFin,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO_INI          =>", Integer.toString(anioIni), ", ",
                                "UN_ANIO_FIN          =>", Integer.toString(anioFin), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };

        try
        {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CIRCULAR_UNICA.FC_ACTUALIZAR_VIG_PLANPPTAL",
                                            SysmanFunciones.concatenar(parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {
            throw new SystemException(e);

        }

    }
    
    @Override
  	public String cargarConfiguracionPptal(
  			String tabla,
  			String cadena,
  			String usuario,
  			String compania,
  			int ano)
  					throws SystemException {
  		try {
  		
  			String[] parametro = {
  					"UN_TABLA       =>'", tabla, "', ",
  					"UN_CADENA      =>", cadena, ", ",
  					"UN_USUARIO     =>'", usuario, "', ", 
  					"UN_COMPANIA    =>'", compania, "', ",
  					"UN_ANO    		=>", Integer.toString(ano), " "
  			};

  			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
  					ConectorPool.ESQUEMA_SYSMAN,
  					"PCK_CIRCULAR_UNICA.FC_CARGAR_CONFIG_PRESUPUESTAL",
  					SysmanFunciones.concatenar(parametro),
  					Types.CLOB));
  		}
  		catch (IOException | SQLException e) {
  			throw new SystemException(e);
  		}
  	}

}