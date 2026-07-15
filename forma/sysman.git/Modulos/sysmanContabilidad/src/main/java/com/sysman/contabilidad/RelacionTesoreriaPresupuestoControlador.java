/*-
 * RelacionTesoreriaPresupuestoControlador.java
 *
 * 1.0
 * 
 * 26/06/2025
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
import java.util.Calendar;
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
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelaciondeegresosControladorEnum;
import com.sysman.contabilidad.enums.RelaciondeegresosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;


import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 26/06/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class  RelacionTesoreriaPresupuestoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private String cuentaInicial;
	private String cuentaFinal;
	private Date fechaInicial;
	private Date fechaFinal;
	private int anio;
	private StreamedContent archivoDescarga;
	private boolean rubro;

	private final String modulo;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCuentaFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de RelacionTesoreriaPresupuestoControlador
	 */
	public RelacionTesoreriaPresupuestoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();

		try {
			numFormulario = GeneralCodigoFormaEnum.RELACIONTESORERIAPRESUPUESTO_CONTROLADOR
					.getCodigo();

			validarPermisos();

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
		abrirFormulario();
		cargarListaCuentaInicial(); 
		cargarListaCuentaFinal();

	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){

		fechaInicial = new Date();
		fechaFinal = new Date();
		anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCuentaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RelaciondeegresosControladorUrlEnum.URL3743
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(RelaciondeegresosControladorEnum.ANOINICIAL.getValue(),
				anio);
		param.put(RelaciondeegresosControladorEnum.ANOFINAL.getValue(),
				anio);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, RelaciondeegresosControladorEnum.ID.getValue());
	}
	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCuentaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RelaciondeegresosControladorUrlEnum.URL4993
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(RelaciondeegresosControladorEnum.ANOINICIAL.getValue(),
				anio);
		param.put(RelaciondeegresosControladorEnum.ANOFINAL.getValue(),
				anio);

		param.put(RelaciondeegresosControladorEnum.CUENTAINICIAL.getValue(),
				cuentaInicial);
		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, RelaciondeegresosControladorEnum.ID.getValue());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPDF() {
		
		archivoDescarga=null;  
		generarInforme(FORMATOS.PDF);
		          
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EXCEL
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirEXCEL() {

		archivoDescarga=null; 
		generarInforme(FORMATOS.EXCEL97);

	}


	public void generarInforme(ReportesBean.FORMATOS formato)
	{
		try
		{
			String reporte = "800714RelacionTesoreriaPresupuesto";
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("anoInicial", SysmanFunciones.ano(fechaInicial));
			reemplazar.put("anoFinal", SysmanFunciones.ano(fechaFinal));
			reemplazar.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
			reemplazar.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));
			reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
			reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_FECHAS", "Periodo del "
					+ SysmanFunciones.convertirAFechaCadena(fechaInicial) + " al "
					+ SysmanFunciones.convertirAFechaCadena(fechaFinal));

			Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(
					reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SysmanException | ParseException e)
		{
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Metodo ejecutado al cambiar el control FechaInicial
	 * 
	 * 
	 */
	public void cambiarfechaInicial() {

		anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;

		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
	}
	/**
	 * Metodo ejecutado al cambiar el control FechaFinal
	 * 
	 * 
	 */
	public void cambiarfechaFinal() {

	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), " ")
				.toString();
		cuentaFinal = null;
		cargarListaCuentaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), " ")
				.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return  cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}
	/**
	 * Asigna la variable  cuentaInicial
	 * 
	 * @param  cuentaInicial
	 * Variable a asignar en  cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}
	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return  cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}
	/**
	 * Asigna la variable  cuentaFinal
	 * 
	 * @param  cuentaFinal
	 * Variable a asignar en  cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
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
	 * Retorna la lista listaCuentaInicial
	 * 
	 * @return listaCuentaInicial
	 */
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}
	/**
	 * Asigna la lista listaCuentaInicial
	 * 
	 * @param listaCuentaInicial
	 * Variable a asignar en  listaCuentaInicial
	 */
	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}
	/**
	 * Retorna la lista listaCuentaFinal
	 * 
	 * @return listaCuentaFinal
	 */
	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}
	/**
	 * Asigna la lista listaCuentaFinal
	 * 
	 * @param listaCuentaFinal
	 * Variable a asignar en  listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
