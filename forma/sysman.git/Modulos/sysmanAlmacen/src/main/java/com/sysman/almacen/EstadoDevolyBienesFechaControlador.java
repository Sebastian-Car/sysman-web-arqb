/*-
 * EstadoDevolyBienesFechaControlador.java
 *
 * 1.0
 * 
 * 01/08/2018
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
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.EstadoDevolyBienesFechaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * Controlador que sirve para generar informes de estados y bienes a una fecha.
 *
 * @version 1.0, 01/08/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class EstadoDevolyBienesFechaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 Variable que almacena el modulo
	 */
	private final String modulo;

	// <DECLARAR_ATRIBUTOS>
	private String reporte;
	
	private Date fecha;

	private String digitos;

	private boolean conSaldo;
	
	private int ordenar;
	
	private String elementoInicial;
	
	private String elementoFinal;
	
	private String nombreElemIni;
	
	private String nombreElemFinal;

	private StreamedContent archivoDescarga;

	private RegistroDataModelImpl listaElementoInicial;
	
	private RegistroDataModelImpl listaElementoFinal;
	

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public EstadoDevolyBienesFechaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo=SessionUtil.getModulo();
		ordenar=1;
		try {
			numFormulario = GeneralCodigoFormaEnum.ESTADO_DEVOL_Y_BIENES_FECHA_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
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
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		try {
			digitos = ejbSysmanUtil.consultarParametro(compania,"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),false);
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaElementoInicial();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR1878-AL_ABRIR Private Sub Form_Open(Cancel As Integer) DoCmd.Restore
		 * 'formularioAbrir 10, Me.Name End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaElementoInicial
	 *
	 * Metodos de cargar listas. 
	 */
	public void cargarListaElementoInicial() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(EstadoDevolyBienesFechaControladorUrlEnum.URL2472.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,"CODIGOELEMENTO");
	}
	public void cargarListaElementoFinal() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(EstadoDevolyBienesFechaControladorUrlEnum.URL2473.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CODIGOINICIAL", elementoInicial);
		listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGOELEMENTO");
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 
	 * Metodos para imprimir. 
	 *
	 */
	public void oprimirPDF() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}
	public void oprimirexcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	public void generaInforme(ReportesBean.FORMATOS formato) {

		try{
			reporte="001846IEstadoDevYBiAFecha"; 
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("fecha", SysmanFunciones.convertirAFechaCadena(fecha));
			reemplazar.put("elementoInicial",elementoInicial);
			reemplazar.put("elementoFinal",elementoFinal);
			reemplazar.put("compania",compania);
			reemplazar.put("saldo", conSaldo);
			reemplazar.put("digito", digitos);
			reemplazar.put("orden", ordenar); 

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),reemplazar, parametros);    
			parametros.put("PR_FORMS_ESTADODEVOLUTIVOSYBIENESINMAFECHA_TXTFECHA", SysmanFunciones.convertirAFechaCadena(fecha));
			parametros.put("PR_NOMBRECOMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NITCOMPANIA",SessionUtil.getCompaniaIngreso().getNit());
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaElementoInicial
	 *
	 * Metodo de seleccion de datos inicial
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoInicial = registroAux.getCampos().get("CODIGOELEMENTO").toString();

		nombreElemIni = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
				.toString();
		elementoFinal = null;
		nombreElemFinal = null;
		cargarListaElementoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaElementoFinal
	 *
	 * Metodo de seleccion de datos final
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = registroAux.getCampos().get("CODIGOELEMENTO").toString();

		nombreElemFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
				.toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable conSaldo
	 * 
	 * @return conSaldo
	 */
	public boolean getConSaldo() {
		return conSaldo;
	}

	/**
	 * Asigna la variable conSaldo
	 * 
	 * @param conSaldo
	 *            Variable a asignar en conSaldo
	 */
	public void setConSaldo(boolean conSaldo) {
		this.conSaldo = conSaldo;
	}

	/**
	 * Retorna la variable ordenadoPor
	 * 
	 * @return ordenadoPor
	 */
	public int getOrdenar() {
		return ordenar;
	}

	/**
	 * Asigna la variable ordenadoPor
	 * 
	 * @param ordenadoPor
	 *            Variable a asignar en ordenadoPor
	 */
	public void setOrdenar(int ordenar) {
		this.ordenar = ordenar;
	}

	/**
	 * Retorna la variable elementoInicial
	 * 
	 * @return elementoInicial
	 */
	public String getElementoInicial() {
		return elementoInicial;
	}

	/**
	 * Asigna la variable elementoInicial
	 * 
	 * @param elementoInicial
	 *            Variable a asignar en elementoInicial
	 */
	public void setElementoInicial(String elementoInicial) {
		this.elementoInicial = elementoInicial;
	}

	/**
	 * Retorna la variable elementoFinal
	 * 
	 * @return elementoFinal
	 */
	public String getElementoFinal() {
		return elementoFinal;
	}

	/**
	 * Asigna la variable elementoFinal
	 * 
	 * @param elementoFinal
	 *            Variable a asignar en elementoFinal
	 */
	public void setElementoFinal(String elementoFinal) {
		this.elementoFinal = elementoFinal;
	}

	/**
	 * Retorna la variable nombreElemIni
	 * 
	 * @return nombreElemIni
	 */
	public String getNombreElemIni() {
		return nombreElemIni;
	}

	/**
	 * Asigna la variable nombreElemIni
	 * 
	 * @param nombreElemIni
	 *            Variable a asignar en nombreElemIni
	 */
	public void setNombreElemIni(String nombreElemIni) {
		this.nombreElemIni = nombreElemIni;
	}

	/**
	 * Retorna la variable nombreElemFinal
	 * 
	 * @return nombreElemFinal
	 */
	public String getNombreElemFinal() {
		return nombreElemFinal;
	}

	/**
	 * Asigna la variable nombreElemFinal
	 * 
	 * @param nombreElemFinal
	 *            Variable a asignar en nombreElemFinal
	 */
	public void setNombreElemFinal(String nombreElemFinal) {
		this.nombreElemFinal = nombreElemFinal;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaElementoInicial
	 * 
	 * @return listaElementoInicial
	 */
	public RegistroDataModelImpl getListaElementoInicial() {
		return listaElementoInicial;
	}

	/**
	 * Asigna la lista listaElementoInicial
	 * 
	 * @param listaElementoInicial
	 *            Variable a asignar en listaElementoInicial
	 */
	public void setListaElementoInicial(RegistroDataModelImpl listaElementoInicial) {
		this.listaElementoInicial = listaElementoInicial;
	}

	/**
	 * Retorna la lista listaElementoFinal
	 * 
	 * @return listaElementoFinal
	 */
	public RegistroDataModelImpl getListaElementoFinal() {
		return listaElementoFinal;
	}

	/**
	 * Asigna la lista listaElementoFinal
	 * 
	 * @param listaElementoFinal
	 *            Variable a asignar en listaElementoFinal
	 */
	public void setListaElementoFinal(RegistroDataModelImpl listaElementoFinal) {
		this.listaElementoFinal = listaElementoFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
