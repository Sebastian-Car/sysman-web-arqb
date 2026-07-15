/*-
 * Frmreferenciadoconceptoservicio.java
 *
 * 1.0
 * 
 * 30/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.FrmreferenciadoconceptoservicioUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import java.io.IOException;

/**
 *
 * @version 1.0, 30/05/2024
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class Frmreferenciadoconceptoservicio extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private String ano;
	private String anioDestino;
	private String auxiliar;
	private StreamedContent archivoDescarga;

	private RegistroDataModelImpl listaCuentaContable;
	private RegistroDataModelImpl listaCuentaContableE;
	private RegistroDataModelImpl listaServicio;
	private RegistroDataModelImpl listaServicioE;
	private RegistroDataModelImpl listaReferenciado;
	private RegistroDataModelImpl listaReferenciadoE;

	private List<Registro> listaAno;
	private List<Registro>listaAnioDestino;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	
	@EJB
	 EjbContabilidadTresRemote ejbContabilidadTres;
	    


	/**
	 * Crea una nueva instancia de Frmreferenciadoconceptoservicio
	 */
	public Frmreferenciadoconceptoservicio() {
		super();
		Calendar calendario = new GregorianCalendar();
		compania = SessionUtil.getCompania();
		ano = String.valueOf(SysmanFunciones.ano(new Date()));
		anioDestino = String.valueOf(calendario.get(Calendar.YEAR) + 1); 
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_REFERENCIADO_CONCEPTO_SERVICIO.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
			Logger.getLogger(TiporetencionsControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			SessionUtil.cleanFlash();
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

		enumBase = GenericUrlEnum.REFERENCIADOS;
		reasignarOrigen();
		buscarLlave();
		registro = new Registro();
		cargarListaAno();
		cargarListaAnioOrigen();
		abrirFormulario();

	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();
	}

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
											FrmreferenciadoconceptoservicioUrlEnum.URL4001.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCuenta Contable
	 *
	 */
	public void cargarListaCuentaContable() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmreferenciadoconceptoservicioUrlEnum.URL16221.getValue());

		listaCuentaContable = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaCuenta Contable
	 *
	 */
	public void cargarListaCuentaContableE() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmreferenciadoconceptoservicioUrlEnum.URL16221.getValue());

		listaCuentaContableE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaServicio
	 *
	 */
	public void cargarListaServicio() {

		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmreferenciadoconceptoservicioUrlEnum.URL1930002.getValue());
		listaServicio = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaServicio
	 *
	 */
	public void cargarListaServicioE() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmreferenciadoconceptoservicioUrlEnum.URL1930002.getValue());
		listaServicioE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaReferenciado
	 *
	 */
	public void cargarListaReferenciado() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmreferenciadoconceptoservicioUrlEnum.URL15627.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaReferenciado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaReferenciado
	 */
	public void cargarListaReferenciadoE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmreferenciadoconceptoservicioUrlEnum.URL15627.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaReferenciadoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	

	public void cargarListaAnioOrigen(){
		 Map<String, Object> param = new TreeMap<>();
       param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
       
		try {
			listaAnioDestino = RegistroConverter.toListRegistro(
			        requestManager.getList(UrlServiceUtil.getInstance()
			                .getUrlServiceByUrlByEnumID(
			                		FrmreferenciadoconceptoservicioUrlEnum.URL4001
			                                                .getValue())
			                .getUrl(), param));
		} catch (SystemException e) {
           JsfUtil.agregarMensajeError(e.getMessage());
			e.printStackTrace();
		}
	}

	public void oprimirPrepararAnio() {
	    try {
	        int anioOrigenInt = Integer.parseInt(ano);
	        int anioDestinoInt = Integer.parseInt(anioDestino);
	        
	        String mensajeResultado = ejbContabilidadTres.pasarReferenciados(
	            compania,
	            anioOrigenInt,
	            anioDestinoInt
	        );
	        
	        archivoDescarga = JsfUtil.getArchivoDescarga(
	            JsfUtil.serializarPlano(mensajeResultado),
	            "Reporte_Proveedores.txt"
	        );
	        
	        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB506"));
	        
	    } catch (Exception e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		String anoRegistro = SysmanFunciones.toString(
				registro.getCampos().get("ANO"));

		if (anoRegistro != null && !anoRegistro.trim().isEmpty()) {
			ano = anoRegistro;
		}
		
		cargarListaCuentaContable();
		cargarListaCuentaContableE();
		cargarListaServicio();
		cargarListaServicioE();
		cargarListaReferenciado();
		cargarListaReferenciadoE();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuenta Contable
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaContable(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTACONTABLE", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuenta Contable
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaContableE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaServicio
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaServicio(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaServicio
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaServicioE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciado
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciado
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciadoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
	}

//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		cargarListaCuentaContable();
		cargarListaCuentaContableE();
		cargarListaServicio();
		cargarListaServicioE();
		cargarListaReferenciado();
		cargarListaReferenciadoE();
		// </CODIGO_DESARROLLADO>
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
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
	}

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
	 * Retorna la lista listaCuenta Contable
	 * 
	 * @return listaCuenta Contable
	 */
	public RegistroDataModelImpl getListaCuentaContable() {
		return listaCuentaContable;
	}

	/**
	 * Asigna la lista listaCuenta Contable
	 * 
	 * @param listaCuenta Contable Variable a asignar en listaCuenta Contable
	 */
	public void setListaCuentaContable(RegistroDataModelImpl listaCuentaContable) {
		this.listaCuentaContable = listaCuentaContable;
	}

	/**
	 * Retorna la lista listaCuenta Contable
	 * 
	 * @return listaCuenta Contable
	 */
	public RegistroDataModelImpl getListaCuentaContableE() {
		return listaCuentaContableE;
	}

	/**
	 * Asigna la lista listaCuenta Contable
	 * 
	 * @param listaCuenta Contable Variable a asignar en listaCuenta Contable
	 */
	public void setListaCuentaContableE(RegistroDataModelImpl listaCuentaContableE) {
		this.listaCuentaContableE = listaCuentaContableE;
	}

	/**
	 * Retorna la lista listaServicio
	 * 
	 * @return listaServicio
	 */
	public RegistroDataModelImpl getListaServicio() {
		return listaServicio;
	}

	/**
	 * Asigna la lista listaServicio
	 * 
	 * @param listaServicio Variable a asignar en listaServicio
	 */
	public void setListaServicio(RegistroDataModelImpl listaServicio) {
		this.listaServicio = listaServicio;
	}

	/**
	 * Retorna la lista listaServicio
	 * 
	 * @return listaServicio
	 */
	public RegistroDataModelImpl getListaServicioE() {
		return listaServicioE;
	}

	/**
	 * Asigna la lista listaServicio
	 * 
	 * @param listaServicio Variable a asignar en listaServicio
	 */
	public void setListaServicioE(RegistroDataModelImpl listaServicioE) {
		this.listaServicioE = listaServicioE;
	}

	/**
	 * Retorna la lista listaReferenciado
	 * 
	 * @return listaReferenciado
	 */
	public RegistroDataModelImpl getListaReferenciado() {
		return listaReferenciado;
	}

	/**
	 * Asigna la lista listaReferenciado
	 * 
	 * @param listaReferenciado Variable a asignar en listaReferenciado
	 */
	public void setListaReferenciado(RegistroDataModelImpl listaReferenciado) {
		this.listaReferenciado = listaReferenciado;
	}

	/**
	 * Retorna la lista listaReferenciado
	 * 
	 * @return listaReferenciado
	 */
	public RegistroDataModelImpl getListaReferenciadoE() {
		return listaReferenciadoE;
	}

	/**
	 * Asigna la lista listaReferenciado
	 * 
	 * @param listaReferenciado Variable a asignar en listaReferenciado
	 */
	public void setListaReferenciadoE(RegistroDataModelImpl listaReferenciadoE) {
		this.listaReferenciadoE = listaReferenciadoE;
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
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	/**
	 * @return the ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * @param ano the ano to set
	 */
	public void setAno(String ano) {
		this.ano = ano;
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
	 * @return the listaAnioDestino
	 */
	public List<Registro> getListaAnioDestino() {
		return listaAnioDestino;
	}

	/**
	 * @param listaAnioDestino the listaAnioDestino to set
	 */
	public void setListaAnioDestino(List<Registro> listaAnioDestino) {
		this.listaAnioDestino = listaAnioDestino;
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

	/**
	 * @return the listaAnioOrigen
	 */

	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
