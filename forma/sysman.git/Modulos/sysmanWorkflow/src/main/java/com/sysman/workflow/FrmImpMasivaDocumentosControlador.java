/*-
 * FrmImpMasivaDocumentosControlador.java
 *
 * 1.0
 * 
 * 12/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmImpMasivaDocumentosControladorUrlEnum;
/**
 *
 * @version 1.0, 12/01/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmImpMasivaDocumentosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>

	private String tipoImpuesto;
	private String etapa;
	private String tramiteInicial;
	private String tramiteFinal;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 */
	private RegistroDataModelImpl listaTipoImpuesto;
	/**
	 */
	private RegistroDataModelImpl listaEtapa;
	private RegistroDataModelImpl listaPlantilla;
	private Date fechaPlantilla;
	private String nombrePlantilla;
	private String plantilla;
	private Registro rsCantidad;
	private String cantidadTramites;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmImpMasivaDocumentosControlador
	 */
	public FrmImpMasivaDocumentosControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2232;
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
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
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaTipoImpuesto(); 
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
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
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaTipoImpuesto
	 *
	 */
	public void cargarListaTipoImpuesto(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmImpMasivaDocumentosControladorUrlEnum.URL0001
						.getValue());

		listaTipoImpuesto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaEtapa
	 *
	 */
	public void cargarListaEtapa(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROCESOJUD.getName(), tipoImpuesto);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmImpMasivaDocumentosControladorUrlEnum.URL0002
						.getValue());

		listaEtapa = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaPlantilla
	 *
	 */
	public void cargarListaPlantilla(){

		Map<String, Object> param = new TreeMap<>();
		param.put("COMPANIA", compania);
		param.put("TIPO", "55");
		param.put("NODO", etapa);
		param.put("PROCESO", tipoImpuesto);


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmImpMasivaDocumentosControladorUrlEnum.URL1035006
						.getValue());
		listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Generar
	 * en la vista
	 *
	 *
	 */
	public void oprimirGenerar() {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> paramVar = new HashMap<>();
		//cargarPlatillas();
		//numeroTramites();
		paramVar.put("s$compania$s", compania);
		paramVar.put("s$proceso$s", tipoImpuesto);
		paramVar.put("s$etapa$s", etapa);
		paramVar.put("s$usuario$s", SessionUtil.getUser().getCodigo());
		//param.put("s$tipoTramite$s",tipoTramite);
		paramVar.put("s$tramiteInicial$s",tramiteInicial);
		paramVar.put("s$tramiteFinal$s",tramiteFinal);


		/* Reemplazos de la consulta asociada a la plantilla */
		SessionUtil.setSessionVar("variablesConsultaWord", paramVar);


		String[] claves = new String[4];
		claves[0] = "codigoPlantilla";
		claves[1] = "fechaPlantilla";
		claves[2] = "nombreDocDescarga";
		//claves[3] = "cantidadTramite";

		String[] valores = new String[4];
		valores[0] = plantilla;
		valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
		valores[2] = nombrePlantilla;
		//valores[3] = cantidadTramites;



		SessionUtil.cargarModalDatosFlashCerrar(Integer
				.toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
						.getCodigo()),
				SessionUtil.getModulo(), claves, valores);

		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>

	public void cargarPlatillas() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROCESOJUD.getName(), tipoImpuesto);
		param.put("ETAPA", etapa);
		param.put("TRAMITEINI", tramiteInicial);
		param.put("TRAMITEFIN", tramiteFinal);
		param.put("USUARIO", SessionUtil.getUser().getCodigo());

		try {
			rsCantidad = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmImpMasivaDocumentosControladorUrlEnum.URL0004.getValue())
							.getUrl(),
							param));

			if (rsCantidad != null) {

				cantidadTramites = rsCantidad.getCampos().get("CANTIDAD").toString();
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


	}

	public void numeroTramites() {


		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROCESOJUD.getName(), tipoImpuesto);
		param.put("ETAPA", etapa);
		param.put("TRAMITEINI", tramiteInicial);
		param.put("TRAMITEFIN", tramiteFinal);
		param.put("USUARIO", SessionUtil.getUser().getCodigo());

		try {
			List<Registro>	rsCantidadT = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmImpMasivaDocumentosControladorUrlEnum.URL0005
									.getValue())
							.getUrl(), param));

			if (rsCantidadT != null) {

				Map<String, Object> paramVar = new HashMap<>();
				for (int i = 0; i <  rsCantidadT.size(); i++) {

					paramVar.put(String.valueOf(i), rsCantidadT.get(i).getCampos().get("NUMERO"));
				}
				SessionUtil.setSessionVar("numeroTramite", paramVar);
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


	}
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipoImpuesto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoImpuesto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoImpuesto= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		etapa = null;
		plantilla = null;
		cargarListaEtapa();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEtapa
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEtapa(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		etapa= registroAux.getCampos().get("CODIGO").toString();
		plantilla = null;
		cargarListaPlantilla();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPlantilla
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPlantilla(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		plantilla= registroAux.getCampos().get("CODIGO").toString();
		nombrePlantilla = registroAux.getCampos().get("NOMBRE").toString();
		fechaPlantilla  = (Date) registroAux.getCampos().get("FECHA");

	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoImpuesto
	 * 
	 * @return  tipoImpuesto
	 */
	public String getTipoImpuesto() {
		return tipoImpuesto;
	}
	/**
	 * Asigna la variable  tipoImpuesto
	 * 
	 * @param  tipoImpuesto
	 * Variable a asignar en  tipoImpuesto
	 */
	public void setTipoImpuesto(String tipoImpuesto) {
		this.tipoImpuesto = tipoImpuesto;
	}
	/**
	 * Retorna la variable etapa
	 * 
	 * @return  etapa
	 */
	public String getEtapa() {
		return etapa;
	}
	/**
	 * Asigna la variable  etapa
	 * 
	 * @param  etapa
	 * Variable a asignar en  etapa
	 */
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	/**
	 * Retorna la variable tramiteInicial
	 * 
	 * @return  tramiteInicial
	 */
	public String getTramiteInicial() {
		return tramiteInicial;
	}
	/**
	 * Asigna la variable  tramiteInicial
	 * 
	 * @param  tramiteInicial
	 * Variable a asignar en  tramiteInicial
	 */
	public void setTramiteInicial(String tramiteInicial) {
		this.tramiteInicial = tramiteInicial;
	}
	/**
	 * Retorna la variable tramiteFinal
	 * 
	 * @return  tramiteFinal
	 */
	public String getTramiteFinal() {
		return tramiteFinal;
	}
	/**
	 * Asigna la variable  tramiteFinal
	 * 
	 * @param  tramiteFinal
	 * Variable a asignar en  tramiteFinal
	 */
	public void setTramiteFinal(String tramiteFinal) {
		this.tramiteFinal = tramiteFinal;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipoImpuesto
	 * 
	 * @return listaTipoImpuesto
	 */
	public RegistroDataModelImpl getListaTipoImpuesto() {
		return listaTipoImpuesto;
	}
	/**
	 * Asigna la lista listaTipoImpuesto
	 * 
	 * @param listaTipoImpuesto
	 * Variable a asignar en  listaTipoImpuesto
	 */
	public void setListaTipoImpuesto(RegistroDataModelImpl listaTipoImpuesto) {
		this.listaTipoImpuesto = listaTipoImpuesto;
	}
	/**
	 * Retorna la lista listaEtapa
	 * 
	 * @return listaEtapa
	 */
	public RegistroDataModelImpl getListaEtapa() {
		return listaEtapa;
	}
	/**
	 * Asigna la lista listaEtapa
	 * 
	 * @param listaEtapa
	 * Variable a asignar en  listaEtapa
	 */
	public void setListaEtapa(RegistroDataModelImpl listaEtapa) {
		this.listaEtapa = listaEtapa;
	}

	/**
	 * @return the listaPlantilla
	 */
	public RegistroDataModelImpl getListaPlantilla() {
		return listaPlantilla;
	}
	/**
	 * @param listaPlantilla the listaPlantilla to set
	 */
	public void setListaPlantilla(RegistroDataModelImpl listaPlantilla) {
		this.listaPlantilla = listaPlantilla;
	}
	/**
	 * @return the plantilla
	 */
	public String getPlantilla() {
		return plantilla;
	}
	/**
	 * @param plantilla the plantilla to set
	 */
	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
