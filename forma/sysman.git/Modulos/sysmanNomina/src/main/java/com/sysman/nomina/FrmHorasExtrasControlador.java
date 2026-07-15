/*-
 * FrmHorasExtrasControlador.java
 *
 * 1.0
 * 
 * 06/09/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmHorasExtrasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.nomina.ejb.EjbNominaDosRemote;


/**
 *
 * @version 1.0, 06/09/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmHorasExtrasControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;

	private int anioFiltro;
	private int anoDuplicar;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>

	private List<Registro> listaAnio;
	private List<Registro> listaAnioFiltro;
	private List<Registro> listaanoDuplicar;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaConcepto;
	private RegistroDataModelImpl listaConceptoE;
	private RegistroDataModelImpl listaConceptoRelacionado;
	private RegistroDataModelImpl listaConceptoRelacionadoE;
	
	private boolean confirmar;
	private String mensajeDialogo;

	@EJB
	private EjbNominaDosRemote ejbNominaDosRemote;
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;

	private String nombreConcepto;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmHorasExtrasControlador
	 */
	public FrmHorasExtrasControlador() {
		super();
		compania = SessionUtil.getCompania();
		anioFiltro = SysmanFunciones.ano(new Date());
		try {
			numFormulario=2424;
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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

		enumBase = GenericUrlEnum.HORAS_EXTRAS;
		confirmar = false;
		
		buscarLlave();
		reasignarOrigen();		

		registro= new Registro();
		//<CARGAR_LISTA>
		cargarListaAnio();
		cargarListaAnioFiltro();
		cargarListaanoDuplicar();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaConcepto();
		cargarListaConceptoRelacionado(); 
		cargarListaConceptoRelacionadoE();
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
		parametrosListado.put(GeneralParameterEnum.ANO.getName(),
				anioFiltro);

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
	}
	
	public void duplicarHE()
    {
    	try
        {
            ejbNominaDosRemote.duplicarHE	(
                    						compania,
                    						anoDuplicar,
                    						anioFiltro,
                    						SessionUtil.getUser().toString()
                    						);
            
            JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_PROCESO_EJECUTADO));
        }
        catch (NumberFormatException | SystemException ex)
        {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), " ", ex.getMessage()));
            Logger.getLogger(ActualizarSueldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }
	
	public void oprimirduplicar()
    {	
		if ( anioFiltro == 0) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB3001"));
			confirmar = false;
		} else if ( anoDuplicar == 0 ) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB3002"));
			confirmar = false;
		} else if (anioFiltro >= anoDuplicar) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB3003"));
			confirmar = false;			
		} else {
			mensajeDialogo = idioma.getString("TB_TB3000").replace("#$ano#$", Integer.toString(anioFiltro)).replace("#$anoDuplicar#$", Integer.toString(anoDuplicar));
			confirmar = true;
		}
    }
	
	public void aceptarconfirmar()
    {
        confirmar = false;
        duplicarHE();
    }
	
	public void cancelarconfirmar()
    {
        confirmar = false;
    }
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmHorasExtrasControladorUrlEnum.URL4001
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
	 * Carga la lista listaAnioFiltro
	 *
	 */
	public void cargarListaanoDuplicar(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaanoDuplicar = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmHorasExtrasControladorUrlEnum.URL4001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void cargarListaAnioFiltro(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnioFiltro = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmHorasExtrasControladorUrlEnum.URL4001
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
	 * Carga la lista listaConcepto
	 *
	 */
	public void cargarListaConcepto(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID( FrmHorasExtrasControladorUrlEnum.URL151001.getValue());
		listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, "ID_DE_CONCEPTO");

	}
	/**
	 * 
	 * Carga la lista listaConcepto
	 *
	 */
	public void  cargarListaConceptoE(){
		//listaConceptoE = new RegistroDataModelImpl(ConectorPool.ESQUEMA_SYSMAN, ":FRFR2424:TBCB8334","SELECT DISTINCT"+
		//"     CONCEPTOS.ID_DE_CONCEPTO, "+
		//"     CONCEPTOS.NOMBRE_CONCEPTO "+
		//" FROM CONCEPTOS"+
		//" ORDER BY CONCEPTOS.ID_DE_CONCEPTO",true,"ID_DE_CONCEPTO");
	}
	/**
	 * 
	 * Carga la lista listaConceptoRelacionado
	 *
	 */
	public void cargarListaConceptoRelacionado(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID( FrmHorasExtrasControladorUrlEnum.URL151001.getValue());
		listaConceptoRelacionado = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, "ID_DE_CONCEPTO");

	}
	/**
	 * 
	 * Carga la lista listaConceptoRelacionado
	 *
	 */
	public void  cargarListaConceptoRelacionadoE(){

		listaConceptoRelacionadoE = listaConceptoRelacionado;

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control AnioFiltro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAnioFiltro() {
		//<CODIGO_DESARROLLADO>
		reasignarOrigen();
		//</CODIGO_DESARROLLADO>
	}
	
	public void cambiaranoDuplicar() {
		
	}
	/**
	 * Metodo ejecutado al cambiar el control ConceptoRelacionado en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarConceptoRelacionadoC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_RELACIONADO", nombreConcepto);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConcepto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConcepto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO", registroAux.getCampos().get("ID_DE_CONCEPTO"));
		registro.getCampos().put("NOMBRE_CONCEPTO", registroAux.getCampos().get("NOMBRE_CONCEPTO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConcepto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("ID_DE_CONCEPTO");
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConceptoRelacionado
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoRelacionado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO_RELACIONADO", registroAux.getCampos().get("ID_DE_CONCEPTO"));
		registro.getCampos().put("NOMBRE_RELACIONADO", registroAux.getCampos().get("NOMBRE_CONCEPTO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConceptoRelacionado
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoRelacionadoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_CONCEPTO"));
		nombreConcepto = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_CONCEPTO"));
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
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>

		try {
			long codigo = ejbSysmanUtil.generarConsecutivoConValorInicial(
					"HORAS_EXTRAS",null, "CODIGO", "1");


			registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
					codigo);

			registro.getCampos().remove("NOMBRE_CONCEPTO");
			registro.getCampos().remove("NOMBRE_RELACIONADO");

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().remove("NOMBRE_CONCEPTO");
		registro.getCampos().remove("NOMBRE_RELACIONADO");
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
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

	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// TODO Auto-generated method stub
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * @return the anioFiltro
	 */
	public int getAnioFiltro() {
		return anioFiltro;
	}
	/**
	 * @param anioFiltro the anioFiltro to set
	 */
	public void setAnioFiltro(int anioFiltro) {
		this.anioFiltro = anioFiltro;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}
	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio
	 * Variable a asignar en  listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}
	/**
	 * @return the listaAnioFiltro
	 */
	public List<Registro> getListaAnioFiltro() {
		return listaAnioFiltro;
	}
	/**
	 * @param listaAnioFiltro the listaAnioFiltro to set
	 */
	public void setListaAnioFiltro(List<Registro> listaAnioFiltro) {
		this.listaAnioFiltro = listaAnioFiltro;
	}

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
	 * Retorna la lista listaConceptoRelacionado
	 * 
	 * @return listaConceptoRelacionado
	 */
	public RegistroDataModelImpl getListaConceptoRelacionado() {
		return listaConceptoRelacionado;
	}
	/**
	 * Asigna la lista listaConceptoRelacionado
	 * 
	 * @param listaConceptoRelacionado
	 * Variable a asignar en  listaConceptoRelacionado
	 */
	public void setListaConceptoRelacionado(RegistroDataModelImpl listaConceptoRelacionado) {
		this.listaConceptoRelacionado = listaConceptoRelacionado;
	}
	/**
	 * Retorna la lista listaConceptoRelacionado
	 * 
	 * @return listaConceptoRelacionado
	 */
	public RegistroDataModelImpl getListaConceptoRelacionadoE() {
		return listaConceptoRelacionadoE;
	}
	/**
	 * Asigna la lista listaConceptoRelacionado
	 * 
	 * @param listaConceptoRelacionado
	 * Variable a asignar en  listaConceptoRelacionado
	 */
	public void setListaConceptoRelacionadoE(RegistroDataModelImpl listaConceptoRelacionadoE) {
		this.listaConceptoRelacionadoE = listaConceptoRelacionadoE;
	}
	/**
	 * @return the auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
	/**
	 * @param auxiliar the auxiliar to set
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
	public boolean isConfirmar() {
		return confirmar;
	}
	public void setConfirmar(boolean confirmar) {
		this.confirmar = confirmar;
	}
	public String getMensajeDialogo() {
		return mensajeDialogo;
	}
	public void setMensajeDialogo(String mensajeDialogo) {
		this.mensajeDialogo = mensajeDialogo;
	}
	public int getAnoDuplicar() {
		return anoDuplicar;
	}
	public void setAnoDuplicar(int anoDuplicar) {
		this.anoDuplicar = anoDuplicar;
	}
	public List<Registro> getListaanoDuplicar() {
		return listaanoDuplicar;
	}
	public void setListaanoDuplicar(List<Registro> listaanoDuplicar) {
		this.listaanoDuplicar = listaanoDuplicar;
	}


	//</SET_GET_LISTAS_COMBO_GRANDE>
}
