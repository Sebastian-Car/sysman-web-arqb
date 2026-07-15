/*-
 * KardexPepsControlador.java
 *
 * 1.0
 * 
 * 24/07/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

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

import com.sysman.almacen.enums.KardexControladorEnum;
import com.sysman.almacen.enums.KardexControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 24/07/2025
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  KardexPepsControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	private String elementoDesde;
	private String elementoHasta;
	private Date fechaInicial;
	private Date fechaFinal;
	private String titulo;
	private String tipo = "C";
	private String reporte;
	private final String cStrsql;
	private String nombreElementoIni;
	private String nombreElementoFin;
	private final String cCodigoElemento;
	private StreamedContent archivoDescarga;
	private RegistroDataModelImpl listaCmbElementoDesde;
	private RegistroDataModelImpl listaCmbElementoHasta;
	private final String cParReporte;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

		/**
	 * Crea una nueva instancia de KardexPepsControlador
	 */
	public KardexPepsControlador() {
		super();
		compania = SessionUtil.getCompania();
		cCodigoElemento = "CODIGOELEMENTO";
		modulo = SessionUtil.getModulo();
		cStrsql = "PR_STRSQL";
		cParReporte = "parReporte";
		titulo = idioma.getString("TB_TB4480");;

		try {
			numFormulario = GeneralCodigoFormaEnum.KARDEX_COSUMO_PEPS.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
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
	public void inicializar(){
		fechaInicial = new Date();
		fechaFinal = new Date();
		abrirFormulario();
		cargarListaCmbElementoDesde();
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
	 * Carga la lista listaCmbElementoDesde
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCmbElementoDesde(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						KardexControladorUrlEnum.URL11488
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(KardexControladorEnum.TIPOELEMENTO.getValue(),tipo);
		listaCmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigoElemento);
		
	}
	/**
	 * 
	 * Carga la lista listaCmbElementoHasta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCmbElementoHasta(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(KardexControladorUrlEnum.URL7809.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(KardexControladorEnum.TIPOELEMENTO.getValue(), tipo);
		param.put(KardexControladorEnum.ELEMENTODESDE.getValue(), elementoDesde);
		listaCmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, cCodigoElemento);
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPresentar() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		presentarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		presentarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	
	private void presentarInforme(FORMATOS formatos) {
		try {
			reporte = "002813TarjetaKardexPeps";

			String parametro = ejbSysmanUtil.consultarParametro(compania, "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(), true);

			HashMap<String, Object> remplazar = new HashMap<>();

			remplazar.put("parametro", parametro);
			remplazar.put("elementoInicial", "'" + elementoDesde + "'");
			remplazar.put("elementoFinal", "'" + elementoHasta + "'");
			remplazar.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
			remplazar.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));
			// .replace("00:00:00", "23:59:59"));
			remplazar.put("tipo", "'" + tipo + "'");

			Map<String, Object> parametros = new HashMap<>();

			parametros.put("PR_TITULO", titulo);
			parametros.put("PR_FECHAS", idioma.getString("TB_TB3127")
					.replace("#fechaInicial#", SysmanFunciones.convertirAFechaCadena(fechaInicial, "dd MMMMM yyyy"))
					.replace("#fechaFinal#", SysmanFunciones.convertirAFechaCadena(fechaFinal, "dd MMMMM yyyy")));
			parametros.put(cStrsql, Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), remplazar));
			parametros.put(cParReporte, reporte);

			archivoDescarga = JsfUtil.exportarStreamed(parametros.get(cParReporte).toString(), parametros,
					ConectorPool.ESQUEMA_SYSMAN, formatos);
		} catch (ParseException | OutOfMemoryError | JRException | IOException | SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Desde
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarDesde() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Hasta
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarHasta() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbElementoDesde
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCmbElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoDesde = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigoElemento), "").toString();
		nombreElementoIni = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRELARGO"), "").toString();

		elementoHasta = null;
		nombreElementoFin = null;
		cargarListaCmbElementoHasta();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCmbElementoHasta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCmbElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoHasta = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigoElemento), "").toString();
		nombreElementoFin = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRELARGO"), "").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable elementoDesde
	 * 
	 * @return  elementoDesde
	 */
	public String getElementoDesde() {
		return elementoDesde;
	}
	/**
	 * Asigna la variable  elementoDesde
	 * 
	 * @param  elementoDesde
	 * Variable a asignar en  elementoDesde
	 */
	public void setElementoDesde(String elementoDesde) {
		this.elementoDesde = elementoDesde;
	}
	/**
	 * Retorna la variable elementoHasta
	 * 
	 * @return  elementoHasta
	 */
	public String getElementoHasta() {
		return elementoHasta;
	}
	/**
	 * Asigna la variable  elementoHasta
	 * 
	 * @param  elementoHasta
	 * Variable a asignar en  elementoHasta
	 */
	public void setElementoHasta(String elementoHasta) {
		this.elementoHasta = elementoHasta;
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
	 * Retorna la variable nombreElementoIni
	 * 
	 * @return  nombreElementoIni
	 */
	public String getNombreElementoIni() {
		return nombreElementoIni;
	}
	/**
	 * Asigna la variable  nombreElementoIni
	 * 
	 * @param  nombreElementoIni
	 * Variable a asignar en  nombreElementoIni
	 */
	public void setNombreElementoIni(String nombreElementoIni) {
		this.nombreElementoIni = nombreElementoIni;
	}
	/**
	 * Retorna la variable nombreElementoFin
	 * 
	 * @return  nombreElementoFin
	 */
	public String getNombreElementoFin() {
		return nombreElementoFin;
	}
	/**
	 * Asigna la variable  nombreElementoFin
	 * 
	 * @param  nombreElementoFin
	 * Variable a asignar en  nombreElementoFin
	 */
	public void setNombreElementoFin(String nombreElementoFin) {
		this.nombreElementoFin = nombreElementoFin;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	
	/**
	 * Retorna la lista listaCmbElementoDesde
	 * 
	 * @return listaCmbElementoDesde
	 */
	public RegistroDataModelImpl getListaCmbElementoDesde() {
		return listaCmbElementoDesde;
	}
	/**
	 * Asigna la lista listaCmbElementoDesde
	 * 
	 * @param listaCmbElementoDesde
	 * Variable a asignar en  listaCmbElementoDesde
	 */
	public void setListaCmbElementoDesde(RegistroDataModelImpl listaCmbElementoDesde) {
		this.listaCmbElementoDesde = listaCmbElementoDesde;
	}
	/**
	 * Retorna la lista listaCmbElementoHasta
	 * 
	 * @return listaCmbElementoHasta
	 */
	public RegistroDataModelImpl getListaCmbElementoHasta() {
		return listaCmbElementoHasta;
	}
	/**
	 * Asigna la lista listaCmbElementoHasta
	 * 
	 * @param listaCmbElementoHasta
	 * Variable a asignar en  listaCmbElementoHasta
	 */
	public void setListaCmbElementoHasta(RegistroDataModelImpl listaCmbElementoHasta) {
		this.listaCmbElementoHasta = listaCmbElementoHasta;
	}
	
	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}
