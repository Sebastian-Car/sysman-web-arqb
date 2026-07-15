/*-
 * CausacioningresosautControlador.java
 *
 * 1.0
 * 
 * 07/04/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.enums.CausacioningresosautControladorUrlEnum;
import com.sysman.contabilidad.enums.ComprobantecntsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 07/04/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  CausacioningresosautControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	
	private static final String RUTA_ENCABEZADO_CONTAB = "/comprobantecnt.sysman";
	
	private String compania;
	//<DECLARAR_ATRIBUTOS>
	private Map<String, Object> rid; 
	private String ano; 
	private String tipoComp;  
	private String numeroComp;
	private String nombreComprobante; 
	private String tercero;
	private String sucursal;
	private String titulo;
	private String mes;
	private String opcionMenu;
	private String accionEncabezado;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaConcepto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaConceptoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTercero;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTerceroE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCentroCosto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCentroCostoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaAuxiliar;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaAuxiliarE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaFuente;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaFuenteE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaReferencia;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaReferenciaE;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	
	@EJB
    private EjbSysmanUtilRemote sysmanUtil;
	
	@EJB
	private EjbContabilidadCincoRemote ejbContabilidadCinco;
	
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de CausacioningresosautControlador
	 */
	public CausacioningresosautControlador() {
		super();
		try {
		compania = SessionUtil.getCompania();
		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null)
        {
            rid = (Map<String, Object>) parametrosEntrada.get("rid");
            ano = extraerString(parametrosEntrada.get("ano"));
            tipoComp = extraerString(parametrosEntrada.get("tipoComp"));
            numeroComp = extraerString(parametrosEntrada.get("numeroComp"));
            nombreComprobante = extraerString(parametrosEntrada.get("nombreComprobante"));
            tercero = extraerString(parametrosEntrada.get("tercero"));
            sucursal = extraerString(parametrosEntrada.get("sucursal"));
            mes = extraerString(parametrosEntrada.get("mes"));
            opcionMenu = extraerString(parametrosEntrada.get("opcionMenu"));
            accionEncabezado = extraerString(parametrosEntrada.get("accion"));
            titulo = nombreComprobante + " " + numeroComp;
        }
		
			numFormulario = GeneralCodigoFormaEnum.CA_CAUSACION_INGRESOS.getCodigo();
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

		enumBase = GenericUrlEnum.CA_CAUSACION_INGRESOS;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaConcepto(); cargarListaConceptoE();
		cargarListaTercero(); cargarListaTerceroE();
		cargarListaCentroCosto(); cargarListaCentroCostoE();
		cargarListaAuxiliar(); cargarListaAuxiliarE();
		cargarListaFuente(); cargarListaFuenteE();
		cargarListaReferencia(); cargarListaReferenciaE();
		//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaConcepto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaConcepto(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CausacioningresosautControladorUrlEnum.URL1997003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
		
        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaConcepto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaConceptoE(){
		listaConceptoE = listaConcepto;
	}
	/**
	 * 
	 * Carga la lista listaTercero
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTercero(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CausacioningresosautControladorUrlEnum.URL14001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");
	}
	/**
	 * 
	 * Carga la lista listaTercero
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaTerceroE(){
		listaTerceroE = listaTercero;
	}
	/**
	 * 
	 * Carga la lista listaCentroCosto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCentroCosto(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CausacioningresosautControladorUrlEnum.URL20026
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
		
		listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaCentroCosto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaCentroCostoE(){
		listaCentroCostoE = listaCentroCosto;
	}
	/**
	 * 
	 * Carga la lista listaAuxiliar
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAuxiliar(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CausacioningresosautControladorUrlEnum.URL23015
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		
        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaAuxiliar
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaAuxiliarE(){
		listaAuxiliarE = listaAuxiliar;
	}
	/**
	 * 
	 * Carga la lista listaFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaFuente(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CausacioningresosautControladorUrlEnum.URL34001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		
		listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaFuenteE(){
		listaFuenteE = listaFuente;
	}
	/**
	 * 
	 * Carga la lista listaReferencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaReferencia(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CausacioningresosautControladorUrlEnum.URL13001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		
		listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaReferencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaReferenciaE(){
		listaReferenciaE = listaReferencia;
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirAceptar() {
		//<CODIGO_DESARROLLADO>
		try {
			ejbContabilidadCinco.generarCausacionIng(compania, ano, tipoComp,numeroComp,SessionUtil.getUser().getCodigo());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
		} catch (NumberFormatException | SystemException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
				logger.error(e.getMessage(), e);
			}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Concepto en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarConceptoC(int rowNum) {
		// Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
		// Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Tercero en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarTerceroC(int rowNum) {
		// Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
		// Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConcepto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConcepto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO_ID", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMBRECONCEPTO", registroAux.getCampos().get("NOMBRE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConcepto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  registroAux.getCampos().get("CODIGO").toString();
		registro.getCampos().put("CONCEPTO_ID", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMBRECONCEPTO", registroAux.getCampos().get("NOMBRE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTercero
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTercero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));
		registro.getCampos().put("SUCURSAL", registroAux.getCampos().get("SUCURSAL"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTercero
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  registroAux.getCampos().get("NIT").toString();
		registro.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));
		registro.getCampos().put("SUCURSAL", registroAux.getCampos().get("SUCURSAL"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCentroCosto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CENTRO_COSTO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCentroCosto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCostoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliar
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("AUXILIAR", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliar
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FUENTE", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReferencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("REFERENCIA", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReferencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		asignarValoresRegistro();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		try {
			long aux = sysmanUtil.generarConsecutivoConValorInicial(
					"CA_CAUSACION_ING",
					"CA_CAUSACION_ING.COMPANIA =''" + compania + "''"
							+ " AND CA_CAUSACION_ING.ANO =" + ano
							+ " AND CA_CAUSACION_ING.TIPO = ''" + tipoComp
							+ "'' AND CA_CAUSACION_ING.NUMERO=" + numeroComp,
							"CONSECUTIVO", "1");
			
			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
			registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
			registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipoComp);
			registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numeroComp);
			registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), aux);
			removerCombos();
		} catch (SystemException ex)
		{
			Logger.getLogger(SubdetallecomprobantecntsControlador.class
					.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		asignarValoresRegistro();
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO CC4263 MPEREZ>		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoComp);
		param.put(GeneralParameterEnum.NUMERO.getName(),numeroComp);
		
		try {
			List<Registro> x = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ComprobantecntsControladorUrlEnum.URL13454.getValue())
									.getUrl(),
							param));
			if (!x.isEmpty() && (x.get(0).getCampos().get("X") != null)) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4369"));
				return false;
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//</CODIGO_DESARROLLADO CC4263 MPEREZ>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		asignarValoresRegistro();
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO CC4263 MPEREZ>
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoComp);
		param.put(GeneralParameterEnum.NUMERO.getName(),numeroComp);
		
		try {
			List<Registro> x = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ComprobantecntsControladorUrlEnum.URL13454.getValue())
									.getUrl(),
							param));
			if (!x.isEmpty() && (x.get(0).getCampos().get("X") != null)) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4369"));
				return false;
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//</CODIGO_DESARROLLADO CC4263 MPEREZ>		
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
		registro.getCampos().remove("NOMBRECONCEPTO");
	}
	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		registro.getCampos().put("TERCERO", tercero);
		registro.getCampos().put("SUCURSAL", sucursal);
		registro.getCampos().put("CENTRO_COSTO", SysmanConstantes.CONS_CENTRO);
		registro.getCampos().put("AUXILIAR", SysmanConstantes.CONS_AUXILIAR);
		registro.getCampos().put("REFERENCIA", SysmanConstantes.CONS_REFERENCIA);
		registro.getCampos().put("FUENTE", SysmanConstantes.CONS_FUENTE);
	}
	
	/**
     * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
     *
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object)
    {
        return object != null ? object.toString() : null;
    }
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaConcepto
	 * 
	 * @return listaConcepto
	 */
	public RegistroDataModelImpl getListaConcepto() {
		return listaConcepto;
	}
	/**
	 * Asigna la lista listaConcepto
	 * 
	 * @param listaConcepto
	 * Variable a asignar en  listaConcepto
	 */
	public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
		this.listaConcepto = listaConcepto;
	}
	/**
	 * Retorna la lista listaConcepto
	 * 
	 * @return listaConcepto
	 */
	public RegistroDataModelImpl getListaConceptoE() {
		return listaConceptoE;
	}
	/**
	 * Asigna la lista listaConcepto
	 * 
	 * @param listaConcepto
	 * Variable a asignar en  listaConcepto
	 */
	public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
		this.listaConceptoE = listaConceptoE;
	}
	/**
	 * Retorna la lista listaTercero
	 * 
	 * @return listaTercero
	 */
	public RegistroDataModelImpl getListaTercero() {
		return listaTercero;
	}
	/**
	 * Asigna la lista listaTercero
	 * 
	 * @param listaTercero
	 * Variable a asignar en  listaTercero
	 */
	public void setListaTercero(RegistroDataModelImpl listaTercero) {
		this.listaTercero = listaTercero;
	}
	/**
	 * Retorna la lista listaTercero
	 * 
	 * @return listaTercero
	 */
	public RegistroDataModelImpl getListaTerceroE() {
		return listaTerceroE;
	}
	/**
	 * Asigna la lista listaTercero
	 * 
	 * @param listaTercero
	 * Variable a asignar en  listaTercero
	 */
	public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
		this.listaTerceroE = listaTerceroE;
	}
	/**
	 * Retorna la lista listaCentroCosto
	 * 
	 * @return listaCentroCosto
	 */
	public RegistroDataModelImpl getListaCentroCosto() {
		return listaCentroCosto;
	}
	/**
	 * Asigna la lista listaCentroCosto
	 * 
	 * @param listaCentroCosto
	 * Variable a asignar en  listaCentroCosto
	 */
	public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}
	/**
	 * Retorna la lista listaCentroCosto
	 * 
	 * @return listaCentroCosto
	 */
	public RegistroDataModelImpl getListaCentroCostoE() {
		return listaCentroCostoE;
	}
	/**
	 * Asigna la lista listaCentroCosto
	 * 
	 * @param listaCentroCosto
	 * Variable a asignar en  listaCentroCosto
	 */
	public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE) {
		this.listaCentroCostoE = listaCentroCostoE;
	}
	/**
	 * Retorna la lista listaAuxiliar
	 * 
	 * @return listaAuxiliar
	 */
	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}
	/**
	 * Asigna la lista listaAuxiliar
	 * 
	 * @param listaAuxiliar
	 * Variable a asignar en  listaAuxiliar
	 */
	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}
	/**
	 * Retorna la lista listaAuxiliar
	 * 
	 * @return listaAuxiliar
	 */
	public RegistroDataModelImpl getListaAuxiliarE() {
		return listaAuxiliarE;
	}
	/**
	 * Asigna la lista listaAuxiliar
	 * 
	 * @param listaAuxiliar
	 * Variable a asignar en  listaAuxiliar
	 */
	public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE) {
		this.listaAuxiliarE = listaAuxiliarE;
	}
	/**
	 * Retorna la lista listaFuente
	 * 
	 * @return listaFuente
	 */
	public RegistroDataModelImpl getListaFuente() {
		return listaFuente;
	}
	/**
	 * Asigna la lista listaFuente
	 * 
	 * @param listaFuente
	 * Variable a asignar en  listaFuente
	 */
	public void setListaFuente(RegistroDataModelImpl listaFuente) {
		this.listaFuente = listaFuente;
	}
	/**
	 * Retorna la lista listaFuente
	 * 
	 * @return listaFuente
	 */
	public RegistroDataModelImpl getListaFuenteE() {
		return listaFuenteE;
	}
	/**
	 * Asigna la lista listaFuente
	 * 
	 * @param listaFuente
	 * Variable a asignar en  listaFuente
	 */
	public void setListaFuenteE(RegistroDataModelImpl listaFuenteE) {
		this.listaFuenteE = listaFuenteE;
	}
	/**
	 * Retorna la lista listaReferencia
	 * 
	 * @return listaReferencia
	 */
	public RegistroDataModelImpl getListaReferencia() {
		return listaReferencia;
	}
	/**
	 * Asigna la lista listaReferencia
	 * 
	 * @param listaReferencia
	 * Variable a asignar en  listaReferencia
	 */
	public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
		this.listaReferencia = listaReferencia;
	}
	/**
	 * Retorna la lista listaReferencia
	 * 
	 * @return listaReferencia
	 */
	public RegistroDataModelImpl getListaReferenciaE() {
		return listaReferenciaE;
	}
	/**
	 * Asigna la lista listaReferencia
	 * 
	 * @param listaReferencia
	 * Variable a asignar en  listaReferencia
	 */
	public void setListaReferenciaE(RegistroDataModelImpl listaReferenciaE) {
		this.listaReferenciaE = listaReferenciaE;
	}
	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 * Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar= auxiliar;
	}
	
	public String getTitulo() {
		return titulo;
	}
	
	public void setTitulo(String titulo) {
		this.titulo= titulo;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
