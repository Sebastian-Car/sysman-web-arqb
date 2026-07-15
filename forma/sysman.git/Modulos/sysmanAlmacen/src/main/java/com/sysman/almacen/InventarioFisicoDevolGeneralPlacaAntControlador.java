/*-
 * InventarioFisicoDevolGeneralPlacaAntControlador.java
 *
 * 1.0
 * 
 * 13/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;

import net.sf.jasperreports.engine.JRException;
/**
 * Controlador que sirve para generar reportes sobre el inventario general de placas anteriores.
 *
 * @version 1.0, 13/08/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  InventarioFisicoDevolGeneralPlacaAntControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 Variable que almacena el modulo
	 */
	private final String modulo;

	private String digitos;
	
	private String reporte;
	
	private StreamedContent archivoDescarga;
	
	/**
	 * Crea una nueva instancia de InventarioFisicoDevolGeneralPlacaAntControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public InventarioFisicoDevolGeneralPlacaAntControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo=SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.INVENTARIO_FISICO_DEVOL_GENERAL_PLACA_ANT_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
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
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		try {

			digitos = ejbSysmanUtil.consultarParametro(compania, "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),false);

		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//<CARGAR_LISTA_COMBO_GRANDE>
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
		/*
	FR1889-AL_ABRIR
	Private Sub Form_Open(Cancel As Integer)
	   'formularioAbrir 10, Me.Name
	End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**

	 * Metodos para imprimir. 
	 *
	 */
	public void oprimirbtnPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;       
		generaInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}

	public void oprimirbtnExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;        
		generaInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	public void generaInforme(ReportesBean.FORMATOS formato)  {
		try{
			reporte="001864InventarioFisicoDeDevolutivosplaca";
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("compania",compania);
			reemplazar.put("digito", digitos); 
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),reemplazar, parametros);    

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (JRException | IOException| SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
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
	//</SET_GET_LISTAS_COMBO_GRANDE>
	public String getDigitos() {
		return digitos;
	}
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
