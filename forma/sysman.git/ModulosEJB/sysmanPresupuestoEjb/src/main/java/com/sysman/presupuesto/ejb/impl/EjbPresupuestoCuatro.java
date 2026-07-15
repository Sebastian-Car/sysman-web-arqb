/*-
 * EjbPresupuestoTres.java
 *
 * 1.0
 *
 * 26/05/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PresupuestoCuatro
 */
@Stateless
@LocalBean
public class EjbPresupuestoCuatro implements EjbPresupuestoCuatroRemote,
                EjbPresupuestoCuatroLocal {
    /**
     * Default constructor.
     */
    public EjbPresupuestoCuatro() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public void congelarSaldosMan(
        String compania,
        int anio,
        String tipo,
        BigInteger comprobanteIni,
        BigInteger comprobanteFin)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_COMPROBANTE_INI   =>",
                                comprobanteIni.toString(), ", ",
                                "UN_COMPROBANTE_FIN   =>",
                                comprobanteFin.toString()
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO_COM4.PR_CONGELARSALDOSMAN",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void insertaPpto(
        String compania,
        String claseOrden,
        String numero,
        String claseDisp,
        long numeroDispSel,
        String tercero,
        String sucursal,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_NUMERO            =>'", numero, "', ",
                                "UN_CLASEDISP         =>'", claseDisp, "', ",
                                "UN_NUMERODISPSEL     =>",
                                Long.toString(numeroDispSel), ", ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO_COM4.PR_INSERTAPPTO",
                        SysmanFunciones.concatenar(parametros));
    }

	@Override
	public void cargarComprobanteDetallePptal(String compania, String cadenaplan, String usuario)
			throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                				"UN_CADENAPLAN        =>'", cadenaplan, "', ",
                				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PRESUPUESTO_COM4.PR_CARGAR_COMP_DETALLE_PPTAL",
				SysmanFunciones.concatenar(parametros));
		
	}
	
	@Override
	public void cargarTipoClasificador(String compania, String cadenaplan, String usuario)
			throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                				"UN_CADENAPLAN        =>'", cadenaplan, "', ",
                				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PRESUPUESTO_COM4.PR_CARGAR_TIPO_CLASIFICADORES",
				SysmanFunciones.concatenar(parametros));
		
	}
	
	@Override
	public void cargarClasificadorHijo(String compania, String cadenaplan, String usuario)
			throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                				"UN_CADENAPLAN        =>'", cadenaplan, "', ",
                				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_PRESUPUESTO_COM4.PR_CARGAR_CLASIFICADORES_HIJO",
				SysmanFunciones.concatenar(parametros));
		
	}
	
	@Override
	public  void  cargarPmrFuente(
			String compania, 
			String cadena, 
			String usuario) 
					throws SystemException {
		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
				, "UN_CADENA            =>" , cadena , ", "
				, "UN_USUARIO           =>'" , usuario , "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_PRESUPUESTO_COM4.PR_CARGAR_PMR_FUENTE",
				SysmanFunciones.concatenar(parametros));
	}
	/**
	 * Se reemplaza el nombre del metodo original FC_ACTUALIZARCLASIFICADORESPPTAL por 
	 * FC_ACT_CLASIFICADORESPPTAL para evitar que soprepase los 30 caractereres
	 */
	 @Override
	    public boolean actualizarClasificadoresPptal(
	        String compania,
	        int ano,
	        String usuario
	        )
	                    throws SystemException {
	        String[] parametro = { "UN_COMPANIA   =>'", compania, "', ",
	                               "UN_ANIO        =>", Integer.toString(ano) ,"," ,
	                               "UN_USUARIO    =>'", usuario, "'"
	        };
	        byte rta = (byte) AccionesImp.ejecutarFuncion(
	                        ConectorPool.ESQUEMA_SYSMAN,
	                        "PCK_PRESUPUESTO_COM4.FC_ACT_CLASIFICADORESPPTAL",
	                        SysmanFunciones.concatenar(parametro),
	                        Types.TINYINT);
	        return rta != 0;
	    }
	 
	    @Override
	    public String generarlibroRegistrosSql(
	        String  strsql)
	                    throws SystemException {
	        try {
	            String[] parametros = { "UN_STRSQL          =>'", strsql, "' "
	            };
	            return Acciones.clobToStringSalto(
	                            (Clob) AccionesImp.ejecutarFuncion(
	                                            ConectorPool.ESQUEMA_SYSMAN,
	                                            "PCK_PRESUPUESTO_COM4.FC_GENERALIBROREGISTROSSQL",
	                                            SysmanFunciones.concatenar(
	                                                            parametros),
	                                            Types.CLOB));
	        }
	        catch ( IOException | SQLException e) {
	            throw new SystemException(e);
	        }
	    }	 
	    
	    
            public  void  actualizarAuxiliaresIngreso(
				String compania, 
				String anio, 
				String rubro,
				String centrocostoini, 
				String centrocostofin, 
				String auxiliarini,
				String auxiliarfin,
				String referenciaini,
				String referenciafin,
				String fuenteini, 
				String fuentefin) 
						throws SystemException {
	    	String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_ANIO            =>" , anio , ", "
					, "UN_RUBRO            =>'" , rubro , "', "
					, "UN_CENTROCOSTO_INI            =>'" , centrocostoini , "', "
					, "UN_CENTROCOSTO_FIN            =>'" , centrocostofin , "', "
					, "UN_AUXILIAR_INI           =>'" , auxiliarini , "', "
					, "UN_AUXILIAR_FIN            =>'" , auxiliarfin , "', "
					, "UN_REFERENCIA_INI            =>'" , referenciaini , "', "
					, "UN_REFERENCIA_FIN            =>'" , referenciafin , "', "
					, "UN_FUENTERECURSO_INI            =>'" , fuenteini , "', "
					, "UN_FUENTERECURSO_FIN            =>'" , fuentefin , "' "
			};
			AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_PRESUPUESTO_COM4.PR_ACTUALIZAR_AUX_EN_INGRESO",
					SysmanFunciones.concatenar(parametros));
		}
	    
	    
	    @Override
	    public void cargarMovimientosPptales(String compania, String cadenah, String cadenad, String usuario)
	                    throws SystemException {
	        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
	                                "UN_CADENAH   =>" , cadenah , " , ",
	                                "UN_CADENAD   =>" , cadenad , " , ",
	                                "UN_USUARIO   =>'", usuario , "' "
	        };
	        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
	                        "PCK_PRESUPUESTO_COM4.PR_CARGAR_MOV_PPTALES",
	                        SysmanFunciones.concatenar(parametros));

	    }
	    
	 
	 
	 

}