/*-
 * FrminfinversionpormunicipiosControlador.java
 *
 * 1.0
 * 
 * 13/05/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 13/05/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  FrminfinversionpormunicipiosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String agrupadoPor;
	private String destino;
	private Date fechaInicial;
	private Date fechaFinal;
	private String informe;
	private String FechaIni;
	private String FechaFin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listadestino;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminfinversionpormunicipiosControlador
	 */
	public FrminfinversionpormunicipiosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_INF_INV_POR_MUNICIPIOS
                    .getCodigo();
			validarPermisos();
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
		cargarListadestino();
		//</CARGAR_LISTA>
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
		fechaInicial = new Date();
		fechaFinal = new Date();
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listadestino
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListadestino(){
		Map<String,Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		try {
			listadestino = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID("1977002")
							.getUrl(),param));
		} catch(SystemException e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
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
		generarInforme(ReportesBean.FORMATOS.EXCEL);           
		//</CODIGO_DESARROLLADO>
	}

	public void generarInforme(ReportesBean.FORMATOS formato) {

		try {


			if ("1".equals(agrupadoPor)) {
				informe = "002940InvPorMunicipiosAgrupado";
			} else {
				informe = "002939InvPorMunicipiosDetallado";
			}

			FechaIni = SysmanFunciones
					.convertirAFechaCadena(fechaInicial);
			FechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

			String filtroDestino = "";

			if (!"T".equals(destino)) {
				filtroDestino = " AND PLAN_PRESUPUESTAL.DESTINO = '" + destino + "'";
			}

			Map<String, Object> reemplazos = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();

			reemplazos.put("compania", compania);
			reemplazos.put("fechaInicial", FechaIni);
			reemplazos.put("fechaFinal", FechaFin);
			reemplazos.put("filtroDestino", filtroDestino);

			Reporteador.resuelveConsulta(informe,
					Integer.parseInt(modulo), reemplazos, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (ParseException | JRException | IOException | SysmanException
				| NumberFormatException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable agrupadoPor
	 * 
	 * @return  agrupadoPor
	 */
	public String getAgrupadoPor() {
		return agrupadoPor;
	}
	/**
	 * Asigna la variable  agrupadoPor
	 * 
	 * @param  agrupadoPor
	 * Variable a asignar en  agrupadoPor
	 */
	public void setAgrupadoPor(String agrupadoPor) {
		this.agrupadoPor = agrupadoPor;
	}
	/**
	 * Retorna la variable destino
	 * 
	 * @return  destino
	 */
	public String getDestino() {
		return destino;
	}
	/**
	 * Asigna la variable  destino
	 * 
	 * @param  destino
	 * Variable a asignar en  destino
	 */
	public void setDestino(String destino) {
		this.destino = destino;
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
	/**
	 * Retorna la lista listadestino
	 * 
	 * @return listadestino
	 */
	public List<Registro> getListadestino() {
		return listadestino;
	}
	/**
	 * Asigna la lista listadestino
	 * 
	 * @param listadestino
	 * Variable a asignar en  listadestino
	 */
	public void setListadestino(List<Registro> listadestino) {
		this.listadestino = listadestino;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
