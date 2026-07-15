/*-
 * FrmTipoProveedorControlador.java
 *
 * 1.0
 * 
 * 06/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.FrmTipoProveedorControladorUrlEnum;
import com.sysman.contabilidad.enums.TasasinteresesControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 06/05/2024
 * @author User
 */
@ManagedBean
@ViewScoped
public class  FrmTipoProveedorControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	
	  @EJB
	 EjbContabilidadTresRemote ejbContabilidadTres;
	    
	private StreamedContent archivoDescarga;

	private final String compania;

	private String anio;
	/*
	 * Variable almacena el anio destino para ejecutar la rutina
	 */
	private String anioDestino;	

	private RegistroDataModelImpl listacuentaCredito;

	private RegistroDataModelImpl listacuentaCreditoE;

	private List<Registro> listaano;

	private List<Registro> listaanioDestino;


	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	/**
	 * Crea una nueva instancia de FrmTipoProveedorControlador
	 */
	public FrmTipoProveedorControlador() {
		super();
		compania = SessionUtil.getCompania();
		anio = String.valueOf(SysmanFunciones.ano(new Date()));

		Calendar calendario = new GregorianCalendar();
		anioDestino = String.valueOf(calendario.get(Calendar.YEAR) + 1); 

		try {
			numFormulario=GeneralCodigoFormaEnum.FRM_TIPO_PROVEEDORES_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			SessionUtil.cleanFlash();
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
		enumBase=GenericUrlEnum.TIPO_PROVEEDORES;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		cargarListacuentaCredito(); 
		cargarListacuentaCreditoE();
		cargarListaano();
		cargarListaanioDestino();

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

		parametrosListado.put("COMPANIA", compania);
		parametrosListado.put("ANO", anio);
	}
	/**
	 * 
	 * Carga la lista listacuentaCredito
	 *
	 */
	public void cargarListacuentaCredito(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio); 

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmTipoProveedorControladorUrlEnum.URL16221
						.getValue());

		listacuentaCredito = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaano(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaano = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTipoProveedorControladorUrlEnum.URL4001.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}		
	}
	/**
	 * 
	 * Carga la lista listacuentaCredito
	 *
	 */
	public void  cargarListacuentaCreditoE(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio); 

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmTipoProveedorControladorUrlEnum.URL16221
						.getValue());

		listacuentaCreditoE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	
	public void cargarListaanioDestino(){
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        
		try {
			listaanioDestino = RegistroConverter.toListRegistro(
			        requestManager.getList(UrlServiceUtil.getInstance()
			                .getUrlServiceByUrlByEnumID(
			                		FrmTipoProveedorControladorUrlEnum.URL4001
			                                                .getValue())
			                .getUrl(), param));
		} catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void oprimirPreperarAnio() {
	    try {
	        int anioOrigenInt = Integer.parseInt(anio);
	        int anioDestinoInt = Integer.parseInt(anioDestino);
	        
	        String mensajeResultado = ejbContabilidadTres.pasarProvedores(
	            compania,
	            anioOrigenInt,
	            anioDestinoInt
	        );
	        
	      
	        archivoDescarga = JsfUtil.getArchivoDescarga(
	            JsfUtil.serializarPlano(mensajeResultado),
	            "Reporte_Proveedores.txt"
	        );
	        
	      
	        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB506"));
	        
	    } catch (SystemException | JRException | IOException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	}

	public void cambiarano() {
		reasignarOrigen();
		cargarListacuentaCreditoE();
		cargarListacuentaCredito();
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaCredito
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaCredito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTACREDITO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaCredito
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaCreditoE(SelectEvent event) {
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
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", anio);
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 */
	@Override
	public boolean insertarDespues(){

		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 */
	@Override
	public boolean actualizarAntes(){

		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){

		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 *
	 */
	@Override    
	public boolean eliminarAntes(){

		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 */
	@Override   
	public boolean eliminarDespues(){

		return true;
	}

	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacuentaCredito
	 * 
	 * @return listacuentaCredito
	 */
	public RegistroDataModelImpl getListacuentaCredito() {
		return listacuentaCredito;
	}
	/**
	 * Asigna la lista listacuentaCredito
	 * 
	 * @param listacuentaCredito
	 * Variable a asignar en  listacuentaCredito
	 */
	public void setListacuentaCredito(RegistroDataModelImpl listacuentaCredito) {
		this.listacuentaCredito = listacuentaCredito;
	}
	/**
	 * Retorna la lista listacuentaCredito
	 * 
	 * @return listacuentaCredito
	 */
	public RegistroDataModelImpl getListacuentaCreditoE() {
		return listacuentaCreditoE;
	}
	/**
	 * Asigna la lista listacuentaCredito
	 * 
	 * @param listacuentaCredito
	 * Variable a asignar en  listacuentaCredito
	 */
	public void setListacuentaCreditoE(RegistroDataModelImpl listacuentaCreditoE) {
		this.listacuentaCreditoE = listacuentaCreditoE;
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
	/**
	 * @return the anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * @param anio the anio to set
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * @return the listaano
	 */
	public List<Registro> getListaano() {
		return listaano;
	}
	/**
	 * @param listaano the listaano to set
	 */
	public void setListaano(List<Registro> listaano) {
		this.listaano = listaano;
	}
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub

	}
	@Override
	public void removerCombos() {
		// TODO Auto-generated method stub

	}
	/**
	 * @return the anioDestino
	 */
	public String getAnioDestino() {
		return anioDestino;
	}
	/**
	 * @param anioDestino the anioDestino to set
	 */
	public void setAnioDestino(String anioDestino) {
		this.anioDestino = anioDestino;
	}
	/**
	 * @return the listaanioDestino
	 */
	public List<Registro> getListaanioDestino() {
		return listaanioDestino;
	}
	/**
	 * @param listaanioDestino the listaanioDestino to set
	 */
	public void setListaanioDestino(List<Registro> listaanioDestino) {
		this.listaanioDestino = listaanioDestino;
	}
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	
	
	


}
