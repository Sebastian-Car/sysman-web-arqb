/*-
 * CGRCero.java
 *
 * 1.0
 * 
 * 17/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr.ejb.impl;

import com.sysman.cgr.ejb.EjbCGRCeroLocal;
import com.sysman.cgr.ejb.EjbCGRCeroRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EjbCGRCero
 */
@Stateless
@LocalBean
public class EjbCGRCero implements EjbCGRCeroRemote, EjbCGRCeroLocal {
	/**
	 * Default constructor.
	 */
	public EjbCGRCero() {
	}

	@Override
	public String generarConfiguracion(
			String compania,
			int anio)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANIO              =>",
				Integer.toString(anio), ""
		};
		try {
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_CGR.FC_VERIFICARCONFIGURACION",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarPlanoEjecucionGastos(
			String compania,
			int ano,
			int trimestre,
			String codigoentidad,
			String codigocontaduria,
			boolean anticipos,
			boolean excel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TRIMESTRE         =>",
				Integer.toString(trimestre), ", ",
				"UN_CODIGOENTIDAD     =>'", codigoentidad,
				"', ", "UN_CODIGOCONTADURIA  =>'",
				codigocontaduria, "', ",
				"UN_ANTICIPOS         =>",
				anticipos ? "-1" : "0", ", ",
						"UN_EXCEL             =>", (excel ? "-1" : "0"),
						""
		};
		try {
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.FC_EJECUCION_GASTOS",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarPlanoEjecucionGastosRegalias(
			String compania,
			int ano,
			int trimestre,
			String codigoContaduria,
			boolean anticipos,
			String codigoSgr,
			boolean excel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TRIMESTRE         =>",
				Integer.toString(trimestre), ", ",
				"UN_CODIGOCONTADURIA  =>'", codigoContaduria,
				"', ",
				"UN_ANTICIPOS         =>",
				anticipos ? "-1" : "0", ", ",
						"UN_CODIGOSGR         =>'", codigoSgr, "', ",
						"UN_EXCEL             =>", excel ? "-1" : "0",
								""
		};
		try {
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.FC_EJECUCION_GASTOS_REGALIAS",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarPlanoEjecucionIngresos(
			String compania,
			int ano,
			int trimestre,
			String codigoentidad,
			String codigocontaduria,
			boolean excel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TRIMESTRE         =>",
				Integer.toString(trimestre), ", ",
				"UN_CODIGOENTIDAD     =>'", codigoentidad,
				"', ", "UN_CODIGOCONTADURIA  =>'",
				codigocontaduria, "', ",
				"UN_EXCEL             =>", (excel ? "-1" : "0"),
				""
		};
		try {
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.FC_EJECUCION_INGRESOS",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarPlanoEjecucionIngresosRegalias(
			String compania,
			int ano,
			int trimestre,
			String codigoContaduria,
			String codigoSgr,
			boolean excel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TRIMESTRE         =>",
				Integer.toString(trimestre), ", ",
				"UN_CODIGOCONTADURIA  =>'", codigoContaduria,
				"', ", "UN_CODIGOSGR         =>'", codigoSgr,
				"', ", "UN_EXCEL             =>",
				excel ? "-1" : "0", ""
		};
		try {
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.FC_EJECUCION_INGRESOS_REGALIAS",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarPlanoProgramacionGastosRegalias(
			String compania,
			int ano,
			int trimestre,
			String codigoContaduria,
			String codigoSgr,
			boolean excel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TRIMESTRE         =>",
				Integer.toString(trimestre), ", ",
				"UN_CODIGOCONTADURIA  =>'", codigoContaduria,
				"', ", "UN_CODIGOSGR         =>'", codigoSgr,
				"', ",
				"UN_EXCEL             =>", excel ? "-1" : "0",
						""
		};
		try {
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.FC_PROGRAMA_GASTOS_REGALIAS",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarPlanoProgramacionIngresosRegalias(
			String compania,
			int ano,
			int trimestre,
			String codigoContaduria,
			String codigoSgr,
			boolean excel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TRIMESTRE         =>",
				Integer.toString(trimestre), ", ",
				"UN_CODIGOCONTADURIA  =>'", codigoContaduria,
				"', ", "UN_CODIGOSGR         =>'", codigoSgr,
				"', ", "UN_EXCEL             =>",
				excel ? "-1" : "0", ""
		};
		try {
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.FC_PROGRAMA_INGRESOS_REGALIAS",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarPlanoProgramacionGastos(
			String compania,
			int ano,
			int trimestre,
			String codigoContaduria,
			boolean excel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TRIMESTRE         =>",
				Integer.toString(trimestre), ", ",
				"UN_CODIGOCONTADURIA  =>'", codigoContaduria,
				"', ", "UN_EXCEL       =>", excel ? "-1" : "0"
		};
		try {
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.FC_PROGRAMACION_GASTOS",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarPlanoProgramacionIngresos(
			String compania,
			int ano,
			int trimestre,
			String codigocontaduria,
			boolean excel)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_TRIMESTRE         =>",
				Integer.toString(trimestre), ", ",
				"UN_CODIGOCONTADURIA  =>'", codigocontaduria,
				"', ", "UN_EXCEL             =>",
				excel ? "-1" : "0", ""
		};
		try {
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.FC_PROGRAMACION_INGRESOS",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String consultarCodigoSChip(
			String compania)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "'"
		};
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CGR.FC_CODIGOSCHIP",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
	}

	@Override
	public BigDecimal actualizarConfiguracionPptal(
			String compania,
			int anio,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANIO              =>",
				Integer.toString(anio), ", ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		return (BigDecimal) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CGR.FC_ACTUALIZACONFIGPPTAL",
				SysmanFunciones.concatenar(parametros),
				Types.DECIMAL);
	}

	@Override
	public String validaConfPptal(
			String cadena,
			String compania,
			int anio,
			int entidad,
			String regalias,
			String naturaleza,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_CADENA            =>'", cadena, "', ",
				"UN_COMPANIA          =>'", compania, "', ",
				"UN_ANIO              =>",
				Integer.toString(anio), ", ",
				"UN_ENTIDAD           =>",
				Integer.toString(entidad), ", ",
				"UN_REGALIAS          =>'", regalias, "', ",
				"UN_NATURALEZA        =>'", naturaleza, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		try {
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_CGR.FC_VALIDACONFPPTAL",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));

		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public void actualizarCodigoCCEPT(
			String compania,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CGR.PR_ACTUALIZAR_CODIGOCCEPT",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void actCamposCuipo(
			String compania,
			int anio,
			String campo,
			String usuario)
					throws SystemException {
		String[] parametros = {
				"UN_COMPANIA          =>'", compania, "', ",
				"UN_ANIO              =>",
				Integer.toString(anio), ", ",
				"UN_CAMPO             =>'", campo, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CGR.PR_ACT_CAMPOS_CUIPO",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void actualizarCamposCuipoAfect(
			String compania,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CGR.PR_ACTUALIZAR_CAMPOSCUIPOAFECT",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public  void  cargarTipoRecurso(
			String compania, 
			String cadena, 
			String usuario) 
					throws SystemException {
		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
				, "UN_CADENA            =>" , cadena , ", "
				, "UN_USUARIO           =>'" , usuario , "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CGR.PR_CARGAR_TIPO_RECURSO",
				SysmanFunciones.concatenar(parametros));
	}
	/**
	 * Se reemplaza el nombre del metodo original FC_ACTUALIZARCLASIFICADORESPPTALCUIPO por 
	 * FC_ACTCLASIFICADORESPPTALCUIPO para evitar que soprepase los 30 caractereres
	 */
	 @Override
	    public boolean actualizarClasificadoresPptalCuipo(
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
	                        "PCK_CGR.FC_ACTCLASIFICADORESPPTALCUIPO",
	                        SysmanFunciones.concatenar(parametro),
	                        Types.TINYINT);
	        return rta != 0;
	    }
	 /**
	  * Se reemplaza el nombre del metodo original PR_CARGAR_TIPO_CLASIFICADORES_DETALLE por 
	  * PR_CARGAR_TIPO_CLASIFI_DETALLE, para evitar que soprepase los 30 caractereres
	  */
	 @Override
		public void cargarTipoClasificador(String compania, String cadenaplan, String usuario)
				throws SystemException {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
	                				"UN_CADENAPLAN        =>'", cadenaplan, "', ",
	                				"UN_USUARIO           =>'", usuario, "'"
			};
			AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_CGR.PR_CARGAR_TIPO_CLASIFI_DETALLE",
					SysmanFunciones.concatenar(parametros));
			
		}
	 
	 @Override
	 public String actclaarbol(String compania, String cadenaplan, String usuario)
				throws SystemException {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
	                				"UN_CADENAPLAN        =>'", cadenaplan, "', ",
	                				"UN_USUARIO           =>'", usuario, "'"
			};
			try {
				return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
						ConectorPool.ESQUEMA_SYSMAN,
						"PCK_CGR.PR_ACT_CLA_ARBOL",
						SysmanFunciones.concatenar(parametros),
						Types.CLOB));
			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				throw new SystemException(e);
			}
			
		}
	 /**
	  * se crea metodo para actualizar los clasificadores en las apropiaciones iniciales
	  */
	 @Override
	 public String actclaApropiaciones(String compania, String ano, String cadenaplan, String usuario)
				throws SystemException {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
									"UN_ANO               =>'", ano, "', ",
	                				"UN_CADENAPLAN        =>'", cadenaplan, "', ",
	                				"UN_USUARIO           =>'", usuario, "'"
			};
			try {
				return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
						ConectorPool.ESQUEMA_SYSMAN,
						"PCK_CGR.FC_ACT_CLA_APROPIACIONES",
						SysmanFunciones.concatenar(parametros),
						Types.CLOB));
			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				throw new SystemException(e);
			}
			
		}
	 /**
	  * metodo original PL/SQL FC_ACTUALIZAR_CLASIFICADOR_DETALLE, reemplazado por FC_ACT_CLASIFICADOR_DETALLE
	  * es el mismo metodo pero con un nombre ajustado a 30 caracteres
	  */
	 @Override
		public String actualizarTipoClasificador(
				 String compania, String cadenaplan, String usuario)
						throws SystemException {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                    "UN_CADENAPLAN        =>'", cadenaplan, "', ",
                    "UN_USUARIO           =>'", usuario, "'"
			};
			try {
				return Acciones.clobToStringSalto(
						(Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
						"PCK_CGR.FC_ACT_CLASIFICADOR_DETALLE",
						SysmanFunciones.concatenar(parametros),
						Types.CLOB));
			} catch (IOException | SQLException e) {
				throw new SystemException(e);
			}
		}

	 @Override
		public void cargarTablaTempActualizaciones(String compania, String ano, String codigo) throws SystemException {
			AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_CGR.PR_CARGARTABLATEMP_ACT", 
					          "  UN_COMPANIA          => '" + compania + "'"
					        + ", UN_ANO               =>" + ano + " "
							+ ", UN_CODIGO            => '" + codigo + "' ");
		 }
	 @Override
		 public void actualizarTipoClasificadoresAnuevos(String compania, String ano, String codigo) {
			 try {
				AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
							"PCK_CGR.PR_ACT_TIPOCLASIFICADORES", 
							          "  UN_COMPANIA          => '" + compania + "'"
							        + ", UN_ANO               =>" + ano + " "
									+ ", UN_CODIGO            => '" + codigo + "' ");
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 @Override
		public String actualizarTipoClasificadorRubro(
				 String compania,int anio, String naturaleza, String cadenaplan, String usuario)
						throws SystemException {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					 "UN_ANO        =>", Integer.toString(anio) ,"," ,
					 "UN_NATURALEZA        =>'", naturaleza, "', ",
                 "UN_CADENAPLAN        =>'", cadenaplan, "', ",
                 "UN_USUARIO           =>'", usuario, "'"
			};
			try {
				return Acciones.clobToStringSalto(
						(Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
						"PCK_CGR.FC_ACT_CLASIFICADOR_RUBRO",
						SysmanFunciones.concatenar(parametros),
						Types.CLOB));
			} catch (IOException | SQLException e) {
				throw new SystemException(e);
			}
		}
}