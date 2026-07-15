/*-
 * MovimientoAuxiliarControlador.java
 *
 * 1.0
 * 
 * 27/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import com.sysman.almacen.enums.MovimientoAuxiliarControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
/**
 * Permite generar informe de movimientos por elemento
 *
 * @version 1.0, 27/07/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  MovimientoAuxiliarControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * lista elemento
	 */
	private String elementoInicial;
	/**
	 * lista elemento
	 */
	private String elementoFinal;
	/**
	 * fecha inicial
	 */
	private Date fechaInicial;
	/**
	 * fecha final
	 */
	private Date fechaFinal;

	private boolean excelPlano;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * lista elementos inicial
	 */
	private RegistroDataModelImpl listacmbElementoDesde;
	/**
	 * lista elemento final
	 */
	private RegistroDataModelImpl listacmbElementoHasta;
	private int anio;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de MovimientoAuxiliarControlador
	 */
	public MovimientoAuxiliarControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//1871
			numFormulario=GeneralCodigoFormaEnum.FRMMOVIMIENTOAUXILIAR_CONTROLADOR.getCodigo();
			validarPermisos();

			anio = SysmanFunciones.ano(new Date());
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
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacmbElementoDesde(); 
		cargarListacmbElementoHasta();
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
	 * Carga la lista listacmbElementoDesde
	 */
	public void cargarListacmbElementoDesde(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						MovimientoAuxiliarControladorUrlEnum.URL2842
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listacmbElementoHasta
	 */
	public void cargarListacmbElementoHasta(){


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						MovimientoAuxiliarControladorUrlEnum.URL2098
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton cmdPantalla
	 * en la vista
	 *
	 */
	public void oprimircmdPantalla() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;        
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     

		if(excelPlano) {

			generarArchivoPlano();

		}else {

			generarInforme(FORMATOS.EXCEL);

		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Excel
	 * 
	 * 
	 */
	public void cambiarExcel() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>

	public void generarInforme(FORMATOS formato) {

		try {

			Map<String, Object> reemplazos = new HashMap<>();

			reemplazos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			reemplazos.put("elementoInicial", elementoInicial);
			reemplazos.put("elementoFinal", elementoFinal);
			reemplazos.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
			reemplazos.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));

			String consulta = Reporteador.resuelveConsulta("900014AuxiliarMovimientosTipo", 
					Integer.parseInt(SessionUtil.getModulo()), reemplazos);

			Map<String, Object> parametros = new HashMap<>();

			parametros.put("PR_FECHAINICIAL", fechaInicial);
			parametros.put("PR_FECHAFINAL", fechaFinal);
			parametros.put("PR_STRSQL", consulta);
			parametros.put("PR_FORMS_AUXILIARMOVIMIENTOS_TIPOINICIAL",
					elementoInicial);
			parametros.put("PR_FORMS_AUXILIARMOVIMIENTOS_TIPOFINAL", elementoFinal);
			parametros.put("PR_FORMS_AUXILIARMOVIMIENTOS_FECHAINICIAL",
					SysmanFunciones.convertirAFechaCadena(fechaInicial).toString());
			parametros.put("PR_FORMS_AUXILIARMOVIMIENTOS_FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal).toString());

			archivoDescarga = JsfUtil.exportarStreamed("000395AuxiliarMovimientosTipo", 
					parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (JRException | IOException | SysmanException | ParseException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void generarArchivoPlano() {
		try {

			Map<String, Object> reemplazos = new HashMap<>();

			reemplazos.put("elementoInicial", elementoInicial);
			reemplazos.put("elementoFinal", elementoFinal);
			reemplazos.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
			reemplazos.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));

			String consulta = Reporteador.resuelveConsulta("800378AuxiliarMovimientosTipo", 
					Integer.parseInt(SessionUtil.getModulo()), reemplazos);

			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consulta, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, "800378AuxiliarMovimientosTipo");
		}
		catch ( JRException | IOException | SQLException | DRException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacmbElementoDesde
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoInicial= SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		cargarListacmbElementoHasta();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacmbElementoHasta
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
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
	 * Retorna la lista listacmbElementoDesde
	 * 
	 * @return listacmbElementoDesde
	 */
	public RegistroDataModelImpl getListacmbElementoDesde() {
		return listacmbElementoDesde;
	}
	/**
	 * Asigna la lista listacmbElementoDesde
	 * 
	 * @param listacmbElementoDesde
	 * Variable a asignar en  listacmbElementoDesde
	 */
	public void setListacmbElementoDesde(RegistroDataModelImpl listacmbElementoDesde) {
		this.listacmbElementoDesde = listacmbElementoDesde;
	}
	/**
	 * Retorna la lista listacmbElementoHasta
	 * 
	 * @return listacmbElementoHasta
	 */
	public RegistroDataModelImpl getListacmbElementoHasta() {
		return listacmbElementoHasta;
	}
	/**
	 * Asigna la lista listacmbElementoHasta
	 * 
	 * @param listacmbElementoHasta
	 * Variable a asignar en  listacmbElementoHasta
	 */
	public void setListacmbElementoHasta(RegistroDataModelImpl listacmbElementoHasta) {
		this.listacmbElementoHasta = listacmbElementoHasta;
	}
	public boolean isExcelPlano() {
		return excelPlano;
	}
	public void setExcelPlano(boolean excelPlano) {
		this.excelPlano = excelPlano;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
