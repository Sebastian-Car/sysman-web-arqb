package com.sysman.recursos.ejb.impl;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilLocal;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

/**
 * Session Bean implementation class EjbSysmanUtil
 */
@Stateless
@LocalBean
public class EjbSysmanUtil implements EjbSysmanUtilRemote, EjbSysmanUtilLocal {
	/**
	 * Default constructor.
	 */
	public EjbSysmanUtil() {
	}

	@Override
	public String consultarParametro(String compania, String nombre, String modulo, Date fechaPar, boolean indMayus)
			throws SystemException {
		try {
			return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_PAR",
					"UN_COMPANIA          =>'" + compania + "', " + "UN_NOMBRE            =>'" + nombre + "', "
							+ "UN_MODULO            =>" + modulo + ", " + "UN_FECHA_PAR         =>TO_DATE('"
							+ SysmanFunciones.convertirAFechaCadena(fechaPar) + "','DD/MM/YYYY HH24:MI:SS'), "
							+ "UN_IND_MAYUS         =>" + (indMayus ? "-1" : "0") + "",
					Types.VARCHAR);
		} catch (ParseException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public int generarDigitoDeVerificacion(String numero) throws SystemException {
		return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_DCH",
				"UN_NUMERO            =>'" + numero + "'", Types.INTEGER);
	}

	@Override
	public int calcularDiferenciaEnMeses(Date fecha1, Date fecha2) throws SystemException {
		try {
			return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_MESESCOMPLETOS",
					"UN_FECHA1            =>TO_DATE('" + SysmanFunciones.convertirAFechaCadena(fecha1)
							+ "','DD/MM/YYYY HH24:MI:SS'), " + "UN_FECHA2            =>TO_DATE('"
							+ SysmanFunciones.convertirAFechaCadena(fecha2) + "','DD/MM/YYYY HH24:MI:SS')",
					Types.INTEGER);
		} catch (ParseException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public int retornarDiasComerciales(Date fechain, Date fechafin) throws SystemException {
		return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL",
				"UN_FECHAIN           =>" + SysmanFunciones.formatearFecha(fechain) + ", " + "UN_FECHAFIN          =>"
						+ SysmanFunciones.formatearFecha(fechafin),
				Types.INTEGER);
	}

	@Override
	public int retornarDiaDeLaSemana(Date fecha, int arranca) throws SystemException {
		return (int) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_WEEKDAY", "UN_FECHA             =>"
						+ SysmanFunciones.formatearFecha(fecha) + ", " + "UN_ARRANCA           =>" + arranca + "",
				Types.INTEGER);
	}

	@Override
	public String convetirValorEnLetras(BigDecimal numt, boolean ctvs) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_VALOR_LETRAS",
				"UN_NUMT              =>" + numt + ", " + "UN_CTVS              =>" + (ctvs ? "-1" : "0") + "",
				Types.VARCHAR);
	}

	@Override
	public String calcularDiferenciaDeTiempo(Date tiempoi, Date tiempof) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_DURACION",
				"UN_TIEMPOI           =>" + SysmanFunciones.formatearFecha(tiempoi) + ", " + "UN_TIEMPOF           =>"
						+ SysmanFunciones.formatearFecha(tiempof),
				Types.VARCHAR);
	}

	@Override
	public long generarSiguienteConsecutivo(String tabla, String criterio, String campo) throws SystemException {
		return generarSiguienteConsecutivo(tabla, criterio, campo, ConectorPool.ESQUEMA_SYSMAN);
	}

	@Override
	public long generarSiguienteConsecutivo(String tabla, String criterio, String campo, String nombreConexion)
			throws SystemException {
		return (long) AccionesImp
				.ejecutarFuncion(nombreConexion, "PCK_SYSMAN_UTL.FC_GENCONSECUTIVO",
						"UN_TABLA             =>'" + tabla + "', " + "UN_CRITERIO          =>'" + criterio + "', "
								+ "UN_CAMPO             =>'" + campo + "', " + "UN_INICIAL           =>NULL",
						Types.BIGINT);
	}

	@Override
	public long generarConsecutivoConValorInicial(String tabla, String criterio, String campo, String inicial)
			throws SystemException {
		String unCriterio = criterio != null ? "'" + criterio + "'" : null;
		return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_GENCONSECUTIVO",
				"UN_TABLA             =>'" + tabla + "', " + "UN_CRITERIO          =>" + unCriterio + ", "
						+ "UN_CAMPO             =>'" + campo + "', " + "UN_INICIAL           =>'" + inicial + "'",
				Types.BIGINT);
	}

	@Override
	public String consultarNombreDeTercero(String compania, String nit, String sucursal) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_NOMTERCERO",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_NIT               =>'" + nit + "', "
						+ "UN_SUCURSAL          =>'" + sucursal + "'",
				Types.VARCHAR);
	}

	@Override
	public String consultarNombreCuentaContable(String compania, int ano, String cuenta) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_NOMBRECUENTA", "UN_COMPANIA          =>'" + compania
						+ "', " + "UN_ANO               =>" + ano + ", " + "UN_CUENTA            =>'" + cuenta + "'",
				Types.VARCHAR);
	}

	@Override
	public String consultarNombreCentroDeCosto(String compania, int ano, String codigo) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_NOMBRECENTROCOSTO",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_CODIGO            =>'" + codigo + "'",
				Types.VARCHAR);
	}

	@Override
	public String consultarNombreAuxiliar(String compania, int ano, String codigo) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_NOMBREAUXILIAR", "UN_COMPANIA          =>'" + compania
						+ "', " + "UN_ANO               =>" + ano + ", " + "UN_CODIGO            =>'" + codigo + "'",
				Types.VARCHAR);
	}

	@Override
	public String mostrarNombreDeMes(int numeroMes) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_NOMBRE_MES",
				"UN_NUMERO_MES        =>" + numeroMes + "", Types.VARCHAR);
	}

	@Override
	public Date retornarFechaMasDiasHabiles(String compania, Date fecha, int dias, boolean sabado)
			throws SystemException {
		return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_FECHAFINALHABIL",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_FECHA             =>"
						+ SysmanFunciones.formatearFecha(fecha) + ", " + "UN_DIAS              =>" + dias + ", "
						+ "UN_SABADO            =>" + (sabado ? "-1" : "0") + "",
				Types.DATE);
	}

	@Override
	public String formatearNitEntidad(String compania, int opcion) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_MINIT",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_OPCION      =>" + opcion + "", Types.VARCHAR);
	}

	@Override
	public boolean consultarDiaFestivo(String compania, Date fecha) throws SystemException {
		byte rta = (byte) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_ESFESTIVO", "UN_COMPANIA          =>'" + compania
						+ "', " + "UN_FECHA             =>" + SysmanFunciones.formatearFecha(fecha) + ", ",
				Types.TINYINT);
		return rta != 0;
	}

	@Override
	public int retornarDiasHabilesEntreFechas(String compania, Date fechaini, Date fechafin, boolean sabados)
			throws SystemException {
		return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_DIASHABIL",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_FECHAINI          =>"
						+ SysmanFunciones.formatearFecha(fechaini) + ", " + "UN_FECHAFIN          =>"
						+ SysmanFunciones.formatearFecha(fechafin) + ", " + "UN_SABADOS           =>"
						+ (sabados ? "-1" : "0") + "",
				Types.INTEGER);
	}

	@Override
	public String consutarNombreCompletoDeTerceroConParametro(String compania, String nombre1, String nombre2,
			String apellido1, String apellido2) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_NOMBRECOMPLETO",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_NOMBRE1           =>'" + nombre1 + "', "
						+ "UN_NOMBRE2           =>'" + nombre2 + "', " + "UN_APELLIDO1         =>'" + apellido1 + "', "
						+ "UN_APELLIDO2         =>'" + apellido2 + "'",
				Types.VARCHAR);
	}

	@Override
	public String crearCodigoPresupuestal(String compania, int ano, String cuenta, String centroCosto, String tercero,
			String sucursal, String auxiliar, String referencia, String fuente) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_CODIGO_PPTAL",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_CUENTA            =>'" + cuenta + "', " + "UN_CENTRO_COSTO      =>'" + centroCosto + "', "
						+ "UN_TERCERO           =>'" + tercero + "', " + "UN_SUCURSAL          =>'" + sucursal + "', "
						+ "UN_AUXILIAR          =>'" + auxiliar + "', " + "UN_REFERENCIA        =>'" + referencia
						+ "', " + "UN_FUENTE            =>'" + fuente + "'",
				Types.VARCHAR);
	}

	@Override
	public String crearCodigoContable(String compania, int ano, String cuenta, String centroCosto, String tercero,
			String sucursal, String auxiliar, String referencia, String fuente) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_CODIGO_CNT",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_CUENTA            =>'" + cuenta + "', " + "UN_CENTRO_COSTO      =>'" + centroCosto + "', "
						+ "UN_TERCERO           =>'" + tercero + "', " + "UN_SUCURSAL          =>'" + sucursal + "', "
						+ "UN_AUXILIAR          =>'" + auxiliar + "', " + "UN_REFERENCIA        =>'" + referencia
						+ "', " + "UN_FUENTE            =>'" + fuente + "'",
				Types.VARCHAR);
	}

	@Override
	public String calcularDiferenciaEntreFechas(Date fechaini, Date fechafin, int formato, int edadpersona)
			throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_EDAD",
				"UN_FECHAINI          =>" + SysmanFunciones.formatearFecha(fechaini) + ", " + "UN_FECHAFIN          =>"
						+ SysmanFunciones.formatearFecha(fechafin) + ", " + "UN_FORMATO           =>" + formato + ", "
						+ "UN_EDADPERSONA       =>" + edadpersona + "",
				Types.VARCHAR);
	}

	@Override
	public int calcularDiferenciaEnDias(Date fechaini, Date fechafin) throws SystemException {
		String respuesta = (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_EDAD",
				"UN_FECHAINI          =>" + SysmanFunciones.formatearFecha(fechaini) + ", " + "UN_FECHAFIN          =>"
						+ SysmanFunciones.formatearFecha(fechafin) + ", " + "UN_FORMATO           =>" + 3 + "",
				Types.VARCHAR);

		return Integer.parseInt(respuesta);

	}

	@Override
	public int calcularEdadDelPersonal(Date fecha) throws SystemException {
		return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_EDAD",
				"UN_FECHA             =>" + SysmanFunciones.formatearFecha(fecha), Types.INTEGER);
	}

	@Override
	public String generarValorDeCamposAuxiliaresPresupuestales(String compania, int ano, String cuenta, String campo,
			int valor) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_GEN_AUXILIARPPTAL_VIRTUAL",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_CUENTA            =>'" + cuenta + "', " + "UN_CAMPO             =>'" + campo + "', "
						+ "UN_VALOR             =>" + valor + "",
				Types.VARCHAR);
	}

	@Override
	public String generarValorDeCamposAuxiliaresContables(String compania, int ano, String cuenta, String campo,
			int valor) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_CUENTA            =>'" + cuenta + "', " + "UN_CAMPO             =>'" + campo + "', "
						+ "UN_VALOR             =>" + valor + "",
				Types.VARCHAR);
	}

	@Override
	public Date fechaFinalMasDiasComerciales(Date fechainicial, int dias, boolean mescomercial) throws SystemException {
		return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_FECHAFINAL",
				"UN_FECHAINICIAL      =>" + SysmanFunciones.formatearFecha(fechainicial) + ","
						+ "UN_DIAS              =>" + dias + ", " + "UN_MESCOMERCIAL      =>"
						+ (mescomercial ? "-1" : "0") + "",
				Types.DATE);
	}

	@Override
	public String consutarNombreCompletoDeTercero(String nombre1, String nombre2, String apellido1, String apellido2)
			throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_NOMBRECOMPLETO",
				"UN_NOMBRE1           =>'" + nombre1 + "', " + "UN_NOMBRE2           =>'" + nombre2 + "', "
						+ "UN_APELLIDO1         =>'" + apellido1 + "', " + "UN_APELLIDO2         =>'" + apellido2 + "'",
				Types.VARCHAR);
	}

	@Override
	public Date fechaFinalMasDiasComercialesTT(Date fechainicial, int dias, int mescomercial) throws SystemException {
		return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_FECHAFINALNOV",
				"UN_FECHAINICIAL      =>" + SysmanFunciones.formatearFecha(fechainicial) + ", "
						+ "UN_DIAS              =>" + dias + ", " + "UN_MESCOMERCIAL      =>" + mescomercial + "",
				Types.DATE);
	}

	@Override
	public String verificarEstadoPeriodoAnual(String compania, int ano, int modulo, int proceso)
			throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_MODULO            =>" + modulo + ", " + "UN_PROCESO           =>" + proceso + "",
				Types.VARCHAR);
	}
	
	@Override
	public String verificarEstadoPeriodoAnual(String compania, String ano, String modulo, String proceso)
			throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_MODULO            =>" + modulo + ", " + "UN_PROCESO           =>" + proceso + "",
				Types.VARCHAR);
	}

	@Override
	public String verificarEstadoPeriodoMensual(String compania, int ano, int mes, int modulo, int proceso)
			throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_MES               =>" + mes + ", " + "UN_MODULO            =>" + modulo + ", "
						+ "UN_PROCESO           =>" + proceso + "",
				Types.VARCHAR);
	}

	@Override
	public String verificarEstadoDiario(String compania, int ano, int mes, int dia, int modulo, int proceso)
			throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_MES               =>" + mes + ", " + "UN_DIA               =>" + dia + ", "
						+ "UN_MODULO            =>" + modulo + ", " + "UN_PROCESO           =>" + proceso + "",
				Types.VARCHAR);
	}

	@Override
	public String generarCuentaRecibiendoAuxiliares(String compania, int ano, String cuenta, String centroCosto,
			String tercero, String sucursal, String auxiliar, String referencia, String fuente) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_CODIGO_CNT_CCYOPAL",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_CUENTA            =>'" + cuenta + "', " + "UN_CENTRO_COSTO      =>'" + centroCosto + "', "
						+ "UN_TERCERO           =>'" + tercero + "', " + "UN_SUCURSAL          =>'" + sucursal + "', "
						+ "UN_AUXILIAR          =>'" + auxiliar + "', " + "UN_REFERENCIA        =>'" + referencia
						+ "', " + "UN_FUENTE            =>'" + fuente + "'",
				Types.VARCHAR);
	}

	@Override
	public String retornarNombreCardinal(int numero, int tipo) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_NOCARDINALES",
				"UN_NUMERO            =>" + numero + ", " + "UN_TIPO              =>" + tipo + "", Types.VARCHAR);
	}

	@Override
	public Date sumarDiasFecha(Date fecha, int dias) throws SystemException {
		return (Date) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_SUMARDIAS_FECHA", "UN_FECHA             =>"
						+ SysmanFunciones.formatearFecha(fecha) + ", " + "UN_DIAS              =>" + dias + "",
				Types.DATE);
	}

	@Override
	public Boolean insertarDiasdelMes(String compania, int ano, int mes, String creador) throws SystemException {
		byte rta = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_INSERTARDIASDELMES",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANIO              =>" + ano + ", "
						+ "UN_MES               =>" + mes + ", " + "UN_CREATED_BY        =>'" + creador + "'",
				Types.TINYINT);
		return rta != 0;

	}

	@Override
	public Boolean insertarMesesdelAno(String compania, int ano, String creador) throws SystemException {
		byte rta = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_INSERTARMESESDELANO", "UN_COMPANIA          =>'" + compania + "', "
						+ "UN_ANIO              =>" + ano + ", " + "UN_CREATED_BY        =>'" + creador + "'",
				Types.TINYINT);
		return rta != 0;
	}

	@Override
	public Boolean cambiarEstadoMes(String compania, int ano, int mes, int modulo, int proceso, String estado,
			String modificador) throws SystemException {
		byte rta = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_CAMBIARESTADOMES",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_MES               =>" + mes + ", " + "UN_MODULO            =>" + modulo + ", "
						+ "UN_PROCESO           =>" + proceso + ", " + "UN_ESTADO            =>'" + estado + "', "
						+ "UN_MODIFIED_BY       =>'" + modificador + "'",
				Types.TINYINT);
		return rta != 0;

	}

	@Override
	public Boolean cambiarEstadoAno(String compania, int ano, int modulo, int proceso, String estado,
			String modificador) throws SystemException {
		byte rta = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_CAMBIARESTADOANO",
				"UN_COMPANIA          =>'" + compania + "', " + "UN_ANO               =>" + ano + ", "
						+ "UN_MODULO            =>" + modulo + ", " + "UN_PROCESO           =>" + proceso + ", "
						+ "UN_ESTADO            =>'" + estado + "', " + "UN_MODIFIED_BY       =>'" + modificador + "'",
				Types.TINYINT);
		return rta != 0;

	}

	@Override
	public String verificarConsultaPlantilla(String consulta) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_VERIFICARCONSULTAPLANTILLA", "UN_CONSULTA          =>'" + consulta + "'",
				Types.VARCHAR);
	}

	@Override
	public void anexarDatosEnTabla(String tabla, String excluidos, String valorNuevo, String baseExcluido,
			String condicion) throws SystemException {
		String unCondicion = condicion.isEmpty() ? null : "'" + condicion + "'";
		String[] parametros = { "UN_TABLA             =>'", tabla, "', ", "UN_EXCLUIDOS         =>'", excluidos, "', ",
				"UN_VALOR_NUEVO       =>'", valorNuevo, "', ", "UN_BASE_EXCLUIDO     =>'", baseExcluido, "', ",
				"UN_CONDICION         => ", unCondicion };
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public String generarCerosIzquierda(long numero, long longitud) throws SystemException {
		String[] parametros = { "UN_NUMERO            =>", Long.toString(numero), ", ", "UN_LONGITUD          =>",
				Long.toString(longitud) };
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_STRZERO",
				SysmanFunciones.concatenar(parametros), Types.VARCHAR);
	}

	@Override
	public BigDecimal fix(BigDecimal valor) throws SystemException {
		String[] parametros = { "UN_VALOR             =>", valor.toString() };
		return (BigDecimal) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_FIX",
				SysmanFunciones.concatenar(parametros), Types.DECIMAL);
	}

	@Override
	public int retornarDiasHabilesViaticos(String compania, Date fechaIni, Date fechaFin, boolean sabados,
			boolean domingos, boolean festivos) throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_FECHAINI          =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(fechaIni), "','DD/MM/YYYY'), ",
					"UN_FECHAFIN          =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaFin),
					"','DD/MM/YYYY'), ", "UN_SABADOS           =>", sabados ? "-1" : "0", ", ",
					"UN_DOMINGOS          =>", domingos ? "-1" : "0", ", ", "UN_FESTIVOS          =>",
					festivos ? "-1" : "0" };
			return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS",
					SysmanFunciones.concatenar(parametros), Types.INTEGER);
		} catch (ParseException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public BigDecimal retornarDiasPernoctando(String compania, boolean perNoctando, Date fechaInicio, Date fechaFin,
			boolean sabado, boolean domingo, boolean festivo) throws SystemException {
		try {
			String[] parametros = { "UN_COMPANIA          =>'", compania, "', ", "UN_PERNOCTANDO       =>'",
					perNoctando ? "-1" : "0", "', ", "UN_FECHAINICIO       =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(fechaInicio), "','DD/MM/YYYY'), ",
					"UN_FECHAFIN          =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaFin),
					"','DD/MM/YYYY'), ", "UN_SABADO            =>", sabado ? "-1" : "0", ", ",
					"UN_DOMINGO           =>", domingo ? "-1" : "0", ", ", "UN_FESTIVO           =>",
					festivo ? "-1" : "0", "" };
			return (BigDecimal) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_SYSMAN_UTL.FC_DIASPERNOCTANDO", SysmanFunciones.concatenar(parametros), Types.DECIMAL);
		} catch (ParseException e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String detectarCampos(String sql) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_DETECTARCAMPOS",
				"UN_SQL          =>'" + sql + "'", Types.VARCHAR);

	}
	
	@Override
	public int consultarModeloAno(String compania, String ano ) throws SystemException {
		return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_CONSULTARMODELOANO", "UN_COMPANIA          =>'" + compania
				+ "', " + "UN_ANO               =>" + ano + "", Types.INTEGER);

	}
	
	@Override
	public int aplicacionCuenta(String compania, String ano, String codigo, String cuenta ) throws SystemException {
		return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_APLICACIONCUENTA", 
				          "  UN_COMPANIA          => '" + compania + "'"
				        + ", UN_ANO               =>" + ano + " "
						+ ", UN_CODIGO            => '" + codigo + "' "
						+ ", UN_CUENTA            =>'" + cuenta + "'", Types.INTEGER);

	}
	
	@Override
	public String cuentaClasificador(String compania, String ano, String cuenta, String clase ) throws SystemException {
		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_SYSMAN_UTL.FC_CUENTACLASIFICADOR", "UN_COMPANIA          =>'" + compania
				+ "', " + "UN_ANO               =>" + ano + " , UN_CUENTA =>'" + compania
				+ "', UN_CLASE =>' " + clase + "'", Types.VARCHAR);

	}
	
	@Override
	public String consultarParametroMarcaBlanca( String nombre)
			throws SystemException {
		
			return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_PAR_MARCA_BLANCA",
					"UN_NOMBRE            =>'" + nombre + "') ",Types.VARCHAR);
		
	}
	
	 @Override
	 public String copiarPlantilla(String plantillaOrigen, String plantillaDestino,  Date fecha, String nombre, int tipo, String nombrePlantilla) throws SystemException {
		 try  {   
		 String[] parametros={ "UN_PLANTILLA_ORIGEN   =>'", plantillaOrigen, "', ",
	                                "UN_PLANTILLA_DESTINO    =>'", plantillaDestino, "', ",
	                                "UN_FECHA_ORIGEN    =>'", SysmanFunciones.convertirAFechaCadena(fecha),"', ",
	                                "UN_NOMBRE      => '", nombre ,"',",
	                                "UN_TIPO 		=> ", Integer.toString(tipo) ,",",
	                                "UN_PLANTILLA    => '", nombrePlantilla ,"'" };
	        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMANK, "PCK_SYSMAN_UTL.FC_COPIAR_PLANTILLA",
					SysmanFunciones.concatenar(parametros), Types.VARCHAR);
		 } catch (ParseException e){
				throw new SystemException(e);
	    }
	 }
	 
	 @Override
		public String calcularDiferenciaEntreFechasVac(Date fechaini, Date fechafin, int formato, int edadpersona)
				throws SystemException {
			return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_EDAD_VAC",
					"UN_FECHAINI          =>" + SysmanFunciones.formatearFecha(fechaini) + ", " + "UN_FECHAFIN          =>"
							+ SysmanFunciones.formatearFecha(fechafin) + ", " + "UN_FORMATO           =>" + formato + ", "
							+ "UN_EDADPERSONA       =>" + edadpersona + "",
					Types.VARCHAR);
		}
	 
	 @Override
		public int calcularDiferenciaEntreFechasInc(Date fechaini, Date fechafin, int formato)
				throws SystemException {
		 String respuesta = (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_SYSMAN_UTL.FC_EDAD_INC",
					"UN_FECHAINI          =>" + SysmanFunciones.formatearFecha(fechaini) + ", " + "UN_FECHAFIN          =>"
							+ SysmanFunciones.formatearFecha(fechafin) + ", " + "UN_FORMATO           =>" + formato + "",
					Types.VARCHAR);

			return Integer.parseInt(respuesta);
		}
}