/*-
 * RelacionDeEgresosUpcControlador.java
 *
 * 1.0
 * 
 * 30/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelacionDeEgresosUpcControladorEnum;
import com.sysman.contabilidad.enums.RelacionDeEgresosUpcControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jcaceres
 * @version 1, 07/06/2018
 * 
 */
@ManagedBean
@ViewScoped

public class RelacionDeEgresosUpcControlador extends BeanBaseModal
{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private Date fechaPrimera;
	private Date fechaFinal;
	private final String modulo;
	private boolean referencia;
	private StreamedContent archivoDescarga;
	private boolean ckEspecial;
	private String refInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String refFinal =  SysmanConstantes.DEFECTOFINAL_STRING;
	private String bancoInicial =  SysmanConstantes.DEFECTOINICIAL_STRING;
	private String bancoFinal =  SysmanConstantes.DEFECTOFINAL_STRING;
	private RegistroDataModelImpl listaReferenciInicial;
	private RegistroDataModelImpl listaReferenciFinal;
	private RegistroDataModelImpl listaBancoInicial;
	private RegistroDataModelImpl listaBancoFinal;
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private boolean especialExcel;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	public RelacionDeEgresosUpcControlador()
	{
		super();
		compania = SessionUtil.getCompania();
		fechaPrimera = fechaFinal = new Date();
		modulo = SessionUtil.getModulo();
		try
		{
			numFormulario = GeneralCodigoFormaEnum.RELACION_DE_EGRESOS_UPC_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
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
	public void inicializar()
	{
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>

		cargarListaReferenciInicial(); 

		cargarListaBancoInicial(); 


		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario()
	{
		// <CODIGO_DESARROLLADO>

		try
		{
			String manejaRelacionEgresos = SysmanFunciones
					.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"MANEJA REFERENCIA EN REPORTE RELACION DE EGRESOS TESORERIA",
							modulo, new Date(), true), "NO");
			if ("SI".equals(manejaRelacionEgresos))
			{
				referencia = true;
			}
			else
			{
				referencia = false;
			}
		}
		catch (SystemException e)
		{

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	public void cargarListaReferenciInicial()       {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(

						RelacionDeEgresosUpcControladorUrlEnum.URL6234
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		param.put(GeneralParameterEnum.ANO.name(),
				SysmanFunciones.ano(fechaPrimera));

		listaReferenciInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	public void cargarListaReferenciFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RelacionDeEgresosUpcControladorUrlEnum.URL6235
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		param.put(GeneralParameterEnum.ANO.name(),
				SysmanFunciones.ano(fechaFinal));

		param.put(GeneralParameterEnum.CODIGOINICIAL.name(),
				refInicial);

		listaReferenciFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	public void cargarListaBancoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RelacionDeEgresosUpcControladorUrlEnum.URL16016
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		param.put(GeneralParameterEnum.ANO.name(),
				SysmanFunciones.ano(fechaPrimera));

		listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaBancoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RelacionDeEgresosUpcControladorUrlEnum.URL16018
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		param.put(GeneralParameterEnum.ANO.name(),
				SysmanFunciones.ano(fechaPrimera));
		
		param.put(RelacionDeEgresosUpcControladorEnum.CUENTAINICIAL.getValue(),
				bancoInicial);

		listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}


	// <METODOS_CARGAR_LISTA>
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>

	public void generarReporte(FORMATOS formato)

	{
		String reporte=null;
		String consulta=null;
		if (referencia)

		{
			if(ckEspecial) {

				reporte = "002596RelacionDeEgresos_REF";
				consulta = reporte; 
			}else {
				reporte = "001785RelacionDeEgresosREF";
				consulta = "001782RelacionDeEgreso";
			}
		}
		else
		{


			reporte = "001782RelacionDeEgreso";
			consulta = "001782RelacionDeEgreso";
		}
		
        if (FORMATOS.EXCEL.equals(formato) && especialExcel) {
        	reporte = "002870RelacionEgresosEspecial";
        	consulta = "002870RelacionEgresosEspecial";
        }

		try
		{
			// JM CC 4248
			
			String formatoespecial = SysmanFunciones
					.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"FORMATO REPORTE RELACION DE EGRESOS TESORERIA CON REFERENCIA",
							modulo, new Date(), false), "NO");
			
			if(referencia && ckEspecial && !"NO".equalsIgnoreCase(formatoespecial)) {
				reporte = formatoespecial;
				consulta = formatoespecial;
			}
			
			//JM CC 4248
			
			// reemplazos son la variables asignadas que se traen de
			// access
			Map<String, Object> reemplazos = new HashMap<>();
			// parametro son asignados a los correspondientes capos
			// del reporte
			Map<String, Object> parametro = new HashMap<>();
			reemplazos.put("compania", compania);

			reemplazos.put("fechaPrimera",
					SysmanFunciones.formatearFecha(fechaPrimera)); 
			reemplazos.put("fechaFinal",
					SysmanFunciones.formatearFecha(fechaFinal));
			reemplazos.put("referenciaInicial",refInicial);
			reemplazos.put("referenciaFinal",refFinal);
			reemplazos.put("bancInicial", bancoInicial);
			reemplazos.put("bancFinal", bancoFinal);

			parametro.put("PR_FORMS_RELACIONDEEGRESOS_UPC_FECHAINI",
					SysmanFunciones.convertirAFechaCadena(
							fechaPrimera,
							"dd/MM/yyyy"));
			parametro.put("PR_NOMBRECOMPANIA",
					SessionUtil.getCompaniaIngreso().getNombre());

			parametro.put("PR_FORMS_RELACIONDEEGRESOS_UPC_FECHAFIN",
					SysmanFunciones.convertirAFechaCadena(
							fechaFinal,
							"dd/MM/yyyy"));

			Reporteador.resuelveConsulta(consulta,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos, parametro);

			archivoDescarga = JsfUtil.exportarStreamed(
					reporte, parametro,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (JRException | IOException | ParseException | SysmanException | SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista
	 *
	 *
	 */
	public void oprimirImprimir()

	{
		archivoDescarga = null;
		generarReporte(FORMATOS.EXCEL);
	}

	public void oprimirBotonpdf()

	{
		archivoDescarga = null;
		generarReporte(FORMATOS.PDF);
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		refInicial = registroAux.getCampos().get("CODIGO").toString();
		cargarListaReferenciFinal(); 
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		refFinal = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaBancoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBancoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bancoInicial = registroAux.getCampos().get("CODIGO").toString();
		cargarListaBancoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaBancoFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBancoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bancoFinal = registroAux.getCampos().get("CODIGO").toString();
	}

	public void cambiarEspecial() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public Date getFechaPrimera()
	{
		return fechaPrimera;
	}

	public void setFechaPrimera(Date fechaPrimera)
	{
		this.fechaPrimera = fechaPrimera;
	}

	public Date getFechaFinal()
	{
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal)
	{
		this.fechaFinal = fechaFinal;
	}

	public StreamedContent getArchivoDescarga()
	{
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga)
	{
		this.archivoDescarga = archivoDescarga;
	}

	public boolean isReferencia()
	{
		return referencia;
	}

	public void setReferencia(boolean referencia)
	{
		this.referencia = referencia;
	}

	public String getModulo()
	{
		return modulo;
	}

	/**
	 * @return the ckEspecial
	 */

	public boolean isCkEspecial() {
		return ckEspecial;
	}

	public void setCkEspecial(boolean ckEspecial) {
		this.ckEspecial = ckEspecial;
	}

	public String getRefInicial() {
		return refInicial;
	}

	public void setRefInicial(String refInicial) {
		this.refInicial = refInicial;
	}

	public String getRefFinal() {
		return refFinal;
	}

	public void setRefFinal(String refFinal) {
		this.refFinal = refFinal;
	}

	public String getBancoInicial() {
		return bancoInicial;
	}

	public void setBancoInicial(String bancoInicial) {
		this.bancoInicial = bancoInicial;
	}

	public String getBancoFinal() {
		return bancoFinal;
	}

	public void setBancoFinal(String bancoFinal) {
		this.bancoFinal = bancoFinal;
	}

	/**
	 * @return the listaReferenciInicial
	 */
	public RegistroDataModelImpl getListaReferenciInicial() {
		return listaReferenciInicial;
	}

	/**
	 * @param listaReferenciInicial the listaReferenciInicial to set
	 */
	public void setListaReferenciInicial(RegistroDataModelImpl listaReferenciInicial) {
		this.listaReferenciInicial = listaReferenciInicial;
	}

	/**
	 * @return the listaReferenciFinal
	 */
	public RegistroDataModelImpl getListaReferenciFinal() {
		return listaReferenciFinal;
	}

	/**
	 * @param listaReferenciFinal the listaReferenciFinal to set
	 */
	public void setListaReferenciFinal(RegistroDataModelImpl listaReferenciFinal) {
		this.listaReferenciFinal = listaReferenciFinal;
	}

	/**
	 * @return the listaBancoInicial
	 */
	public RegistroDataModelImpl getListaBancoInicial() {
		return listaBancoInicial;
	}

	/**
	 * @param listaBancoInicial the listaBancoInicial to set
	 */
	public void setListaBancoInicial(RegistroDataModelImpl listaBancoInicial) {
		this.listaBancoInicial = listaBancoInicial;
	}

	/**
	 * @return the listaBancoFinal
	 */
	public RegistroDataModelImpl getListaBancoFinal() {
		return listaBancoFinal;
	}

	/**
	 * @param listaBancoFinal the listaBancoFinal to set
	 */
	public void setListaBancoFinal(RegistroDataModelImpl listaBancoFinal) {
		this.listaBancoFinal = listaBancoFinal;
	}
    /**
     * @return the especialExcel
     */
    public boolean isEspecialExcel() {
        return especialExcel;
    }

    /**
     * @param especialExcel
     * the especialExcel to set
     */
    public void setEspecialExcel(boolean especialExcel) {
        this.especialExcel = especialExcel;
    }


}
