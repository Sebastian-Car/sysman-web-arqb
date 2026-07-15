/*-
 * EjbNominaCeroGeneral.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaNueveGeneralLocal;
import com.sysman.nomina.ejb.EjbNominaNueveGeneralRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * 
 * @version 1.0, 19/01/2018
 * @author ybecerra
 *
 */
@Stateless
@LocalBean
public class EjbNominaNueveGeneral implements EjbNominaNueveGeneralRemote,
                EjbNominaNueveGeneralLocal {

    /**
     * Default constructor.
     */
    public EjbNominaNueveGeneral() {
    }

    @Override
    public void actIndCuneEmpleado(
        String compania,
        boolean opcion)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_OPCION         =>",
                                opcion ? "-1" : "0", " "

        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM9.PR_ACTINDCUNEEMPLEADO",
                        SysmanFunciones.concatenar(parametros));

    }
    
    @Override
    public void updateCptoPeriodoNomina(
    		String compania,
            int proceso,
            int anio,
            int mes,
            int periodo)
                    throws SystemException {

        
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                "UN_PROCESO           =>",
                Integer.toString(proceso), ", ",
                "UN_ANIO              =>",
                Integer.toString(anio), ", ",
                "UN_MES               =>",
                Integer.toString(mes), ", ",
                "UN_PERIODO           =>",
                Integer.toString(periodo), ""
        }; //

          AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM9.FC_MANTE_ELIM_CPTO_NOMINA",
                        SysmanFunciones.concatenar(parametros),Types.VARCHAR);

    }
    
    @Override
    public void deleteCptoPeriodoNomina(
    		String compania,
            int proceso,
            int anio,
            int mes,
            int periodo,
            int concepto,
            int empleado)
                    throws SystemException {

 
        
         String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                "UN_PROCESO           =>",
                Integer.toString(proceso), ", ",
                "UN_ANIO              =>",
                Integer.toString(anio), ", ",
                "UN_MES               =>",
                Integer.toString(mes), ", ",
                "UN_PERIODO           =>",
                Integer.toString(periodo), ", ",
                "UN_ID_DE_EMPLEADO    =>",
                Integer.toString(empleado), ", ",
                "UN_IDCONCEPTO           =>",
                Integer.toString(concepto), ""
        };
        
       

          AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM9.FC_ELIMININAR_CPTO_NOMINA",
                        SysmanFunciones.concatenar(parametros),Types.VARCHAR); 
 

    }
    
    @Override
	public void verificarcalculofondosolidaridad(String compania, int periodo, int proceso, int anio, int mes,
			String usuario) throws SystemException {

		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_ANO           =>",
				Integer.toString(anio), ", ", "UN_MES              =>", Integer.toString(mes), ", ",
				"UN_PERIODO               =>", Integer.toString(periodo), ", ", "UN_PROCESO           =>",
				Integer.toString(proceso), ", ", "UN_USUARIO    =>'", usuario, "' ", "" };

		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM9.PR_DOS_IDEMPLEADO",SysmanFunciones.concatenar(parametros));
    }   
    
    
    @Override
	public void verificarcarencargospersonalhistorico(String compania, String anio, int empleado) throws SystemException {

		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_ANO           =>",
				anio, ", ", "UN_MES              =>", null, ", ",
				"UN_PERIODO               =>", null , ", ", "UN_PROCESO               =>", null,  ", ",
				"UN_FECHAFIN               =>", null,", ", "UN_FECHAINICIO           =>",
				null, ", ", "UN_ID_EMPLEADO    =>", Integer.toString(empleado), " ", "" };


		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM9.PR_ACT_ENC_PERS_HIST",SysmanFunciones.concatenar(parametros));
    }
    
    @Override
	public void revisarCalculoParafiscales(String compania, int periodo, int proceso, int anio, int mes,
			String usuario) throws SystemException {

		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_ANO           =>",
				Integer.toString(anio), ", ", "UN_MES              =>", Integer.toString(mes), ", ",
				"UN_PERIODO               =>", Integer.toString(periodo), ", ", "UN_PROCESO           =>",
				Integer.toString(proceso), ", ", "UN_USUARIO    =>'", usuario, "' ", "" };

		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, 
				"PCK_NOMINA_COM9.PR_REVISAR_PARAFISCALES",
				SysmanFunciones.concatenar(parametros));
    }   
    
}
