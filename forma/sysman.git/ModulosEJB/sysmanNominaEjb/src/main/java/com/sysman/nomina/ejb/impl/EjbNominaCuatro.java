package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaCuatroLocal;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
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
 * Session Bean implementation class NominaCuatro
 */
@Stateless
@LocalBean
public class EjbNominaCuatro
implements EjbNominaCuatroRemote, EjbNominaCuatroLocal {
	/**
	 * Default constructor.
	 */
	public EjbNominaCuatro() {
	}

	@Override
	public boolean borrarEmbargos(
			int proceso,
			int mes,
			int anio,
			int periodo,
			String compania)
					throws SystemException {
		byte salida;
		String[] parametros = { "UN_PROCESO           =>",
				Integer.toString(proceso), ", ",
				"UN_MES               =>",
				Integer.toString(mes), ", ",
				"UN_ANIO              =>",
				Integer.toString(anio), ", ",
				"UN_PERIODO           =>",
				Integer.toString(periodo), ", ",
				"UN_COMPANIA          =>'", compania, "'"
		};
		salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_NOMINA_COM4.FC_BORRAREMBARGOS",
				SysmanFunciones.concatenar(parametros),
				Types.TINYINT);
		return salida == 0 ? false : true;
	}

	@Override
	public String nombreJuzgado(
			String compania,
			String idjuzgado)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_IDJUZGADO         =>'", idjuzgado, "'"
		};
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_NOMINA_COM4.FC_NOMJUZGADO",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
	}

	@Override
	public int conceptoEquivalente(
			String compania,
			String idDeConcepto)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ID_DE_CONCEPTO    =>'", idDeConcepto, "'"
		};
		return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_NOMINA_COM4.FC_CONCEPTOEQUIVALENTE",
				SysmanFunciones.concatenar(parametros),
				Types.INTEGER);
	}

	@Override
	public boolean subirNovedadExcel(
			String compania,
			int proceso,
			int ano,
			int mes,
			int concepto,
			int codigo,
			BigDecimal valor,
			int periodo,
			String tipo,
			String usuario,
			int nsiaue)
					throws SystemException {
		byte salida;
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROCESO           =>",
				Integer.toString(proceso), ", ",
				"UN_ANO               =>",
				Integer.toString(ano), ", ",
				"UN_MES               =>",
				Integer.toString(mes), ", ",
				"UN_CONCEPTO          =>",
				Integer.toString(concepto), ", ",
				"UN_CODIGO            =>",
				Integer.toString(codigo), ", ",
				"UN_VALOR             =>", valor.toString(),
				", ",
				"UN_PERIODO           =>",
				Integer.toString(periodo), ", ",
				"UN_TIPO              =>'", tipo, "', ",
				"UN_USUARIO           =>'", usuario, "',",
				"UN_NSIAUE              =>", Integer.toString(nsiaue)," "
		};
		salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_NOMINA_COM4.FC_SUBIRNOVEDADEXCEL",
				SysmanFunciones.concatenar(parametros),
				Types.TINYINT);
		return salida == 0 ? false : true;
	}

	@Override
	public void crearNovedadesActuales(
			String compania,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_NOMINA_COM4.PR_CREAR_FONDOS_ACTUALES2",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public String discoBancoAgrario(
			String compania,
			int proceso,
			int anio,
			int mes,
			int periodo,
			String banco,
			Date fechaReporte,
			int oficinaOrigen)
					throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_PROCESO           =>",
					Integer.toString(proceso), ", ",
					"UN_ANIO              =>",
					Integer.toString(anio), ", ",
					"UN_MES               =>",
					Integer.toString(mes), ", ",
					"UN_PERIODO           =>",
					Integer.toString(periodo), ", ",
					"UN_BANCO             =>'", banco, "', ",
					"UN_FECHAREPORTE      =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							fechaReporte),
					"','DD/MM/YYYY'), ",
					"UN_OFICINAORIGEN     =>",
					Integer.toString(oficinaOrigen), ""
			};
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_NOMINA_COM4.FC_DISCOBANCOAGRARIO",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarDiscoBancoColombia(
			String compania,
			int proceso,
			int anio,
			int mes,
			int periodo,
			String banco,
			Date fechareporte,
			boolean todoslosbancos,
			String observacion,
			String lote,
			int informe,
			String tcuentabanorigen,
			String cuentabanorigen)
					throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_PROCESO           =>",
					Integer.toString(proceso), ", ",
					"UN_ANIO              =>",
					Integer.toString(anio), ", ",
					"UN_MES               =>",
					Integer.toString(mes), ", ",
					"UN_PERIODO           =>",
					Integer.toString(periodo), ", ",
					"UN_BANCO             =>'", banco, "', ",
					"UN_FECHAREPORTE      =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							fechareporte),
					"','DD/MM/YYYY'), ",
					"UN_TODOSLOSBANCOS    =>",
					todoslosbancos ? "-1" : "0", ", ",
							"UN_OBSERVACION       =>'", observacion,
							"', ",
							"UN_LOTE              =>'", lote, "', ",
							"UN_INFORME           =>",
							Integer.toString(informe), ", ",
							"UN_TCUENTABANORIGEN  =>'",
							tcuentabanorigen, "', ",
							"UN_CUENTABANORIGEN   =>'", cuentabanorigen,
							"'"
			};
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_NOMINA_COM4.FC_DISCOBANCOLOMBIA",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
	@Override
	public String generarDiscoBancoColombiaGn(
			String compania,
			int proceso,
			int anio,
			int mes,
			int periodo,
			String banco,
			Date fechareporte,
			boolean todoslosbancos,
			String observacion,
			String lote,
			String desPago,
			String referencia,
			int informe,
			String tcuentabanorigen,
			String cuentabanorigen)
					throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_PROCESO           =>",
					Integer.toString(proceso), ", ",
					"UN_ANIO              =>",
					Integer.toString(anio), ", ",
					"UN_MES               =>",
					Integer.toString(mes), ", ",
					"UN_PERIODO           =>",
					Integer.toString(periodo), ", ",
					"UN_BANCO             =>'", banco, "', ",
					"UN_FECHAREPORTE      =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							fechareporte),
					"','DD/MM/YYYY'), ",
					"UN_TODOSLOSBANCOS    =>",
					todoslosbancos ? "-1" : "0", ", ",
							"UN_OBSERVACION       =>'", observacion,
							"', ",
							"UN_LOTE              =>'", lote, "', ",
							"UN_DESPAGO              =>'", desPago, "', ",
							"UN_REFERENCIA              =>'", referencia, "', ",
							"UN_INFORME           =>",
							Integer.toString(informe), ", ",
							"UN_TCUENTABANORIGEN  =>'",
							tcuentabanorigen, "', ",
							"UN_CUENTABANORIGEN   =>'", cuentabanorigen,
							"'"
			};
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_NOMINA_COM4.FC_DISCOBANCOLOMBIAGN",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}


	@Override
	public String generarDiscoBancoPopular(
			String compania,
			int proceso,
			int anio,
			int mes,
			int periodo,
			String banco,
			boolean todoslosbancos,
			Date fechaReporte,
			String observacion)
					throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_PROCESO           =>",
					Integer.toString(proceso), ", ",
					"UN_ANIO              =>",
					Integer.toString(anio), ", ",
					"UN_MES               =>",
					Integer.toString(mes), ", ",
					"UN_PERIODO           =>",
					Integer.toString(periodo), ", ",
					"UN_BANCO             =>'", banco, "', ",
					"UN_FECHAREPORTE      =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							fechaReporte),
					"','DD/MM/YYYY'), ",
					"UN_OBSERVACION       =>'", observacion, "'"
			};
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_NOMINA_COM4.FC_DISCOBANCOPOPULAR",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}


	@Override
	public  String generarDiscoBancolombiaIdipron(
			String compania, 
			int proceso,  
			int periodo, 
			int mes, 
			int ano, 
			Date fechaemision, 
			Date fechatransaccion, 
			String observacion, 
			String secuencialote, 
			String banco, 
			boolean todoslosbancos) 
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
					, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
					, "UN_MES               =>" , Integer.toString(mes) , ", "
					, "UN_ANO               =>" , Integer.toString(ano) , ", "
					, "UN_FECHAEMISION      =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechaemision) , "','DD/MM/YYYY'), "
					, "UN_FECHATRANSACCION  =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechatransaccion) , "','DD/MM/YYYY'), "
					, "UN_OBSERVACION       =>'" , observacion , "', "
					, "UN_SECUENCIALOTE     =>'" , secuencialote , "', "
					, "UN_BANCO             =>'" , banco , "', "
					, "UN_TODOSLOSBANCOS    =>" , (todoslosbancos?"-1":"0")  , ""
			};
			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM4.FC_DISCOBANCOLOMBIAIDI",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public  String generarDiscobbva_Cash(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int ano, 
			Date fechaReporte, 
			String banco) 
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
					, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
					, "UN_MES               =>" , Integer.toString(mes) , ", "
					, "UN_ANO               =>" , Integer.toString(ano) , ", "
					, "UN_FECHAEMISION      =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechaReporte) , "','DD/MM/YYYY'), "
					, "UN_BANCO             =>'" , banco , "'"
			};
			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM4.FC_DISCOBBVA_CASH_DUITAMA",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
	@Override
	public  String generarDiscoCajaSocialDuitama(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int ano,  
			Date fechaReporte, 
			String concpago, 
			String banco) 
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
					, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
					, "UN_MES               =>" , Integer.toString(mes) , ", "
					, "UN_ANO               =>" , Integer.toString(ano) , ", "
					, "UN_FECHAEMISION      =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechaReporte) , "','DD/MM/YYYY'), "
					, "UN_CONCPAGO          =>'" , concpago , "', "
					, "UN_BANCO             =>'" , banco , "'"
			};
			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM4.FC_DISCOCAJASOCIAL_DUITAMA",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public  String generarDiscoDaviviendaIdi(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int ano, 
			Date fechaproceso,
			Date fechasistema,
			String banco,
			boolean todosLosBancos) 
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
					, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
					, "UN_MES               =>" , Integer.toString(mes) , ", "
					, "UN_ANO               =>" , Integer.toString(ano) , ", "
					, "UN_FECHAPROCESO      =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechaproceso) , "','DD/MM/YYYY'),"
					, "UN_FECHASISTEMA      =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechasistema) , "','DD/MM/YYYY'),"
					, "UN_BANCO             =>'" , banco , "', "
					, "UN_TODOSLOSBANCOS    =>" , todosLosBancos ? "-1" : "0"					
			};
			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM4.FC_DISCODAVIVIENDAIDI",
					SysmanFunciones.concatenar(parametros), Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public  String  discoBancoAgrarioGobNarino(
			String compania, 
			int proceso, 
			int anio, 
			int mes, 
			int periodo, 
			String banco, 
			Date fechareporte, 
			int oficinaorigen) 
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
					, "UN_ANIO              =>" , Integer.toString(anio) , ", "
					, "UN_MES               =>" , Integer.toString(mes) , ", "
					, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
					, "UN_BANCO             =>'" , banco , "', "
					, "UN_FECHAREPORTE      =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechareporte) , "','DD/MM/YYYY'), "
					, "UN_OFICINAORIGEN     =>" , Integer.toString(oficinaorigen) , ""
			};
			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM4.FC_DISCOBANCOAGRARIOGOBNARINO",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (IOException | SQLException | ParseException e) {
			throw new SystemException(e);
		}
	}



	@Override
	public String generarDiscoTodosLosBancos(
			String compania,
			int proceso,
			int anio,
			int mes,
			int periodo,
			String banco,
			boolean todoslosbancos,
			Date fechaReporte,
			String observacion,
	        String tcuentabanorigen,
	        String cuentabanorigen)
					throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_PROCESO           =>",
					Integer.toString(proceso), ", ",
					"UN_ANIO              =>",
					Integer.toString(anio), ", ",
					"UN_MES               =>",
					Integer.toString(mes), ", ",
					"UN_PERIODO           =>",
					Integer.toString(periodo), ", ",
					"UN_BANCO             =>'", banco, "', ",
					"UN_FECHAREPORTE      =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							fechaReporte),
					"','DD/MM/YYYY'), ",
					"UN_TODOSLOSBANCOS    =>" ,todoslosbancos?"-1":"0" , " , ",
					"UN_OBSERVACION       =>'", observacion, " ', ",
					"UN_TCUENTABANORIGEN  =>'",tcuentabanorigen, "', ",
					"UN_CUENTABANORIGEN   =>'", cuentabanorigen, "'"
					
			};
			return Acciones.clobToStringSalto(
					(Clob) AccionesImp.ejecutarFuncion(
							ConectorPool.ESQUEMA_SYSMAN,
							"PCK_NOMINA_COM4.FC_DISCOBANCOPOPULARGEN",
							SysmanFunciones.concatenar(
									parametros),
							Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
	
	@Override
	public  String generarDaviviendaExcel(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int ano) 
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
					, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
					, "UN_MES               =>" , Integer.toString(mes) , ", "
					, "UN_ANO               =>" , Integer.toString(ano)				
			};
			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM4.FC_GENERARDAVIVIENDAEXCEL",
					SysmanFunciones.concatenar(parametros), Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
	
	@Override
	public String cargarDatosReforma(
	        String compania,
	        String cadena,
	        String usuario) throws SystemException {
	    try {
	        String[] parametros = {
	            "UN_COMPANIA =>'", compania, "', ",
	            "UN_CADENA   =>", cadena, ", ",
	            "UN_USUARIO  =>'", usuario, "'"
	        };
	        return Acciones.clobToStringSalto((Clob)  AccionesImp.ejecutarFuncion(
	                ConectorPool.ESQUEMA_SYSMAN,
	                "PCK_NOMINA_COM4.FC_DATOS_REFORMA_PENS",
	                SysmanFunciones.concatenar(parametros),
	                Types.CLOB));
	    } catch (SQLException | IOException e) {
	        throw new SystemException(e);
	    }
	}

	
}