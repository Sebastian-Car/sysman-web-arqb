/*-
 * FrmgenerararchivosplanoscntControlador.java
 *
 * 1.0
 * 
 * 27/06/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ImpresionPorLotesControladorEnum;
import com.sysman.general.enums.ImpresionPorLotesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 27/06/2024
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmgenerararchivosplanoscntControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anio;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String tipo;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String numeroInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String numeroFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAnio;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTipo;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaNumeroInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaNumeroFinal;	

//</DECLARAR_LISTAS_COMBO_GRANDE>
	
	@EJB
    private EjbContabilidadSieteRemote ejbContabilidadSiete;
	/**
	 * Crea una nueva instancia de FrmgenerararchivosplanoscntControlador
	 */
	public FrmgenerararchivosplanoscntControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_GENERAR_ARCHIVOS_PLANOS_CNT_CONTROLADOR
                    .getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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
		cargarListaAnio();
		cargarListaTipo();
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAnio() 
	{
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpresionPorLotesControladorUrlEnum.URL7218
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
	 * Carga la lista listaTipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTipo() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                ImpresionPorLotesControladorUrlEnum.URL6255
                                                .getValue());

		Map<String, Object> param = new TreeMap<>();
		
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaNumeroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaNumeroInicial() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                ImpresionPorLotesControladorUrlEnum.URL8338
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();		
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);
		param.put(GeneralParameterEnum.TIPO.getName(), tipo);
		
		listaNumeroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.NUMERO.getName());

	}

	/**
	 * 
	 * Carga la lista listaNumeroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaNumeroFinal() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                ImpresionPorLotesControladorUrlEnum.URL10386
                                                .getValue());

		Map<String, Object> param = new TreeMap<>();
		
		param.put(ImpresionPorLotesControladorEnum.NUMEROINI.getValue(), numeroInicial);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);
		param.put(GeneralParameterEnum.TIPO.getName(), tipo);
		
		listaNumeroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.NUMERO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		try {
			String reporte = ejbContabilidadSiete.generarPlanoCnt(compania, Integer.parseInt(anio),
					tipo, numeroInicial);
			String nombreArchivo = tipo+numeroInicial+".txt";
			
			ByteArrayInputStream texto = JsfUtil.serializarPlano(reporte);

			archivoDescarga = JsfUtil.getArchivoDescarga(texto, nombreArchivo);
			
		} catch (JRException | IOException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} 
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAnio() {
		if (anio != "0") 
		{
			numeroInicial = "";
	        numeroFinal = "";
	        cargarListaNumeroInicial();
		 }
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipo(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
        tipo = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        numeroInicial = "";
        numeroFinal = "";
        cargarListaNumeroInicial();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNumeroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNumeroInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
        numeroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
                        .toString();
        numeroFinal = "";
        cargarListaNumeroFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNumeroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNumeroFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
        numeroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
                        .toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable tipo
	 * 
	 * @return tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * Asigna la variable tipo
	 * 
	 * @param tipo Variable a asignar en tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Retorna la variable numeroInicial
	 * 
	 * @return numeroInicial
	 */
	public String getNumeroInicial() {
		return numeroInicial;
	}

	/**
	 * Asigna la variable numeroInicial
	 * 
	 * @param numeroInicial Variable a asignar en numeroInicial
	 */
	public void setNumeroInicial(String numeroInicial) {
		this.numeroInicial = numeroInicial;
	}

	/**
	 * Retorna la variable numeroFinal
	 * 
	 * @return numeroFinal
	 */
	public String getNumeroFinal() {
		return numeroFinal;
	}

	/**
	 * Asigna la variable numeroFinal
	 * 
	 * @param numeroFinal Variable a asignar en numeroFinal
	 */
	public void setNumeroFinal(String numeroFinal) {
		this.numeroFinal = numeroFinal;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
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
	 * @param listaAnio Variable a asignar en listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}

	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo Variable a asignar en listaTipo
	 */
	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}

	/**
	 * Retorna la lista listaNumeroInicial
	 * 
	 * @return listaNumeroInicial
	 */
	public RegistroDataModelImpl getListaNumeroInicial() {
		return listaNumeroInicial;
	}

	/**
	 * Asigna la lista listaNumeroInicial
	 * 
	 * @param listaNumeroInicial Variable a asignar en listaNumeroInicial
	 */
	public void setListaNumeroInicial(RegistroDataModelImpl listaNumeroInicial) {
		this.listaNumeroInicial = listaNumeroInicial;
	}

	/**
	 * Retorna la lista listaNumeroFinal
	 * 
	 * @return listaNumeroFinal
	 */
	public RegistroDataModelImpl getListaNumeroFinal() {
		return listaNumeroFinal;
	}

	/**
	 * Asigna la lista listaNumeroFinal
	 * 
	 * @param listaNumeroFinal Variable a asignar en listaNumeroFinal
	 */
	public void setListaNumeroFinal(RegistroDataModelImpl listaNumeroFinal) {
		this.listaNumeroFinal = listaNumeroFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
