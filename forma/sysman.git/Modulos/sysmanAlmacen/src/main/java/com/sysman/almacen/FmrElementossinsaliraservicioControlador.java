/*-
 * FmrElementossinsaliraservicioControlador.java
 *
 * 1.0
 * 
 * 17/12/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.FmrElementossinsaliraservicioControladorEnum;
import com.sysman.almacen.enums.FmrElementossinsaliraservicioControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 17/12/2021
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class  FmrElementossinsaliraservicioControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>

	private String opcion;
	/**
	 * Atributo que almacena el codigo del elemento inicial
	 * seleccionado en el formulario
	 */
	private String elementoInicial;
	/**
	 * Atributo que almacena el codigo del elemento final
	 * seleccionado en el formulario
	 */
	private String elementoFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private  String modulo;
	private String digitos;
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaElementoInicial;
	private RegistroDataModelImpl listaElementoFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FmrElementossinsaliraservicioControlador
	 */
	public FmrElementossinsaliraservicioControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		opcion = "1";
		try {
			//2329
			numFormulario = GeneralCodigoFormaEnum.FMR_ELEMENTOS_SIN_SALIRA_SERVICIO_CONTROLADOR
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

		//		try {
		//			digitos = ejbSysmanUtil.consultarParametro(compania,
		//					"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),false);
		//		}
		//		catch (SystemException e) {
		//
		//			logger.error(e.getMessage(), e);
		//			JsfUtil.agregarMensajeError(e.getMessage());
		//		}
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaElementoInicial(); 
		cargarListaElementoFinal();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	// <METODOS_CARGAR_LISTA>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		/*
FR2329-AL_ABRIR
Private Sub Form_Load()
   'formularioAbrir 10, Me.Name
   DoCmd.Restore
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaElementoInicial
	 */
	public void cargarListaElementoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FmrElementossinsaliraservicioControladorUrlEnum.URL1229
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
	/**
	 * Carga la lista listaElementoFinal
	 */
	public void cargarListaElementoFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FmrElementossinsaliraservicioControladorUrlEnum.URL1230
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FmrElementossinsaliraservicioControladorEnum.ELEMENTO.getValue(),
				elementoInicial);

		listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName()); 		
	}
	/**
	 * Metodo ejecutado al oprimir el boton imprimirExcel
	 * en la vista
	 * @throws SysmanException 
	 * @throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException 
	 * @throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException 
	 */
	public void oprimirimprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(ReportesBean.FORMATOS.EXCEL);	
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton imprimirPdf
	 * en la vista
	 * @throws SysmanException 
	 * @throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException 
	 *
	 */
	public void oprimirimprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(ReportesBean.FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	private void generarInforme(FORMATOS formato) {

		String reporte = "002328ElementosSinSalidaServicio";
		String devolutivo = "";
		String comodato ="";
		String consolidado ="";
	
		if (opcion.equals("1")) { 
			devolutivo = "AND INVENTARIO.TIPO = 'D' ";

		} else if (opcion.equals("2")) {
			comodato = "AND INVENTARIO.TIPO = 'E' ";            	
		} 
		else if (opcion.equals("3")) {
			consolidado = "AND INVENTARIO.TIPO = 'D' OR INVENTARIO.TIPO = 'E'";	              	
			      
		}

		try {

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("elementoInicial", elementoInicial);
			reemplazar.put("elementoFinal", elementoFinal);
			reemplazar.put("devolutivo", devolutivo);
			reemplazar.put("comodato", comodato);
			reemplazar.put("consolidado", consolidado);



			Map<String, Object> parametros = new HashMap<>();
			// MANEJO DE PARAMETROS DEL REPORTE
			String strSql = Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);
			parametros.put("PR_STRSQL", strSql);
			parametros.put("PR_NOMBRECOMPANIA",
					SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_FORMS_ELEMENTOSSINSALIRASERVICIO_ELEMENTOINICIAL", elementoInicial);
			parametros.put("PR_FORMS_ELEMENTOSSINSALIRASERVICIO_ELEMENTOFINAL", elementoFinal);


			archivoDescarga = JsfUtil.exportarStreamed(reporte,
					parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control TipoElemento
	 * 
	 */
	public void cambiarTipoElemento() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoInicial = registroAux.getCampos().get("CODIGOELEMENTO")
				.toString();

		cargarListaElementoFinal();
		//elementoFinal = null;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = registroAux.getCampos().get("CODIGOELEMENTO")
				.toString();
	}

	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable opcion
	 * 
	 * @return  opcion
	 */
	public String getOpcion() {
		return opcion;
	}
	/**
	 * Asigna la variable  opcion
	 * 
	 * @param  opcion
	 * Variable a asignar en  opcion
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
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
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
