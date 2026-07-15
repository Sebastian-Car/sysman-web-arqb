/*-
 * SubresoluciondiansControlador.java
 *
 * 1.0
 * 
 * 11/10/2022 cperez
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;


import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.SubresoluciondiansControladorUrlEnum;
import com.sysman.contabilidad.enums.TipocomprobantecsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import com.sysman.services.RegistroDataModelImpl;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/10/2022
 * @author cperez2
 */
@ManagedBean
@ViewScoped
public class  SubresoluciondiansControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listacmbTipoFacDian;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listacmbTipoMoneda;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listacmbMedioPago;

	private List<Registro> listaExisteDato;

	private Map<String, Object> parametrosEntrada;
	private String tipoComprobante;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de SubresoluciondiansControlador
	 */
	public SubresoluciondiansControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.RESOLUCIONDIAN_CT.getCodigo();;
			//numFormulario=2369;
			validarPermisos();
			parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				tipoComprobante = SysmanFunciones.nvl(parametrosEntrada.get("CODIGO"), "0")
						.toString();
			}
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
		enumBase = GenericUrlEnum.RESOLUCIONDIAN_CT;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		//<CARGAR_LISTA>
		cargarListacmbTipoFacDian();
		cargarListacmbTipoMoneda();
		cargarListacmbMedioPago();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
		registro.getCampos().put("TIPOCOMPROBANTE", tipoComprobante);

	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		/*
		 * Se obtiene los clases configuradas en el parametro CLASES COMPROBANTE
		 * CONFIGURAR CLASIFICADORES
		 */

		parametrosListado.put(GeneralParameterEnum.COMPANNIA.getName(),
				compania);

		parametrosListado.put(GeneralParameterEnum.TIPOCOMP.getName(), tipoComprobante);

		buscarUrls();
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacmbTipoFacDian
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbTipoFacDian(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.TIPO.getName(),
				5);

		try {
			listacmbTipoFacDian = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubresoluciondiansControladorUrlEnum.URL1848006
									.getValue())
							.getUrl(),
							param));
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacmbTipoMoneda
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbTipoMoneda(){
		/*	listacmbTipoMoneda = service.getListado(conectorPool, "SELECT "+
				"     TIPOMONEDA_SF.CODIGO, "+
				"     TIPOMONEDA_SF.DESCRIPCION "+
				" FROM "+
				"     TIPOMONEDA_SF "+
				" WHERE "+
				"     (((TIPOMONEDA_SF.COMPANIA) = '"+compania+"'))");*/
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		try {
			listacmbTipoMoneda = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubresoluciondiansControladorUrlEnum.URL1853001
									.getValue())
							.getUrl(),
							param));
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacmbMedioPago
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbMedioPago(){
		/*listacmbMedioPago = service.getListado(conectorPool, "SELECT "+
				"     MEDIO_PAGO_SF.CODIGO, "+
				"     MEDIO_PAGO_SF.DESCRIPCION "+
				" FROM "+
				"     MEDIO_PAGO_SF "+
				" WHERE "+
				"     (((MEDIO_PAGO_SF.COMPANIA) = '"+compania+"')) "+
				" ");*/
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		try {
			listacmbMedioPago = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							SubresoluciondiansControladorUrlEnum.URL1860001
							.getValue())
					.getUrl(),
					param));


		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	public void cambiartxtFechaFinal(){
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TIPOCOMPROBANTE", registro.getCampos().get("TIPOCOMPROBANTE"));
		param.put(GeneralParameterEnum.FECHAFINAL.getName(), registro.getCampos().get("FECHAFINAL"));
		param.put(GeneralParameterEnum.FECHAINICIAL.getName(), registro.getCampos().get("FECHAINICIAL"));
		param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get("NUMERO"));
		try {
			listaExisteDato = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubresoluciondiansControladorUrlEnum.URL1896001
											.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if(!listaExisteDato.isEmpty() && listaExisteDato != null ) {
			for (Registro option : listaExisteDato) {
				JsfUtil.agregarMensajeError("La Fecha ingresada ya existe. Resolución: " + option.getCampos().get("EXISTE").toString());
				registro.getCampos().put("FECHAFINAL", null);
				return;
			} 
		}
		try {

			listaExisteDato =  null;
			listaExisteDato = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubresoluciondiansControladorUrlEnum.URL1896002
											.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if(!listaExisteDato.isEmpty() && listaExisteDato != null ) {
			for (Registro option : listaExisteDato) {
				JsfUtil.agregarMensajeError("La Fecha ingresada se cruza con la Resolución: " + option.getCampos().get("EXISTE").toString());
				registro.getCampos().put("FECHAFINAL", null);
				return;
			} 
		}

		try {

			listaExisteDato =  null;
			listaExisteDato = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubresoluciondiansControladorUrlEnum.URL1896003
											.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if(!listaExisteDato.isEmpty() && listaExisteDato != null ) {
			for (Registro option : listaExisteDato) {
				JsfUtil.agregarMensajeError("La Fecha ingresada se cruza con la Resolución: " + option.getCampos().get("EXISTE").toString());
				registro.getCampos().put("FECHAFINAL",null);
				return;
			} 
		}
	}
	public void cambiartxtFechaInicialC(int rowNum) {
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHAFINAL", null);

	}
	public void cambiartxtConsecutivoInicialC(int rowNum) {
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CONSECUTIVOFIN", null);

	}
	public void cambiartxtConsecutivoFinalC(int rowNum) {
		if( Integer.parseInt(   listaInicial.getDatasource().get(rowNum % 10).getCampos().get("CONSECUTIVOFIN").toString()) < Integer.parseInt(   listaInicial.getDatasource().get(rowNum % 10).getCampos().get("CONSECUTIVOINI").toString()) ) {
			JsfUtil.agregarMensajeAlerta("El Consecutivo Inicial debe ser mayor o igual  al Final.");
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CONSECUTIVOFIN", null);
		}
	}
	public void cambiartxtNumeroC(int rowNum) {
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("TIPOCOMPROBANTE", tipoComprobante);
		
	}
	
	public void cambiartxtFechaFinalC(int rowNum) {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TIPOCOMPROBANTE", 
				listaInicial.getDatasource().get(rowNum % 10)
				.getCampos().get("TIPOCOMPROBANTE"));
		param.put(GeneralParameterEnum.FECHAFINAL.getName(), 
				listaInicial.getDatasource().get(rowNum % 10)
				.getCampos().get("FECHAFINAL"));
		param.put(GeneralParameterEnum.FECHAINICIAL.getName(), 
				listaInicial.getDatasource().get(rowNum % 10)
				.getCampos().get("FECHAINICIAL"));
		param.put(GeneralParameterEnum.NUMERO.getName(), 
				listaInicial.getDatasource().get(rowNum % 10)
				.getCampos().get("NUMERO"));
		try {
			listaExisteDato = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubresoluciondiansControladorUrlEnum.URL1896001
											.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if(!listaExisteDato.isEmpty() && listaExisteDato != null ) {
			for (Registro option : listaExisteDato) {
				JsfUtil.agregarMensajeError("La Fecha ingresada ya existe. Resolución: " + option.getCampos().get("EXISTE").toString());

				listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHAFINAL", null);
				return;
			} 
		}
		try {

			listaExisteDato =  null;
			listaExisteDato = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubresoluciondiansControladorUrlEnum.URL1896002
											.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if(!listaExisteDato.isEmpty() && listaExisteDato != null ) {
			for (Registro option : listaExisteDato) {
				JsfUtil.agregarMensajeError("La Fecha ingresada se cruza con la Resolución: " + option.getCampos().get("EXISTE").toString());
				listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.put("FECHAFINAL", null);
				return;
			} 
		}

		try {

			listaExisteDato =  null;
			listaExisteDato = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubresoluciondiansControladorUrlEnum.URL1896003
											.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if(!listaExisteDato.isEmpty() && listaExisteDato != null ) {
			for (Registro option : listaExisteDato) {
				JsfUtil.agregarMensajeError("La Fecha ingresada se cruza con la Resolución: " + option.getCampos().get("EXISTE").toString());
				listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.put("FECHAFINAL", null);
				return;
			} 
		}

	}


	public void cambiartxtConsecutivoFinal(){
		if( Integer.parseInt( registro.getCampos().get("CONSECUTIVOFIN").toString()) <Integer.parseInt(  registro.getCampos().get("CONSECUTIVOINI").toString()) ) {
			JsfUtil.agregarMensajeAlerta("El Consecutivo Inicial debe ser mayor o igual  al Final.");
			//System.out.println("El Consecutivo Inicial debe ser mayor o igual  al Final.");
			registro.getCampos().put("CONSECUTIVOFIN", null);
		}
	}






	//METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control txtNumero
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiartxtNumero() {
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("TIPOCOMPROBANTE", tipoComprobante);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control txtFechaInicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiartxtFechaInicial() {
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("FECHAFINAL", null);
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control txtConsecutivoInicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiartxtConsecutivoInicial() {
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("CONSECUTIVOFIN", null);
		//</CODIGO_DESARROLLADO>
	}



	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
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

	public void ejecutarrcCerrar(){
		//<CODIGO_DESARROLLADO>
		Map<String, Object> param = new HashMap<>();
		param.put("CODIGO", tipoComprobante);

		Direccionador direccionador = new Direccionador();

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.TIPOCOMPROBANTECS_CONTROLADOR
						.getCodigo()));

		direccionador.setParametros(param);
		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
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
		registro.getCampos().put("COMPANIA", compania);
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
		registro.getCampos().put("TIPOCOMPROBANTE", tipoComprobante);
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
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
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
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
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
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listacmbTipoFacDian
	 * 
	 * @return listacmbTipoFacDian
	 */
	public List<Registro> getListacmbTipoFacDian() {
		return listacmbTipoFacDian;
	}
	/**
	 * Asigna la lista listacmbTipoFacDian
	 * 
	 * @param listacmbTipoFacDian
	 * Variable a asignar en  listacmbTipoFacDian
	 */
	public void setListacmbTipoFacDian(List<Registro> listacmbTipoFacDian) {
		this.listacmbTipoFacDian = listacmbTipoFacDian;
	}
	/**
	 * Retorna la lista listacmbTipoMoneda
	 * 
	 * @return listacmbTipoMoneda
	 */
	public List<Registro> getListacmbTipoMoneda() {
		return listacmbTipoMoneda;
	}
	/**
	 * Asigna la lista listacmbTipoMoneda
	 * 
	 * @param listacmbTipoMoneda
	 * Variable a asignar en  listacmbTipoMoneda
	 */
	public void setListacmbTipoMoneda(List<Registro> listacmbTipoMoneda) {
		this.listacmbTipoMoneda = listacmbTipoMoneda;
	}
	/**
	 * Retorna la lista listacmbMedioPago
	 * 
	 * @return listacmbMedioPago
	 */
	public List<Registro> getListacmbMedioPago() {
		return listacmbMedioPago;
	}
	/**
	 * Asigna la lista listacmbMedioPago
	 * 
	 * @param listacmbMedioPago
	 * Variable a asignar en  listacmbMedioPago
	 */
	public void setListacmbMedioPago(List<Registro> listacmbMedioPago) {
		this.listacmbMedioPago = listacmbMedioPago;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
