package com.sysman.bancoproyectos.ejb.impl;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoLocal;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class BancoProyectoCinco
 */
@Stateless
@LocalBean
public class EjbBancoProyectoCinco implements EjbBancoProyectoCincoRemote,
EjbBancoProyectoCincoLocal {
	/**
	 * Default constructor.
	 */
	public EjbBancoProyectoCinco() {
		// Constructor vacio
	}

	@Override
	public BigDecimal mayorizarAvance(
			String compania,
			int vigencia,
			String metaProdIni,
			String metaProdFin,
			BigDecimal total,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
				"UN_VIGENCIA          =>",
				Integer.toString(vigencia), ", ",
				"UN_META_PROD_INI     =>'", metaProdIni, "', ",
				"UN_META_PROD_FIN     =>'", metaProdFin, "', ",
				"UN_TOTAL             =>", total.toString(),
				", ", "UN_USUARIO       =>'", usuario, "'"
		};
		return (BigDecimal) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_MAYORIZA_AVANCE",
				SysmanFunciones.concatenar(parametros),
				Types.DECIMAL);
	}

	@Override
	public BigDecimal actualizarPlanIndicativo(
			String compania,
			int modulo,
			long novedadInicial,
			long novedadFinal,
			String tipo,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
				"UN_MODULO            =>",
				Integer.toString(modulo), ", ",
				"UN_NOVEDAD_INICIAL   =>",
				Long.toString(novedadInicial), ", ",
				"UN_NOVEDAD_FINAL     =>",
				Long.toString(novedadFinal), ", ",
				"UN_TIPO              =>'", tipo, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		return (BigDecimal) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_ACT_PLAN_INDICADOR",
				SysmanFunciones.concatenar(parametros),
				Types.DECIMAL);
	}

	@Override
	public String genConsecutivoCDP(
			String nombretabla,
			String condicion,
			String nombrecampo,
			String inicial)
					throws SystemException {
		String[] parametros = { "UN_NOMBRETABLA       =>'", nombretabla, "', ",
				"UN_CONDICION         =>'", condicion, "', ",
				"UN_NOMBRECAMPO       =>'", nombrecampo, "', ",
				"UN_INICIAL           =>'", inicial, "'"
		};
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_GENCONSECUTIVOCDP",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
	}

	@Override
	public String getNombreTipoNovedad(
			String compania,
			String strtipot,
			String strclaset)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_STRTIPOT          =>'", strtipot, "', ",
				"UN_STRCLASET         =>'", strclaset, "'"
		};
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_NOM_TIPONOVEDAD",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
	}

	@Override
	public void afectarNovedad(
			String compania,
			String clase,
			String tipo,
			String dependencia,
			long codigo,
			long novedadanterior,
			long novedadafectar,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_CLASE             =>'", clase, "', ",
				"UN_TIPO              =>'", tipo, "', ",
				"UN_DEPENDENCIA       =>'", dependencia, "', ",
				"UN_CODIGO            =>",
				Long.toString(codigo), ", ",
				"UN_NOVEDADANTERIOR   =>",
				Long.toString(novedadanterior), ", ",
				"UN_NOVEDADAFECTAR    =>",
				Long.toString(novedadafectar), ", ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.PR_AFECTAR_SOLICITUD",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public BigDecimal saldoPlanIndicativoMeta(
			String compania,
			String meta,
			BigDecimal vigenciaPlan,
			BigDecimal vigenciaMeta)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_META              =>'", meta, "', ",
				"UN_VIGENCIA_PLAN     =>",
				vigenciaPlan.toString(), ", ",
				"UN_VIGENCIA_META     =>",
				vigenciaMeta.toString()
		};
		return (BigDecimal) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_CANT_PLAN_INDICATIVO",
				SysmanFunciones.concatenar(parametros),
				Types.DECIMAL);
	}

	@Override
	public void crearFichaTecnica(
			String compania,
			String proyecto,
			String sector,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROYECTO          =>'", proyecto, "', ",
				"UN_SECTOR            =>'", sector, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.PR_CREARFICHATECNICA",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public String generarConsultaCriticos(
			String compania,
			int ano)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_ANO               =>",
				Integer.toString(ano), ""
		};
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_GENERAR_CONSULTACRITICA",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
	}

	@Override
	public String generarMetaBruta(
			String compania,
			String idPlan,
			int vigenciaPlan,
			int vigenciaMeta,
			BigDecimal aProgramar)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_IDPLAN            =>'", idPlan, "', ",
				"UN_VIGENCIAPLAN      =>",
				Integer.toString(vigenciaPlan), ", ",
				"UN_VIGENCIAMETA      =>",
				Integer.toString(vigenciaMeta), ", ",
				"UN_APROGRAMAR        =>", aProgramar.toString()
		};
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_GENERARMETABRUTA",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
	}

	@Override
	public void actualizarMetaBruta(
			String compania,
			String idPlan,
			int vigenciaPlan,
			int vigenciaMeta,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_IDPLAN            =>'", idPlan, "', ",
				"UN_VIGENCIAPLAN      =>",
				Integer.toString(vigenciaPlan), ", ",
				"UN_VIGENCIAMETA      =>",
				Integer.toString(vigenciaMeta), ", ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.PR_ACTUALIZARMETABRUTA",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void importarXml(
			String compania,
			String proyectos,
			String productos,
			String actividades,
			boolean asignar,
			String codigoProy,
			String dependencia,
			int vigenciaInicial,
			int vigenciaFinal,
			String usuario,
			String codigoProyBpin)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROYECTOS         =>", proyectos, ", ",
				"UN_PRODUCTOS         =>", productos, ", ",
				"UN_ACTIVIDADES       =>", actividades, ", ",
				"UN_ASIGNAR           =>",
				(asignar ? "-1" : "0"), ", ",
				"UN_CODIGOPROY        =>'", codigoProy, "', ",
				"UN_DEPENDENCIA       =>'", dependencia, "', ",
				"UN_VIGENCIAINICIAL   =>",
				Integer.toString(vigenciaInicial), ", ",
				"UN_VIGENCIAFINAL     =>",
				Integer.toString(vigenciaFinal), ", ",
				"UN_USUARIO           =>'", usuario, "', ",
				"UN_CODIGOPROYBPIN    =>'", codigoProyBpin, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.PR_IMPORTARXML",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public long crearProgramacionProy(
			String compania,
			String vigencia,
			String codigo,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_VIGENCIA          =>'", vigencia, "', ",
				"UN_CODIGO            =>'", codigo, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_CREAR_PROG_PROY",
				SysmanFunciones.concatenar(parametros),
				Types.BIGINT);
	}

	@Override
	public String definirOrden(
			String nombretabla,
			String nombrecampo,
			String campoorden,
			String condicion,
			boolean conAlias)
					throws SystemException {
		String[] parametros = { "UN_NOMBRETABLA       =>'", nombretabla, "', ",
				"UN_NOMBRECAMPO       =>'", nombrecampo, "', ",
				"UN_CAMPOORDEN        =>'", campoorden, "', ",
				"UN_CONDICION         =>'", condicion, "', ",
				"UN_CON_ALIAS         =>",
				conAlias ? "-1" : "0", ""
		};
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_DEFINIRORDEN",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
	}

	@Override
	public String generarProyectosInversion(
			String compania,
			int vigenciaI,
			int vigenciaF)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_VIGENCIA_I        =>",
				Integer.toString(vigenciaI), ", ",
				"UN_VIGENCIA_F        =>",
				Integer.toString(vigenciaF), ""
		};
		try {
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_BANCOS_PROY5.FC_FRMSIRECI238",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String generarInformeEficacia(
			String compania,
			int vigencia)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
				"UN_VIGENCIA          =>",
				Integer.toString(vigencia), ""
		};
		try {
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_BANCOS_PROY5.FC_FRMSIRECIEFICACIA",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public void heredarSolicitudScd(
			String compania,
			String tipotHijo,
			String clasetHijo,
			long novedadHijo,
			String dependenciaHijo,
			String tipot,
			String claset,
			long novedad,
			String dependencia,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_TIPOTHIJO         =>'", tipotHijo, "', ",
				"UN_CLASETHIJO        =>'", clasetHijo, "', ",
				"UN_NOVEDADHIJO       =>",
				Long.toString(novedadHijo), ", ",
				"UN_DEPENDENCIAHIJO   =>'", dependenciaHijo,
				"', ", "UN_TIPOT             =>'", tipot, "', ",
				"UN_CLASET            =>'", claset, "', ",
				"UN_NOVEDAD           =>",
				Long.toString(novedad), ", ",
				"UN_DEPENDENCIA       =>'", dependencia, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.PR_HEREDARSOLICITUDSCD",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void consultadDcumentoAfectar(
			String compania,
			String tipot,
			String claset,
			long documento,
			String dependencia,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_TIPOT             =>'", tipot, "', ",
				"UN_CLASET            =>'", claset, "', ",
				"UN_DOCUMENTO         =>",
				Long.toString(documento), ", ",
				"UN_DEPENDENCIA       =>'", dependencia, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.PR_CONSULTARDOCUMENTOAFECTAR",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public long crearMante(
			String compania,
			String vigencia,
			String codigo,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_VIGENCIA          =>'", vigencia, "', ",
				"UN_CODIGO            =>'", codigo, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_CREAR_MANTENIMIENTO",
				SysmanFunciones.concatenar(parametros),
				Types.BIGINT);
	}

	@Override
	public String verificarSaldoRubro(
			String compania,
			String claset,
			long novedad,
			String dependencia,
			String rubroPresupuestal,
			String proyecto,
			String fuenteRecursos,
			String idMetaProducto,
			Double valorSolicitado,
			long valorDisminuido,
			String centroCosto,
			String referencia,
			String auxiliar)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_CLASET            =>'", claset, "', ",
				"UN_NOVEDAD           =>",
				String.valueOf(novedad),
				", ", "UN_DEPENDENCIA       =>'", dependencia,
				"', ", "UN_RUBROPRESUPUESTAL =>'",
				rubroPresupuestal, "', ",
				"UN_PROYECTO          =>'", proyecto, "', ",
				"UN_FUENTERECURSOS    =>'", fuenteRecursos,
				"', ", "UN_IDMETAPRODUCTO    =>'",
				idMetaProducto, "', ",
				"UN_VALORSOLICITADO   =>",
				String.valueOf(valorSolicitado), ", ",
				"UN_VALORDISMINUIDO   =>",
				String.valueOf(valorDisminuido),
				", ", "UN_CENTRO_COSTO       =>'", centroCosto,
				"', ", "UN_REFERENCIA       =>'", referencia,
				"', ", "UN_AUXILIAR       =>'", auxiliar, "' "
		};
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.FC_VERIFICAR_SALDO_RUBRO",
				SysmanFunciones.concatenar(parametros),
				Types.VARCHAR);
	}

	@Override
	public String prepararDatosF202ProyectosDestinados(
			String compania,
			Date vigenciainicial,
			Date vigenciafinal)
					throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
					"UN_VIGENCIAINICIAL   =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							vigenciainicial),
					"','DD/MM/YYYY'), ",
"UN_VIGENCIAFINAL     =>TO_DATE('",
SysmanFunciones.convertirAFechaCadena(
		vigenciafinal),
"','DD/MM/YYYY')"
			};
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_BANCOS_PROY5.FC_F20_2PROYECTOSDESTINADOS",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (ParseException | IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public  String    validarVoBo(
			String compania, 
			BigInteger novedad, 
			String tipo, 
			String clase, 
			boolean vobo) 
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_NOVEDAD           =>" ,novedad.toString() ,", "
					, "UN_TIPO              =>'" , tipo , "', "
					, "UN_CLASE             =>'" , clase , "', "
					, "UN_VOBO              =>" , (vobo?"-1":"0")  , ""
			};

			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_BANCOS_PROY5.FC_VALIDAR_VOBO",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		} catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
	
	@Override
	public void actualizarMetaProducto(
			String compania,
			int vigenciaPlan,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_VIGENCIAPLAN      =>",
				Integer.toString(vigenciaPlan), ", ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_BANCOS_PROY5.PR_ACTUALIZARMETAPRODUCTO",
				SysmanFunciones.concatenar(parametros));
	}
	
	@Override
    public String cargarPlanIndicativo(
    		String compania,
    		String cadena,
    		String usuario)
    				throws SystemException
    {
    	String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
    			                "UN_CADENA   =>", cadena, ", ",
    			                "UN_USUARIO  =>'", usuario, "'"
    	};
    	try
    	{
    		return Acciones.clobToStringSalto(
    				(Clob) AccionesImp.ejecutarFuncion(
    						ConectorPool.ESQUEMA_SYSMAN,
    						"PCK_BANCOS_PROY5.FC_CARGAR_PLAN_INDICATIVO",
    								SysmanFunciones.concatenar(
    										parametros),
    								Types.CLOB));
    	}
    	catch (IOException | SQLException e)
    	{

    		throw new SystemException(e);
    	}
    }



}