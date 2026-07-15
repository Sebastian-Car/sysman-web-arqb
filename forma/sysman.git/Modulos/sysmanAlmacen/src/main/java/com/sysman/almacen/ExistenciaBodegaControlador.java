/*-
 * ExistenciaBodegaControlador.java
 *
 * 1.0
 * 
 * 02/02/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
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

import com.sysman.almacen.enums.DepreciacionMesDependenciaControladorUrlEnum;
import com.sysman.almacen.enums.ExistenciaBodegaControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 02/02/2021
 * @author dcastiblanco
 * Se crea controlador para impresion de informe de elementos de devolutivos por bodega
 */
@ManagedBean
@ViewScoped
public class  ExistenciaBodegaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Constante a nivel de clase que almacena el codigo del usuario
	 * que inicio sesion
	 */
	private String elementoInicial;
	/**
	 * variable que alamcena el elemento inicial
	 */
	private String elementoFinal;
	/**
	 * variable que almacena elemento final
	 */
	private String ano;
	/**
	 * variable que almacena el ano
	 */
	private String mes;
	/**
	 * variable que almacena el mes
	 */
	private String nombreElementoIni;
	/**
	 * variable que almacena el nombreElementoIni
	 */
	private String nombreElementoFin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private  String modulo;
	private String digitos;
	private StreamedContent archivoDescarga;
	private List<Registro> listaAnoInicial;
	/**
	 * variable que almacena la lista de ano inicial
	 */
	private List<Registro> listamesInicial;
	/**
	 * variable que almacena la lista de mes inicial
	 */
	private RegistroDataModelImpl listaElementoInicial;
	/**
	 * variable que almacena la lista de elemento Inicial
	 */
	private RegistroDataModelImpl listaElementoFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ExistenciaBodegaControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public ExistenciaBodegaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			//2234;
			numFormulario = GeneralCodigoFormaEnum.EXISTENCIA_BODEGA_CONTROLADOR
					.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
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

		try {
			digitos = ejbSysmanUtil.consultarParametro(compania,
					"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),false);
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		ano = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
				Calendar.YEAR));
		mes = String.valueOf(
				SysmanFunciones.getParteFecha(new Date(),
						Calendar.MONTH)
				+
				1);
		cargarListaAnoInicial();
		cargarListamesInicial();
		cargarListaElementoInicial(); 
		cargarListaElementoFinal();
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		/*
FR2234-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
   DoCmd.Restore
   'formularioAbrir 10, Me.Name
   Me.cmbElementoDesde.Requery
End Sub
		 */
		//</CODIGO_DESARROLLADO>

	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnoInicial
	 *
	 */
	public void cargarListaAnoInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		try
		{
			listaAnoInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ExistenciaBodegaControladorUrlEnum.URL5600
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e)
		{
			Logger.getLogger(ExistenciaBodegaControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}		

	/**
	 * 
	 * Carga la lista listamesInicial
	 *
	 */
	public void cargarListamesInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		try {
			listamesInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ExistenciaBodegaControladorUrlEnum.URL5230
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			Logger.getLogger(DepreciacionMesDependenciaControlador.class
					.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaElementoInicial
	 *
	 */
	public void cargarListaElementoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ExistenciaBodegaControladorUrlEnum.URL11959
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
	/**
	 * 
	 * Carga la lista listaElementoFinal
	 *
	 */
	public void cargarListaElementoFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ExistenciaBodegaControladorUrlEnum.URL12000
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ELEMENTOINICIAL", elementoInicial);

		listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton imprimirPdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirimprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton imprimirExcel
	 * en la vista
	 *
	 *
	 */
	public void oprimirimprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		generarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarMes() {
		if ((mes == null) || (Integer.parseInt(mes) < 1) ||
				(Integer.parseInt(mes) > 12)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1838"));
			mes = null;
		}
	}


	public void generarInforme(FORMATOS formato) {

		String reporte = "002174ELEMENTOSCONSUMODEVOLUTIVOSENBODEGA";

		try {

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("mes", mes);
			reemplazar.put("ano", ano);
			reemplazar.put("elementoInicial", elementoInicial);
			reemplazar.put("elementoFinal", elementoFinal);


			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_MES_INICIAL", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
			                                                                            .parseInt(mes)].toUpperCase());
			parametros.put("PR_ANO_INICIAL", ano);

			Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar,parametros);

			archivoDescarga = JsfUtil.exportarStreamed(
					reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (FileNotFoundException ex) {
			JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
					idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
					.replace("s$reporte$s", reporte),
					ex.getMessage()));
			logger.error(ex.getMessage(), ex);
		}
		catch (JRException | IOException | SysmanException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	public void cambiarAno() {
		if ((ano == null) || (ano.length() < 4)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1839"));
			ano = null;
		}
		cargarListamesInicial();
		elementoInicial = null;
		elementoFinal = null;
	}

	public void cambiarMesInicial() {
		//<CODIGO_DESARROLLADO>
		cargarListamesInicial();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoInicial = registroAux.getCampos().get("CODIGOELEMENTO")
				.toString();
		nombreElementoIni = registroAux.getCampos().get("NOMBRELARGO")
				.toString();
		cargarListaElementoFinal();
		elementoFinal = nombreElementoFin = null;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoFinal
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = registroAux.getCampos().get("CODIGOELEMENTO")
				.toString();
		nombreElementoFin = registroAux.getCampos().get("NOMBRELARGO")
				.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable elementoInicial
	 * 
	 * @return  elementoInicial
	 */
	public String getElementoInicial() {
		return elementoInicial;
	}
	/**
	 * Asigna la variable  elementoInicial
	 * 
	 * @param  elementoInicial
	 * Variable a asignar en  elementoInicial
	 */
	public void setElementoInicial(String elementoInicial) {
		this.elementoInicial = elementoInicial;
	}
	/**
	 * Retorna la variable elementoFinal
	 * 
	 * @return  elementoFinal
	 */
	public String getElementoFinal() {
		return elementoFinal;
	}
	/**
	 * Asigna la variable  elementoFinal
	 * 
	 * @param  elementoFinal
	 * Variable a asignar en  elementoFinal
	 */
	public void setElementoFinal(String elementoFinal) {
		this.elementoFinal = elementoFinal;
	}
	/**
	 * Retorna la variable ano
	 * 
	 * @return  ano
	 */
	public String getAno() {
		return ano;
	}
	/**
	 * Asigna la variable  ano
	 * 
	 * @param  ano
	 * Variable a asignar en  ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}
	/**
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable nombreElementoIni
	 * 
	 * @return  nombreElementoIni
	 */
	public String getNombreElementoIni() {
		return nombreElementoIni;
	}
	/**
	 * Asigna la variable  nombreElementoIni
	 * 
	 * @param  nombreElementoIni
	 * Variable a asignar en  nombreElementoIni
	 */
	public void setNombreElementoIni(String nombreElementoIni) {
		this.nombreElementoIni = nombreElementoIni;
	}
	/**
	 * Retorna la variable nombreElementoFin
	 * 
	 * @return  nombreElementoFin
	 */
	public String getNombreElementoFin() {
		return nombreElementoFin;
	}
	/**
	 * Asigna la variable  nombreElementoFin
	 * 
	 * @param  nombreElementoFin
	 * Variable a asignar en  nombreElementoFin
	 */
	public void setNombreElementoFin(String nombreElementoFin) {
		this.nombreElementoFin = nombreElementoFin;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnoInicial
	 * 
	 * @return listaAnoInicial
	 */
	public List<Registro> getListaAnoInicial() {
		return listaAnoInicial;
	}
	/**
	 * Asigna la lista listaAnoInicial
	 * 
	 * @param listaAnoInicial
	 * Variable a asignar en  listaAnoInicial
	 */
	public void setListaAnoInicial(List<Registro> listaAnoInicial) {
		this.listaAnoInicial = listaAnoInicial;
	}
	/**
	 * Retorna la lista listamesInicial
	 * 
	 * @return listamesInicial
	 */
	public List<Registro> getListamesInicial() {
		return listamesInicial;
	}
	/**
	 * Asigna la lista listamesInicial
	 * 
	 * @param listamesInicial
	 * Variable a asignar en  listamesInicial
	 */
	public void setListamesInicial(List<Registro> listamesInicial) {
		this.listamesInicial = listamesInicial;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaElementoInicial
	 * 
	 * @return listaElementoInicial
	 */
	public RegistroDataModelImpl getListaElementoInicial() {
		return listaElementoInicial;
	}
	/**
	 * Asigna la lista listaElementoInicial
	 * 
	 * @param listaElementoInicial
	 * Variable a asignar en  listaElementoInicial
	 */
	public void setListaElementoInicial(RegistroDataModelImpl listaElementoInicial) {
		this.listaElementoInicial = listaElementoInicial;
	}
	/**
	 * Retorna la lista listaElementoFinal
	 * 
	 * @return listaElementoFinal
	 */
	public RegistroDataModelImpl getListaElementoFinal() {
		return listaElementoFinal;
	}
	/**
	 * Asigna la lista listaElementoFinal
	 * 
	 * @param listaElementoFinal
	 * Variable a asignar en  listaElementoFinal
	 */
	public void setListaElementoFinal(RegistroDataModelImpl listaElementoFinal) {
		this.listaElementoFinal = listaElementoFinal;
	}

	public String getDigitos() {
		return digitos;
	}
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
