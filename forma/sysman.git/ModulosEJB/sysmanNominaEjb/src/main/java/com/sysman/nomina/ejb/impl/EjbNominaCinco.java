package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaCincoLocal;
import com.sysman.nomina.ejb.EjbNominaCincoRemote;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class NominaCinco
 */
@Stateless
@LocalBean
public class EjbNominaCinco
                implements EjbNominaCincoRemote, EjbNominaCincoLocal {
    /**
     * Default constructor.
     */
    public EjbNominaCinco() {
    }

    @Override
    public BigDecimal obtenerMiMesada(
        String compania,
        int idempleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM5.FC_MIMESADA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean verificarConceptoFactorSS(
        String compania,
        int concepto)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM5.FC_ESFACTOR_SS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void sumarRetroactivosCinco(
        String compania,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM5.PR_OPRIMIRSUMARRETROACTIVOS05",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String generarDisco(
        String compania,
        int tipoliquidacion,
        int procesonomina,
        int anionomina,
        int mesnomina,
        int periodonomina,
        String estructura,
        String planilla,
        boolean correccion,
        String numcorreccion,
        Date fechacorreccion,
        int aniocorreccion,
        int mescorreccion,
        String nitcompania,
        boolean retroactivo,
        int periodoretro,
        Date fechaauto,
        String numradicacion,
        int orden,
        int empleado,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOLIQUIDACION   =>",
                                    Integer.toString(tipoliquidacion), ", ",
                                    "UN_PROCESONOMINA     =>",
                                    Integer.toString(procesonomina), ", ",
                                    "UN_ANIONOMINA        =>",
                                    Integer.toString(anionomina), ", ",
                                    "UN_MESNOMINA         =>",
                                    Integer.toString(mesnomina), ", ",
                                    "UN_PERIODONOMINA     =>",
                                    Integer.toString(periodonomina), ", ",
                                    "UN_ESTRUCTURA        =>'", estructura,
                                    "', ", "UN_PLANILLA          =>'", planilla,
                                    "', ", "UN_CORRECCION        =>",
                                    correccion ? "-1" : "0", ", ",
                                    "UN_NUMCORRECCION     =>",
                                    numcorreccion != null
                                        ? "'" + numcorreccion + "'"
                                        : "NULL",
                                    ", ",
                                    "UN_FECHACORRECCION   =>"
                                        + (fechacorreccion != null
                                            ? "TO_DATE('" +
                                                SysmanFunciones.convertirAFechaCadena(
                                                                fechacorreccion)
                                                + "','DD/MM/YYYY'), "
                                            : "NULL,"),
                                    "UN_ANIOCORRECCION    =>",
                                    Integer.toString(aniocorreccion), ", ",
                                    "UN_MESCORRECCION     =>",
                                    Integer.toString(mescorreccion), ", ",
                                    "UN_NITCOMPANIA       =>'", nitcompania,
                                    "', ", "UN_RETROACTIVO       =>",
                                    retroactivo ? "-1" : "0", ", ",
                                    "UN_PERIODORETRO      =>",
                                    Integer.toString(periodoretro), ", ",
                                    "UN_FECHAAUTO         =>"
                                        + (fechaauto != null ? " TO_DATE('" +
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaauto)
                                            +
                                            "','DD/MM/YYYY'), " : "NULL,"),
                                    "UN_NUMRADICACION     => ",
                                    numradicacion != null
                                        ? "'" + numradicacion + "'"
                                        : "NULL",
                                    ", ",
                                    "UN_ORDEN     =>", Integer.toString(orden),
                                    ", ",
                                    "UN_IDEMPLEADO     =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_COM5.FC_GENERARDISCO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
    

    @Override
    public  String generarDiscoFoncepFavidi(
		String compania, 
		int proceso, 
		String usuario, 
		int periodo, 
		String opcion, 
		int mes, 
		int ano) 
        throws SystemException {
	    	try {
	    		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
	                 , "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
	                 , "UN_USUARIO           =>'" , usuario , "', "
	                 , "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
	                 , "UN_OPCION            =>'" , opcion , "', "
	                 , "UN_MES               =>" , Integer.toString(mes) , ", "
	                 , "UN_ANO               =>" , Integer.toString(ano) , ""
	    		};
	         
				return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM5.FC_DISCOFAVIDI",
	SysmanFunciones.concatenar(parametros),
				        Types.CLOB));
			} catch (IOException | SQLException e) {
				throw  new SystemException(e);
			}
    	}

    @Override
    public  String  generarDisPlanoBBogota(
    		String  compania, 
    		int ano, 
    		int mes, 
    		int periodo, 
    		String referencia) 
    				throws SystemException {
    	try { 
    		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
    				, "UN_ANO               =>" , Integer.toString(ano) , ", "
    				, "UN_MES               =>" , Integer.toString(mes) , ", "
    				, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
    				, "UN_REFERENCIA        =>'" , referencia , "'"
    		};

    		return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM5.FC_DSIP_BANCOBOGOTA",
    				SysmanFunciones.concatenar(parametros),
    				Types.CLOB));
    	} catch (IOException | SQLException e) {
    		throw  new SystemException(e);
    	}
    }

    @Override
    public String cargarBasesNovedades(
    		String compania, 
    		String anio, 
    		String mes,  
    		String cadena, 
    		String usuario)
    				throws SystemException {
    	try { 
    		String[] parametros = {
    				"UN_COMPANIA     =>'" + compania + "', ",
    				"UN_ANIO         =>'" + anio + "', ",
    				"UN_MES          =>'" + mes + "', ",
    				"UN_CADENA       =>"  + cadena + ", ",
    				"UN_USUARIO      =>'" + usuario + "'"
    		};

    		return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
    				ConectorPool.ESQUEMA_SYSMAN,
    				"PCK_NOMINA_COM5.FC_CARGAR_BASESNOVEDADES",
    				SysmanFunciones.concatenar(parametros),
    				Types.CLOB));
    	} catch (IOException | SQLException e) {
    		throw  new SystemException(e);
    	}
    }

}