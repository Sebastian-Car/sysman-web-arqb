/*-
 * verconciliacionbancariasControlador.java
 *
 * 1.0
 * 
 * 07/09/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ImpConciliacionControladorUrlEnum;
import com.sysman.contabilidad.enums.verconciliacionbancariasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 07/09/2022
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class verconciliacionbancariasControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private String compania;
	private String nombreCompania;
	private String cuenta;
	private int ano;
	private int mes;
	private String estadoAnio;
	private StreamedContent archivoDescarga;
	private List<Registro> listaAno;
	private RegistroDataModelImpl listaCuenta;
//</DECLARAR_LISTAS_COMBO_GRANDE>

	// new
	private Date fechaInicial;
	private Date fechaFinal;
	private BigDecimal saldoExtracto;
	private Map<String, Object> parametro;
	private String manejaPartidasConci;
	private String manejaConciAnterior;
	private final String cStrTipos;
	private final String cCondicion;
	private final String cTotal;
	//

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de verconciliacionbancariasControlador
	 */
	public verconciliacionbancariasControlador() {

		compania = SessionUtil.getCompania();
		nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
		cTotal = "TOTAL";
		cStrTipos = "strTipos";
		cCondicion = "condicion";
		try {
			numFormulario = GeneralCodigoFormaEnum.VER_CONCILIACION_BANCARIAS_CONTROLADOR.getCodigo();

			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(verconciliacionbancariasControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		saldoExtracto = traerSaldoExtracto(mes, ano);
		parametro = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH) + 1;
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		cargarListaAno();
		cargarListaCuenta();
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											verconciliacionbancariasControladorUrlEnum.URL001.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCuenta
	 *
	 */
	public void cargarListaCuenta() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(verconciliacionbancariasControladorUrlEnum.URL003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), Integer.toString(ano));
		listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>

	private String valorParametro(String nombre) {
		try {
			return ejbSysmanUtil.consultarParametro(compania, nombre, SessionUtil.getModulo(), new Date(), false);

		} catch (SystemException e) {
			Logger.getLogger(ImpConciliacionControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return null;
		}
	}

	private void cargarIntervaloFechas() {
		Calendar fecha = Calendar.getInstance();
		fecha.set(ano, mes - 1, 1);
		fechaInicial = fecha.getTime();
		try {
			fechaFinal = SysmanFunciones.ultimoDiaDate(fechaInicial);
		} catch (ParseException ex) {
			Logger.getLogger(verconciliacionbancariasControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void actualizarAjusteMes() {
		try {
			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put("cuenta", cuenta);
			param.put("MES", mes);
			param.put("SALDOEXTRACTO", saldoExtracto);
			param.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			Parameter parameter = new Parameter();

			parameter.setFields(param);

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ImpConciliacionControladorUrlEnum.URL26660.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException ex) {
			Logger.getLogger(ImpConciliacionControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(idioma.getString("MSM_TRANS_INTERRUMPIDA") + " " + ex);
		}
	}

	private BigDecimal traerSaldoExtracto(int mes, int ano) {
		BigDecimal saldo = BigDecimal.ZERO;
		try {
			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put("MES", mes);
			param.put("CODCUENTA", cuenta);

			Registro reg = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ImpConciliacionControladorUrlEnum.URL69696.getValue())
											.getUrl(),
									param));
			if (reg == null) {
				return saldo;
			} else {
				saldo = new BigDecimal(reg.getCampos().get("AJUSTE").toString());
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return saldo;
	}

	public void cargarParametros() {
				
		manejaConciAnterior = valorParametro("MANEJA CONCILIACION ANTERIOR");
		manejaPartidasConci = valorParametro("MANEJA INGRESO DE PARTIDAS CONCILIATORIAS");
			String elaboroConciliacion = valorParametro("ELABORO EN CONCILIACION");
		String revisoConcilicacion = valorParametro("REVISO EN CONCILIACION");
		String cargoEncargado = valorParametro("CARGO ENCARGADO DE TESORERIA");
		String nombreEncargado = valorParametro("NOMBRE ENCARGADO DE TESORERIA");
		String cargoJefeConta = valorParametro("CARGO DE JEFE DE CONTABILIDAD");
		String nombreJefeConta = valorParametro("NOMBRE DE JEFE DE CONTABILIDAD");
		String nombreJefeDiv = valorParametro("NOMBRE JEFE DIVISION ADMINISTRATIVA");
		String cargoJefeFinan = valorParametro("CARGO JEFE FINANCIERO");
		String chequesinCobrar = valorParametro("CONCILIACION BANCARIA, CHEQUE SIN COBRAR");
		String consignacionTransito = valorParametro("CONCILIACION BANCARIA, CONSIGNACION DE TRANSITO");		
		String formatoEspConci = valorParametro("FORMATO ESPECIAL DE CONCILIACION");
		String tituloConciBancaria = valorParametro("TITULO Y ELABORO ESPECIAL EN CONCILIACION BANCARIA");
		String modConciBancaria = valorParametro("MODIFICACION DE PALABRAS CONCILIACION BANCARIA");
		String chequeSinCobrarTot = valorParametro("CONCILIACION BANCARIA, CHEQUE SIN COBRAR, TOTAL");
		String consignacionTransitoTot = valorParametro("CONCILIACION BANCARIA, CONSIGNACION DE TRANSITO, TOTAL");
		
	
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, mes - 1);
		calendar.set(Calendar.YEAR, ano);
		parametro.put("PR_FECHA", calendar.getTime());
		parametro.put("PR_MES", getNombreMes(mes));
		parametro.put("PR_ANO", ano);
		parametro.put("PR_ELABORO_EN_CONCILIACION", elaboroConciliacion);
		parametro.put("PR_REVISO_EN_CONCILIACION", revisoConcilicacion);
		parametro.put("PR_CARGO_ENCARGADO_DE_TESORERIA", cargoEncargado);
		parametro.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA", nombreEncargado);
		parametro.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD", cargoJefeConta);
		parametro.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD", nombreJefeConta);
		parametro.put("PR_NOMBRE_JEFE_DIVISION_ADMINISTRATIVA", nombreJefeDiv);
		parametro.put("PR_CARGO_JEFE_FINANCIERO", cargoJefeFinan);
		parametro.put("PR_SALDOEXTRACTO", saldoExtracto.doubleValue());
		parametro.put("PR_CONCILIACION_BANCARIA_CHEQUE_SIN_COBRAR", chequesinCobrar);
		parametro.put("PR_CONCILIACION_BANCARIA,_CONSIGNACION_DE_TRANSITO", consignacionTransito);
		parametro.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
		parametro.put("PR_CIUDADCOMPANIA", SessionUtil.getCompaniaIngreso().getCiudad());
	
		parametro.put("PR_MANEJA_INGRESO_PARTIDAS_CONCILIATORIAS", manejaPartidasConci);
		parametro.put("PR_FORMATO_ESPECIAL_CONCILIACION", formatoEspConci);
		parametro.put("PR_TITULO_ELABORO_ESPECIAL_CONCILIACION_BANCARIA", tituloConciBancaria);
		parametro.put("PR_MODIFICACION_PALABRAS_CONCILIACION_BANCARIA", modConciBancaria);
		parametro.put("PR_TEXTO_TOTAL_SUBCHEQSINCOBRAR", chequeSinCobrarTot);
		parametro.put("PR_TEXTO_TOTAL_SUBCHEQSINCOBRAR_VA", "Total Cheques Sin Cobrar Vigencia anterior ");
		parametro.put("PR_TEXTO_TOTAL_SUBCONSENTRANSITO", consignacionTransitoTot);

	}

	private Object condicionSubConsEnTransito(String fechaArmada) {
		String condicion;
		if (manejaConciAnterior.equals("SI")) {
			condicion = "AND  ANO = " + ano + " \n" + "AND     PAGADOBANCO = 0  \n"
					+ "AND    NVL(FECHA_CONCILIA ,FECHA) <= " + fechaArmada;
		} else {
			condicion = "AND     FECHA <= " + fechaArmada + " \n"
					+ "AND     ((FECHA_CONCILIA IS NULL AND PAGADOBANCO = 0) OR (FECHA_CONCILIA > " + fechaArmada
					+ "))";
		}
		return condicion;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista
	 *
	 *
	 */
	public void oprimirImprimir() {
		try {
			archivoDescarga = null;

			generarLogicaInforme();

			String reporte = "002383Conciliacionbancaria";	
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametro, ConectorPool.ESQUEMA_SYSMAN,
					ReportesBean.FORMATOS.PDF);

		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void generarLogicaInforme() {
		cargarIntervaloFechas();
		String strTipos = "";
		String condicion = "";
		String codEquivalente ="";
		String fechaArmada = SysmanFunciones.formatearFecha(fechaFinal);
		int modulo = Integer.parseInt(SessionUtil.getModulo());			
		saldoExtracto = traerSaldoExtracto(mes, ano);
		cargarParametros();
		
		Map<String, Object> reemplazos = new HashMap<>();
		reemplazos.put("cuenta", cuenta);

		// 002387SubCheqSinCobrar
		strTipos = "'E'";
		String subCheqSinCobrarVA = null;
		if (manejaConciAnterior.equals("SI")) {
			condicion = "AND     D.PAGADOBANCO = 0  \n" + "AND    NVL(FECHA_CONCILIA ,FECHA) <= " + fechaArmada;
		} else {
			condicion = "AND   FECHA <= " + fechaArmada + " \n"
					+ "AND     ((FECHA_CONCILIA IS NULL AND PAGADOBANCO = 0) "
					+ "OR (PAGADOBANCO <> 0 AND FECHA_CONCILIA > " + fechaArmada + "))";
			// SubCheqSinCobrarVA
			if (codEquivalente != null) {
				Map<String, Object> hashMap = new HashMap<>();
				hashMap.put("cuenta", codEquivalente);
				hashMap.put(cStrTipos, strTipos);
				hashMap.put(cCondicion, condicion);
				subCheqSinCobrarVA = Reporteador.resuelveConsulta("002387SubCheqSinCobrar", modulo, hashMap);
			}
		}

		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put(cCondicion, condicion);
		String subCheqSinCobrar = Reporteador.resuelveConsulta("002387SubCheqSinCobrar", modulo, reemplazos);

		// 002388SubConsEnTransito
		strTipos = "'S'";
		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put(cCondicion, condicionSubConsEnTransito(fechaArmada));
		String subConsEnTransito = Reporteador.resuelveConsulta("002388SubConsEnTransito", modulo, reemplazos);

		// 002385SubNotaBancarias
		if (manejaConciAnterior.equals("SI")) {
			strTipos = "'B', 'G' ,'A','D'";
			condicion = "AND     PAGADOBANCO = 0  \n" + "AND     NVL(FECHA_CONCILIA ,FECHA) <= " + fechaArmada;
		} else {
			strTipos = "'B','G','A','D','I'";
			condicion = "AND     FECHA <= " + fechaArmada + " \n"
					+ "AND     ((FECHA_CONCILIA IS NULL AND PAGADOBANCO = 0)  \n" + "        OR (PAGADOBANCO <> 0  \n"
					+ "        AND FECHA_CONCILIA > " + fechaArmada + "))";
		}

		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put(cCondicion, condicion);
		String subNotaBancarias = Reporteador.resuelveConsulta("002385SubNotaBancarias", modulo, reemplazos);

		// 002384SubTransacPagPost
		if (manejaConciAnterior.equals("SI")) {
			condicion = "AND     D.ANO = " + ano + " \n" + "AND     NVL(FECHA_CONCILIA ,FECHA) <= " + fechaArmada;
		} else {
			condicion = "AND  FECHA_CONCILIA IS NOT NULL \n" + "AND     FECHA_CONCILIA <= " + fechaArmada;
		}

		strTipos = "'E','S','B','G','A','D','I'";
		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put(cCondicion, condicion);
		reemplazos.put("fechaArmada", fechaArmada);
		String subTransacPagPost = Reporteador.resuelveConsulta("002384SubTransacPagPost", modulo, reemplazos);

		// 002386SubPartidasConciliatorias
		reemplazos.clear();
		reemplazos.put("ano", ano);
		reemplazos.put("mes", mes);
		int mesAnterior = SysmanFunciones.mes(fechaInicial) - 1;

		reemplazos.put("mesAnterior", mesAnterior);
		reemplazos.put("cuenta", cuenta);
		String subPartidasConciliatorias = Reporteador.resuelveConsulta("002386SubPartidasConciliatorias", modulo,
				reemplazos);

		// 002383Conciliacionbancaria
		String sql = Reporteador.resuelveConsulta("002383Conciliacionbancaria", modulo, reemplazos);

		parametro.put("PR_STRSQL", sql);
		parametro.put("PR_STRSQL_SUBCHEQSINCOBRAR", subCheqSinCobrar);
		parametro.put("PR_STRSQL_SUBCHEQSINCOBRAR_VA", subCheqSinCobrarVA);
		parametro.put("PR_STRSQL_SUBCONSENTRANSITO", subConsEnTransito);
		parametro.put("PR_STRSQL_SUBNOTABANCARIAS", subNotaBancarias);
		parametro.put("PR_STRSQL_SUBTRANSACPAGPOST", subTransacPagPost);
		parametro.put("PR_STRSQL_SUBPARTIDASCONCILIATORIAS", subPartidasConciliatorias);
		parametro.put("PR_CUENTA", cuenta);
		parametro.put("PR_NOMBRECUENTA", cuenta.replace(cuenta + " ", ""));

		String sumaTotal = "SELECT  SUM(VALOR_DEBITO-VALOR_CREDITO) TOTAL FROM    (";
		Registro reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sumaTotal + subTransacPagPost + ")");
		String total = (String) reg.getCampos().get(cTotal);
		double totalTransacPagPost = Double.parseDouble(total != null ? total : "0");

		double totalPartidasConciliatorias = 0;
		double totalChequesVA = 0;
		if ("SI".equals(manejaPartidasConci)) {
			sumaTotal = "SELECT  SUM(PARTIDA_DEBITO-PARTIDA_CREDITO) TOTAL FROM    (";
			reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sumaTotal + subPartidasConciliatorias + ")");
			total = (String) reg.getCampos().get(cTotal);
			totalPartidasConciliatorias = Double.parseDouble(total != null ? total : "0");
		}

		if (	codEquivalente != null) {
			sumaTotal = "SELECT  SUM(VALOR_CREDITO-VALOR_DEBITO) TOTAL FROM    (";
			reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sumaTotal + subCheqSinCobrarVA + ")");
			total = (String) reg.getCampos().get(cTotal);
			totalChequesVA = Double.parseDouble(total != null ? total : "0");
		}

		sumaTotal = "SELECT  SUM(VALOR_CREDITO-VALOR_DEBITO) TOTAL FROM    (";
		reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sumaTotal + subCheqSinCobrar + ")");
		total = (String) reg.getCampos().get(cTotal);
		double totalCheques = Double.parseDouble(total != null ? total : "0");

		reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sumaTotal + subNotaBancarias + ")");
		total = (String) reg.getCampos().get(cTotal);
		double totalNotas = Double.parseDouble(total != null ? total : "0");

		reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sumaTotal + subConsEnTransito + ")");
		total = (String) reg.getCampos().get(cTotal);
		double totalConsignacion = Double.parseDouble(total != null ? total : "0");

		String saldoCon = " SELECT SALDO" + mes + " AS SALDOCONTABLE \n" + " FROM    PLAN_CONTABLE \n"
				+ " WHERE   COMPANIA = '" + compania + "' \n" + "   AND   ANO = " + ano + " \n" + "   AND   CODIGO = '"
				+ cuenta + "'";
		reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, saldoCon);
		total = (String) reg.getCampos().get("SALDOCONTABLE");
		double saldoCont = Double.parseDouble(total != null ? total : "0");

		double saldoConciliado = saldoCont + totalCheques + totalConsignacion + totalNotas + totalTransacPagPost
				+ totalPartidasConciliatorias + totalChequesVA;


		parametro.put("PR_SALDOCONCILIADO", saldoConciliado); 
		parametro.put("PR_AVISO",Double.doubleToRawLongBits(saldoConciliado - saldoExtracto.doubleValue()) == 0 ? "CONCILIACIÓN CORRECTA"
				: "POR CONCILIAR");

	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	

	public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
    	cargarListaCuenta();
        // </CODIGO_DESARROLLADO>
    }
    
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuenta
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuenta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuenta = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	/**
	 * Retorna la variable cuenta
	 * 
	 * @return cuenta
	 */
	public String getCuenta() {
		return cuenta;
	}

	/**
	 * Asigna la variable cuenta
	 * 
	 * @param cuenta Variable a asignar en cuenta
	 */
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	/**
	 * Retorna la variable nombreCompania
	 * 
	 * @return nombreCompania
	 */
	public String getNombreCompania() {
		return nombreCompania;
	}

	/**
	 * Asigna la variable nombreCompania
	 * 
	 * @param nombreCompania Variable a asignar en nombreCompania
	 */
	public void setNombreCompania(String nombreCompania) {
		this.nombreCompania = nombreCompania;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE> 
	/**
	 * Retorna la lista listaCuenta
	 * 
	 * @return listaCuenta
	 */
	public RegistroDataModelImpl getListaCuenta() {
		return listaCuenta;
	}

	/**
	 * Asigna la lista listaCuenta
	 * 
	 * @param listaCuenta Variable a asignar en listaCuenta
	 */
	public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
		this.listaCuenta = listaCuenta;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * Trae el nombre del mes a partir de su numero.
	 *
	 * @param numero Numero de mes. Inicia en 1.
	 * @return Cadena con el nombre del mes.
	 */
	private String getNombreMes(int numero) {
		int aux = numero;
		String nombreMes = "";
		DateFormatSymbols dfs = new DateFormatSymbols(new Locale("es"));
		String[] meses = dfs.getMonths();
		aux--;
		if ((aux >= 0) && (aux <= 11)) {
			nombreMes = SysmanFunciones.initCap(meses[aux]);
		}
		return nombreMes;
	}
	
	

}
