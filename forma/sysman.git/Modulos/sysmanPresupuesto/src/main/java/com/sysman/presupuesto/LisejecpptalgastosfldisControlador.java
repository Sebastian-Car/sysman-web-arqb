/*-
 * LisejecpptalgastosfldisControlador.java
 *
 * 1.0
 * 
 * 19/10/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisejecpptalgastosfldisControladorUrlEnum;
import com.sysman.presupuesto.enums.LisejecpptalgastosflsControladorEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * Formulario en el cual se permite generar 
 * el informe de ejecuciones de gastos especial distritales.
 * @version 1.0, 19/10/2020
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  LisejecpptalgastosfldisControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Constante a nivel de clase que aloja el nombre de la compania
	 * con la que esta interactuando el usuario
	 */
	private final String codigoC;
	/**
	 * Constante que almacenara la cadena "CODIGO"
	 */
	/**
	 * Constante que almacenara la cadena "PR_FORMATO"
	 */
	private final String prFormatoC;
	private boolean especial;
	private int nivel;
	private final String modulo;
	private String cuentaInicial;
	/**
	 * Esta variable se encarga de almacenar la cuenta inicial
	 */
	private String cuentaFinal;
	/**
	 * *Esta variable se encarga de almacenar la cuenta final
	 */
	private String digitos;
	/**
	 * *Esta variable se encarga de almacena los digitos
	 */
	private String decimales;
	/**
	 * *Esta variable se encarga de almacena los decimales
	 */
	private String fuenteInicial;
	/**
	 * Esta variable se encarga de almacenar la fuente inicial
	 */
	private String fuenteFinal;
	/**
	 * *Esta variable se encarga de almacenar la fuente final
	 */
	private int ano;
	/**
	 * Esta variable se encarga de almacenar la ano
	 */
	private int mesInicial;
	/**
	 * Esta variable se encarga de almacenar el mes inicial
	 */
	private int mesFinal;
	/**
	 * Esta variable se encarga de almacenar el mes final
	 */
	private boolean presentarVisible;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * variable descarga
	 */
	private List<Registro> listaAno;
	/**
	 * Listado de registros para la listaAno
	 */
	private List<Registro> listaMesInicial;
	/**
	 * Listado de registros para la listaMesInicial
	 */
	private List<Registro> listaMesFinal;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Listado de registros para la listaMesFinal
	 */
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 *Listado de registros para el combo de listaCuentaInicial
	 */
	private RegistroDataModelImpl listaCuentaFinal;
	/**
	 *Listado de registros para el combo de listaCuentaFinal
	 */
	private RegistroDataModelImpl listaFuenteInicial;
	/**
	 * Listado de registros para el combo de listaCuentaFinal
	 */
	private RegistroDataModelImpl listaFuenteFinal;
	/*
	 * Listado de registros para el combo de listaFuenteFinal
	 */
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de LisejecpptalgastosfldisControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	public LisejecpptalgastosfldisControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		codigoC = "CODIGO";
		prFormatoC = "PR_FORMATO";
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		try {
			numFormulario = GeneralCodigoFormaEnum.LISEJECPPTAL_GASTOSFLDIS_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			Logger.getLogger(LisdispabiertascuentasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		abrirFormulario();
		//<CARGAR_LISTA>
		cargarListaAno();
		cargarListaMesInicial();
		cargarListaMesFinal();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCuentaInicial(); 
		cargarListaCuentaFinal(); 
		cargarListaFuenteInicial(); 
		cargarListaFuenteFinal();
		//</CREAR_ARBOLES>

	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		ano = SysmanFunciones
				.ano(new Date());
		mesInicial = SysmanFunciones
				.mes(new Date());
		digitos = "2";
		decimales = "2";
		nivel = 99;
		presentarVisible = true;
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 *
	 */
	public void cargarListaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									LisejecpptalgastosfldisControladorUrlEnum.URL4777
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaMesInicial
	 *
	 */
	public void cargarListaMesInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(GeneralParameterEnum.ANO.name(), ano);

		UrlBean urlListmesInicial = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalgastosfldisControladorUrlEnum.URL5230
						.getValue());
		try {
			listaMesInicial = RegistroConverter.toListRegistro(requestManager
					.getList(urlListmesInicial.getUrl(), param));
		}
		catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	/**
	 * 
	 * Carga la lista listaMesFinal
	 *
	 */
	public void cargarListaMesFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(GeneralParameterEnum.ANO.name(), ano);

		UrlBean urlListmesFinal = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalgastosfldisControladorUrlEnum.URL5230
						.getValue());
		try {
			listaMesFinal = RegistroConverter.toListRegistro(requestManager
					.getList(urlListmesFinal.getUrl(), param));
		}
		catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 */
	public void cargarListaCuentaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalgastosfldisControladorUrlEnum.URL5961
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoC);
	}
	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 */
	public void cargarListaCuentaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalgastosfldisControladorUrlEnum.URL6925
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
		param.put(LisejecpptalgastosflsControladorEnum.CUENTAINICIAL.getValue(),
				cuentaInicial);
		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoC);
	}
	/**
	 * 
	 * Carga la lista listaFuenteInicial
	 *
	 */
	public void cargarListaFuenteInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalgastosfldisControladorUrlEnum.URL7986
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoC);
	}
	/**
	 * 
	 * Carga la lista listaFuenteFinal
	 *
	 */
	public void cargarListaFuenteFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalgastosfldisControladorUrlEnum.URL8754
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(LisejecpptalgastosflsControladorEnum.AUXILIARINICIAL
				.getValue(),
				fuenteInicial);
		listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoC);
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir
	 * en la vista
	 *
	 *
	 */
	public void oprimirImprimir() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(FORMATOS.PDF);
		//generarInforme(FORMATOS.PDF);

	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(FORMATOS.EXCEL);
		//generarInforme(FORMATOS.EXCEL);

	}
	private void generarInforme(ReportesBean.FORMATOS formato) {

		String parReporte = "";
		String nombreReportC = "002214LisEjecPptalGastosFLIDI";

		try {

			if (SysmanFunciones.validarVariableVacio(digitos)) {
				digitos = "2";
			}

			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("ano", ano);
			reemplazar.put("mesInicial", mesInicial);
			reemplazar.put("mesFinal", mesFinal);
			reemplazar.put("cuentaInicial",cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("fuenteInicial",fuenteInicial);
			reemplazar.put("fuenteFinal",fuenteFinal);
			reemplazar.put("digitos", digitos);
			reemplazar.put("nivel", nivel);

			String firmaUno = ejbSysmanUtil.consultarParametro(
					compania, "FIRMA EJECUCION 1", modulo, new Date(),
					true);
			String firmaDos = ejbSysmanUtil.consultarParametro(
					compania, "FIRMA EJECUCION 2", modulo, new Date(),
					true);
			String firmaTres = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania,
							"FIRMA EJECUCION 3", modulo,
							new Date(), true), "")
					.toString();
			String cargoUno = ejbSysmanUtil.consultarParametro(
					compania, "CARGO EJECUCION 1", modulo, new Date(),
					true);
			String cargoDos = ejbSysmanUtil.consultarParametro(
					compania, "CARGO EJECUCION 2", modulo, new Date(),
					true);
			String cargoTres = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(
							compania,
							"CARGO EJECUCION 3", modulo,
							new Date(), true), "")
					.toString();

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_FIRMA_EJECUCION_1", firmaUno);
			parametros.put("PR_FIRMA_EJECUCION_2", cargoUno);
			parametros.put("PR_FIRMA_EJECUCION_3", firmaDos);
			parametros.put("PR_CARGO_EJECUCION_1", cargoDos);
			parametros.put("PR_CARGO_EJECUCION_2", firmaTres);
			parametros.put("PR_CARGO_EJECUCION_3", cargoTres);
			parametros.put("PR_MES_INICIAL",SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
					.toUpperCase());
			parametros.put("PR_MES_FINAL",SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]
					.toUpperCase());
			parametros.put("PR_ANO",ano);
			parametros.put("PR_ENCABEZADO",
					idioma.getString("TB_TB633") + " "
							+ SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
									.toUpperCase()
									+ " DE " + ano);

			switch (digitos) {

			case "0":
				parametros.put(prFormatoC, "#,#00;(#,#00)");
				break;
			case "1":
				parametros.put(prFormatoC, "#,#00.0;(#,#00.0)");
				break;
			case "2":
				parametros.put(prFormatoC, "#,#00.00;(#,#00.00)");
				break;

			default:
				parametros.put(prFormatoC, "#,##0");
				break;

			}

			if (especial) {
				parReporte = "0022150LisEjecPptalGastosSOIDI";
			}
			else {
				parReporte = "002214LisEjecPptalGastosFLIDI";
			}
			Reporteador.resuelveConsulta(nombreReportC,
					Integer.parseInt(modulo), reemplazar,
					parametros);


			archivoDescarga = JsfUtil.exportarStreamed(parReporte,
					parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SysmanException
				| SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		//<CODIGO_DESARROLLADO>
		cuentaInicial = null;
		cuentaFinal = null;
		fuenteInicial = null;
		fuenteFinal = null;
		cargarListaCuentaInicial();
		cargarListaFuenteInicial();
	}
	/**
	 * Metodo ejecutado al cambiar el control MesInicial
	 * 
	 */
	public void cambiarMesInicial() {
		//<CODIGO_DESARROLLADO>
		cargarListaMesInicial();
	}
	/**
	 * Metodo ejecutado al cambiar el control Especial
	 * 
	 * 
	 */
	public void cambiarEspecial() {
		//<CODIGO_DESARROLLADO>
		if (especial) {
			fuenteInicial = null;
			fuenteFinal = null;
			presentarVisible = false;
		}
		else {
			presentarVisible = true;
		}
	}

	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(codigoC), "")
				.toString();
		cuentaFinal = null;
		cargarListaCuentaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaFinal
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(codigoC), "")
				.toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteInicial
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(codigoC), "")
				.toString();
		fuenteFinal = null;
		cargarListaFuenteFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteFinal
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(codigoC), "")
				.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable especial
	 * 
	 * @return  especial
	 */
	public boolean getEspecial() {
		return especial;
	}
	/**
	 * Asigna la variable  especial
	 * 
	 * @param  especial
	 * Variable a asignar en  especial
	 */
	public void setEspecial(boolean especial) {
		this.especial = especial;
	}
	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return  cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}
	/**
	 * Asigna la variable  cuentaInicial
	 * 
	 * @param  cuentaInicial
	 * Variable a asignar en  cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}
	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return  cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}
	/**
	 * Asigna la variable  cuentaFinal
	 * 
	 * @param  cuentaFinal
	 * Variable a asignar en  cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}
	/**
	 * Retorna la variable digitos
	 * 
	 * @return  digitos
	 */
	public String getDigitos() {
		return digitos;
	}
	/**
	 * Asigna la variable  digitos
	 * 
	 * @param  digitos
	 * Variable a asignar en  digitos
	 */
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}
	/**
	 * Retorna la variable decimales
	 * 
	 * @return  decimales
	 */
	public String getDecimales() {
		return decimales;
	}
	/**
	 * Asigna la variable  decimales
	 * 
	 * @param  decimales
	 * Variable a asignar en  decimales
	 */
	public void setDecimales(String decimales) {
		this.decimales = decimales;
	}
	/**
	 * Retorna la variable fuenteInicial
	 * 
	 * @return  fuenteInicial
	 */
	public String getFuenteInicial() {
		return fuenteInicial;
	}
	/**
	 * Asigna la variable  fuenteInicial
	 * 
	 * @param  fuenteInicial
	 * Variable a asignar en  fuenteInicial
	 */
	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}
	/**
	 * Retorna la variable fuenteFinal
	 * 
	 * @return  fuenteFinal
	 */
	public String getFuenteFinal() {
		return fuenteFinal;
	}
	/**
	 * Asigna la variable  fuenteFinal
	 * 
	 * @param  fuenteFinal
	 * Variable a asignar en  fuenteFinal
	 */
	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
	}
	/**
	 * Retorna la variable ano
	 * 
	 * @return  ano
	 */
	public int getAno() {
		return ano;
	}
	/**
	 * Asigna la variable  ano
	 * 
	 * @param  ano
	 * Variable a asignar en  ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
	/**
	 * Retorna la variable mesInicial
	 * 
	 * @return  mesInicial
	 */
	public int getMesInicial() {
		return mesInicial;
	}
	/**
	 * Asigna la variable  mesInicial
	 * 
	 * @param  mesInicial
	 * Variable a asignar en  mesInicial
	 */
	public void setMesInicial(int mesInicial) {
		this.mesInicial = mesInicial;
	}
	/**
	 * Retorna la variable mesFinal
	 * 
	 * @return  mesFinal
	 */
	public int getMesFinal() {
		return mesFinal;
	}
	/**
	 * Asigna la variable  mesFinal
	 * 
	 * @param  mesFinal
	 * Variable a asignar en  mesFinal
	 */
	public void setMesFinal(int mesFinal) {
		this.mesFinal = mesFinal;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public boolean ispresentarVisible() {
		return presentarVisible;
	}

	public void setpresentarVisible(boolean presentarVisible) {
		this.presentarVisible = presentarVisible;
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
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	/**
	 * Retorna la lista listaMesInicial
	 * 
	 * @return listaMesInicial
	 */
	public List<Registro> getListaMesInicial() {
		return listaMesInicial;
	}
	/**
	 * Asigna la lista listaMesInicial
	 * 
	 * @param listaMesInicial
	 * Variable a asignar en  listaMesInicial
	 */
	public void setListaMesInicial(List<Registro> listaMesInicial) {
		this.listaMesInicial = listaMesInicial;
	}
	/**
	 * Retorna la lista listaMesFinal
	 * 
	 * @return listaMesFinal
	 */
	public List<Registro> getListaMesFinal() {
		return listaMesFinal;
	}
	/**
	 * Asigna la lista listaMesFinal
	 * 
	 * @param listaMesFinal
	 * Variable a asignar en  listaMesFinal
	 */
	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCuentaInicial
	 * 
	 * @return listaCuentaInicial
	 */
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}
	/**
	 * Asigna la lista listaCuentaInicial
	 * 
	 * @param listaCuentaInicial
	 * Variable a asignar en  listaCuentaInicial
	 */
	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}
	/**
	 * Retorna la lista listaCuentaFinal
	 * 
	 * @return listaCuentaFinal
	 */
	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}
	/**
	 * Asigna la lista listaCuentaFinal
	 * 
	 * @param listaCuentaFinal
	 * Variable a asignar en  listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
	/**
	 * Retorna la lista listaFuenteInicial
	 * 
	 * @return listaFuenteInicial
	 */
	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}
	/**
	 * Asigna la lista listaFuenteInicial
	 * 
	 * @param listaFuenteInicial
	 * Variable a asignar en  listaFuenteInicial
	 */
	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}
	/**
	 * Retorna la lista listaFuenteFinal
	 * 
	 * @return listaFuenteFinal
	 */
	public RegistroDataModelImpl getListaFuenteFinal() {
		return listaFuenteFinal;
	}
	/**
	 * Asigna la lista listaFuenteFinal
	 * 
	 * @param listaFuenteFinal
	 * Variable a asignar en  listaFuenteFinal
	 */
	public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
		this.listaFuenteFinal = listaFuenteFinal;
	}
	/**
	 * @return the nivel
	 */
	public int getNivel() {
		return nivel;
	}
	/**
	 * @param nivel the nivel to set
	 */
	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
