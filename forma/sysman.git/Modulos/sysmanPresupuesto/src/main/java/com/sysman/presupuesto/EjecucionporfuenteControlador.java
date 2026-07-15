/*-
 * EjecucionporfuenteControlador.java
 *
 * 1.0
 * 
 * 18/07/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucionporfuenteControladorEnumUrl;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;


/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/07/2023
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class EjecucionporfuenteControlador extends BeanBaseModal {
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
	private String mes;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String fuenteIni;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String fuenteFin;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreMes;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String modulo;
	
	private String tipoVigencia;
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
	private List<Registro> listaanio;
	
	private List<Registro> listatipoVigencia;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listafuenteRecursosIncial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listafuenteRecursosfinal;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de EjecucionporfuenteControlador
	 */
	public EjecucionporfuenteControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		mes = "1";
		nombreMes = "Enero";
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		fuenteIni = "0";
		fuenteFin = "99999999999999999999";
		tipoVigencia = " ";
		try {
			//2418
			numFormulario = GeneralCodigoFormaEnum.EJECUCIONPORFUENTECONTROLADOR.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
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
//<CARGAR_LISTA>
		cargarListaanio();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListafuenteRecursosIncial();
		cargarListafuenteRecursosfinal();
		cargarListaTipoVigencia();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
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
	 * Carga la lista listaanio
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaanio() {
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaanio = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													EjecucionporfuenteControladorEnumUrl.URL3655.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void cargarListaTipoVigencia() {
		try {
			listatipoVigencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											EjecucionporfuenteControladorEnumUrl.URL1766001.getValue())
									.getUrl(),
									null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listafuenteRecursosIncial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListafuenteRecursosIncial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(EjecucionporfuenteControladorEnumUrl.URL58412.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listafuenteRecursosIncial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listafuenteRecursosfinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListafuenteRecursosfinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(EjecucionporfuenteControladorEnumUrl.URL58412.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listafuenteRecursosfinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirexcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (tipoVigencia==null) {
			obtenerReporte(FORMATOS.EXCEL, "800577InformeEjecucionporFuente");
		}else {
			obtenerReporte(FORMATOS.EXCEL, "002497InformeEjecucionporFuentePorVigencia");
		}
		
        // </CODIGO_DESARROLLADO>
	}
	
	public void cambiartipoVigencia() {
		// <CODIGO_DESARROLLADO>
		
        // </CODIGO_DESARROLLADO>
	}
	

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton pdf en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirpdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (tipoVigencia==null) {
			obtenerReporte(FORMATOS.PDF, "002484InformeEjecucionporFuente");
		}else {
			obtenerReporte(FORMATOS.PDF, "002497InformeEjecucionporFuentePorVigencia");
		}
		// </CODIGO_DESARROLLADO>
	}
	public void obtenerReporte(FORMATOS formatos, String nombre) {
         	try {
        		Map<String, Object> reemplazar = new TreeMap<>();
	            String nombreReporte = null;
	            reemplazar.put("compania", compania);
	            reemplazar.put("anio", anio);
	            reemplazar.put("mes", mes);
	            reemplazar.put("fuenteInicial", fuenteIni);
	            reemplazar.put("fuenteFinal", fuenteFin);
	            
	            if(tipoVigencia!=null) {
	            	reemplazar.put("tipoVigencia", tipoVigencia);
	            }
	           
	            Map<String, Object> parametros = new HashMap<>();
	            String entreFechas = "Informe Fuente Recursos Por Gasto "+nombreMes+" De "+anio;
	            	// MANEJO DE PARAMETROS DEL REPORTE
	            parametros.put("PR_ENTREFECHAS", entreFechas);
	            nombreReporte = nombre;
	            String excelSalida = nombreReporte;
	
	            archivoDescarga = JsfUtil.exportarExcelPlano(nombreReporte,
	                            excelSalida,
	                            ConectorPool.ESQUEMA_SYSMAN, formatos, reemplazar,
	                            parametros, Integer.valueOf(modulo));
	        }
	        catch (JRException | IOException |  SQLException | DRException | NumberFormatException | SysmanException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
    }
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control mes
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarmes() {
		// <CODIGO_DESARROLLADO>
		try {
			nombreMes  =  ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes));
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
		};
		// </CODIGO_DESARROLLADO>
	}
	public void cambiaranio() {
		cargarListafuenteRecursosIncial();
		cargarListafuenteRecursosfinal();
	}
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listafuenteRecursosIncial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilafuenteRecursosIncial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteIni = (String) registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listafuenteRecursosfinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilafuenteRecursosfinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFin = registroAux.getCampos().get("CODIGO").toString();
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
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * Retorna la variable fuenteIni
	 * 
	 * @return fuenteIni
	 */
	public String getFuenteIni() {
		return fuenteIni;
	}

	/**
	 * Asigna la variable fuenteIni
	 * 
	 * @param fuenteIni Variable a asignar en fuenteIni
	 */
	public void setFuenteIni(String fuenteIni) {
		this.fuenteIni = fuenteIni;
	}

	/**
	 * Retorna la variable fuenteFin
	 * 
	 * @return fuenteFin
	 */
	public String getFuenteFin() {
		return fuenteFin;
	}

	/**
	 * Asigna la variable fuenteFin
	 * 
	 * @param fuenteFin Variable a asignar en fuenteFin
	 */
	public void setFuenteFin(String fuenteFin) {
		this.fuenteFin = fuenteFin;
	}

	/**
	 * Retorna la variable nombreMes
	 * 
	 * @return nombreMes
	 */
	public String getNombreMes() {
		return nombreMes;
	}

	/**
	 * Asigna la variable nombreMes
	 * 
	 * @param nombreMes Variable a asignar en nombreMes
	 */
	public void setNombreMes(String nombreMes) {
		this.nombreMes = nombreMes;
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
	 * Retorna la lista listaanio
	 * 
	 * @return listaanio
	 */
	public List<Registro> getListaanio() {
		return listaanio;
	}

	/**
	 * Asigna la lista listaanio
	 * 
	 * @param listaanio Variable a asignar en listaanio
	 */
	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listafuenteRecursosIncial
	 * 
	 * @return listafuenteRecursosIncial
	 */
	public RegistroDataModelImpl getListafuenteRecursosIncial() {
		return listafuenteRecursosIncial;
	}

	/**
	 * Asigna la lista listafuenteRecursosIncial
	 * 
	 * @param listafuenteRecursosIncial Variable a asignar en
	 *                                  listafuenteRecursosIncial
	 */
	public void setListafuenteRecursosIncial(RegistroDataModelImpl listafuenteRecursosIncial) {
		this.listafuenteRecursosIncial = listafuenteRecursosIncial;
	}

	/**
	 * Retorna la lista listafuenteRecursosfinal
	 * 
	 * @return listafuenteRecursosfinal
	 */
	public RegistroDataModelImpl getListafuenteRecursosfinal() {
		return listafuenteRecursosfinal;
	}

	/**
	 * Asigna la lista listafuenteRecursosfinal
	 * 
	 * @param listafuenteRecursosfinal Variable a asignar en
	 *                                 listafuenteRecursosfinal
	 */
	public void setListafuenteRecursosfinal(RegistroDataModelImpl listafuenteRecursosfinal) {
		this.listafuenteRecursosfinal = listafuenteRecursosfinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the tipoVigencia
	 */
	public String getTipoVigencia() {
		return tipoVigencia;
	}

	/**
	 * @param tipoVigencia the tipoVigencia to set
	 */
	public void setTipoVigencia(String tipoVigencia) {
		this.tipoVigencia = tipoVigencia;
	}

	/**
	 * @return the listatipoVigencia
	 */
	public List<Registro> getListatipoVigencia() {
		return listatipoVigencia;
	}

	/**
	 * @param listatipoVigencia the listatipoVigencia to set
	 */
	public void setListatipoVigencia(List<Registro> listatipoVigencia) {
		this.listatipoVigencia = listatipoVigencia;
	}
}
