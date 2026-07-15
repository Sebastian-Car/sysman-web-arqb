package com.sysman.exogenas.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.exogenas.ejb.EjbExogenasCeroLocal;
import com.sysman.exogenas.ejb.EjbExogenasCeroRemote;
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
 * Session Bean implementation class ExogenasCero
 */
@Stateless
@LocalBean

public class EjbExogenasCero implements EjbExogenasCeroRemote, EjbExogenasCeroLocal {
    /**
     * Default constructor.
     */
    public EjbExogenasCero() {
    }

    @Override
    public void actualizarConceptoExogena(String compania, int ano, String formato, String usuario)
            throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_ANO               =>",
                Integer.toString(ano), ", ", "UN_FORMATO           =>'", formato, "', ", "UN_USUARIO           =>'",
                usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_EXOGENAS.PR_CONCEPTOSPORFORMATO",
                SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void migrarPlanCuentas(String compania, String ano, String formato, String usuario) throws SystemException {
        String[] parametros = 
        { "UN_ANO               =>", ano, ", ", "UN_COMPANIA          =>'", compania, "', ",
                "UN_FORMATO           =>'", formato, "', ", "UN_USUARIO           =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(
            ConectorPool.ESQUEMA_SYSMAN, "PCK_EXOGENAS.PR_ACTUALIZAR_CONF_EXOGENAS",
                SysmanFunciones.concatenar(parametros));
    }

	public String actTercero2276(String compania, String cadenaplan, String usuario) throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_CADENAPLAN        =>'", cadenaplan, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		try {
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_EXOGENAS.FC_ACT_TERCEROS_EXOGENAS", SysmanFunciones.concatenar(parametros), Types.CLOB));
		} catch (IOException | SQLException e) {
// TODO Auto-generated catch block
			throw new SystemException(e);
		}
	}
	
	 @Override
		public String actConceptoExogenaMasivo(
				 String compania, String anio, String cadenaplan, String usuario)
						throws SystemException {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_ANIO        =>", anio ,"," ,
	                "UN_CADENAPLAN        =>", cadenaplan, ", ",
	                "UN_USUARIO           =>'", usuario, "'"
			};
			try {
				return Acciones.clobToStringSalto(
						(Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
						"PCK_EXOGENAS.FC_ACT_MASIVA_CONCEPTOS_EXO",
						SysmanFunciones.concatenar(parametros),
						Types.CLOB));
			} catch (IOException | SQLException e) {
				throw new SystemException(e);
			}
		}
	 
	 @Override
	 public String copiarConceptoExogenas(String compania, String anioDestino, String ano,
			 String companiaDestino, String formato) throws SystemException {
		 String[] parametros = {
				 "UN_COMPANIA          =>'", compania,        "', ",
				 "UN_ANO_DESTINO       =>",  anioDestino,     ",  ",
				 "UN_ANO_ORIGEN        =>",  ano,             ",  ",
				 "UN_COMPANIA_DESTINO  =>'", companiaDestino, "', ",
				 "UN_FORMATO           =>'", formato,         "'"
		 };
		 return (String) AccionesImp.ejecutarFuncion(
				 ConectorPool.ESQUEMA_SYSMAN,
				 "PCK_EXOGENAS.FC_COPIAR_CONCEPTO_EXOGENA",
				 SysmanFunciones.concatenar(parametros),
				 Types.VARCHAR);
	 }
}