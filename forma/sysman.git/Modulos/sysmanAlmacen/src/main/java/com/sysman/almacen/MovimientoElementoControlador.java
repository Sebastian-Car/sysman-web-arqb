/*-
 * MovimientoElementoControlador.java
 *
 * 1.0
 * 
 * 29/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.MovimientoElementoControladorEnum;
import com.sysman.almacen.enums.MovimientoElementoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 29/01/2021
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  MovimientoElementoControlador extends BeanBaseModal{
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
	private boolean ckGebraTrasDep;
	/**
	 * variabe de validacion
	 */
	private String elementoInicial;
	/**
	 * variable que almacen elemento inicial
	 */
	private String elementoFinal;
	/**
	 * variable que almacen elemento final
	 */
	private Date fechaInicial;
	/**
	 * variable que almacena la fecha inicial
	 */
	private Date fechaFinal;
	/**
	 * varibale que almacen al fecha final
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
	private String claseElemento;
	/**
	 * variable que almacen la clase elemento
	 */

	private StreamedContent archivoDescarga;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private RegistroDataModelImpl listaElementoInicial;
	/**
	 * variable que almacea la lista ElementoInicial
	 */
	private RegistroDataModelImpl listaElementoFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de MovimientoElementoControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	public MovimientoElementoControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2233;
			numFormulario = GeneralCodigoFormaEnum.MOVIMIENTO_ELEMENTO_CONTROLADOR.getCodigo();
			validarPermisos();
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
		fechaInicial = new Date();
		fechaFinal = new Date();
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
FR2233-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
    DoCmd.Restore
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaElementoInicial
	 *
	 */
	public void cargarListaElementoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						MovimientoElementoControladorUrlEnum.URL11959
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
						MovimientoElementoControladorUrlEnum.URL12000
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(MovimientoElementoControladorEnum.PARAM0.getValue(),
				elementoInicial);

		listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(ReportesBean.FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
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
		generarInforme(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	public void generarInforme(ReportesBean.FORMATOS formato) {

		String reporte = "002175MOVIMIENTOELEMENTOS";

		try {

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("elementoInicial", elementoInicial);
			reemplazar.put("elementoFinal", elementoFinal);
			reemplazar.put("claseElemento", claseElemento);
			reemplazar.put("fechaInicial",
					SysmanFunciones.formatearFechaCadena(fechaInicial,"DD/MM/YYYY"));
			reemplazar.put("fechaFinal",
					SysmanFunciones.formatearFechaCadena(fechaFinal,"DD/MM/YYYY"));

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_FORMS_MOVIMIENTOELEMENTO_DESDE",elementoInicial);
			parametros.put("PR_FORMS_MOVIMIENTOELEMENTO_HASTA", elementoFinal);
			parametros.put("PR_VALIDAR_SUB", ckGebraTrasDep);



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
		elementoInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO
						.getName()),   "")
				.toString();

		nombreElementoIni = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
				.toString();


		elementoFinal= null;
		nombreElementoFin = null;
		cargarListaElementoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = SysmanFunciones
				.nvl(registroAux.getCampos() .get(GeneralParameterEnum.CODIGOELEMENTO
						.getName()),  "")
				.toString();

		nombreElementoFin = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
				.toString();
	}
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
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
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
	/**
	 * @return the ckGebraTrasDep
	 */
	public boolean isCkGebraTrasDep() {
		return ckGebraTrasDep;
	}
	/**
	 * @param ckGebraTrasDep the ckGebraTrasDep to set
	 */
	public void setCkGebraTrasDep(boolean ckGebraTrasDep) {
		this.ckGebraTrasDep = ckGebraTrasDep;
	}
	/**
	 * @return the claseElemento
	 */
	public String getClaseElemento() {
		return claseElemento;
	}
	/**
	 * @param claseElemento the claseElemento to set
	 */
	public void setClaseElemento(String claseElemento) {
		this.claseElemento = claseElemento;
	}


	//</SET_GET_LISTAS_COMBO_GRANDE>
}
