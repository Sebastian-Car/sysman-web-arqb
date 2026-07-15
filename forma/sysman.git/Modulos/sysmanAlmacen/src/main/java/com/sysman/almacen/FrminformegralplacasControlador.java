/*-
 * FrminformegralplacasControlador.java
 *
 * 1.0
 * 
 * 29/10/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.enums.FrminformegralplacasControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 29/10/2024
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrminformegralplacasControlador extends BeanBaseModal {
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
	private boolean excelPlano;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String elementoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String elementoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreElementoIni;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreElementoFin;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fecha;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaelementoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaelementoFinal;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminformegralplacasControlador
	 */
	public FrminformegralplacasControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRMINFORMEGRALPLACAS_CONTROLADOR.getCodigo();
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
		
		fecha = new Date();
		cargarListaelementoInicial();
		cargarListaelementoFinal();
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
	 * Carga la lista listaelementoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaelementoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrminformegralplacasControladorUrlEnum.URL112032
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);
		
		listaelementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}

	/**
	 * 
	 * Carga la lista listaelementoFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaelementoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrminformegralplacasControladorUrlEnum.URL112034
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);
		
		param.put(GeneralParameterEnum.CODIGO.getName(),
		                elementoInicial);
		
		listaelementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(FORMATOS.PDF,"002646InformeGralPorPlaca");
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if(excelPlano)
		{
			generarReporte(FORMATOS.EXCEL, "800662InformeGralPorPlaca");
		}
		else
		{
			generarReporte(FORMATOS.EXCEL, "002646InformeGralPorPlaca");
		}
		
		// </CODIGO_DESARROLLADO>
	}
	
	public void generarReporte(FORMATOS formato, String reporte)
    {		
        try
        {        	
        	HashMap<String, Object> reemplazar = new HashMap<>();   
            reemplazar.put("compania", compania);
            reemplazar.put("elementoInicial", elementoInicial);
            reemplazar.put("elementoFinal", elementoFinal);
            reemplazar.put("elementoFinal", elementoFinal);
            reemplazar.put("fechaCorte", SysmanFunciones.convertirAFechaCadena(fecha));
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()), reemplazar);
            if(excelPlano && formato == FORMATOS.EXCEL)
    		{
            	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL,
    					reporte);
    		}
            else
    		{
	            parametros.put("PR_STRSQL", strSql);
	            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
	                            ConectorPool.ESQUEMA_SYSMAN, formato);
    		}
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " " + ex.getMessage() + " " + reporte);
            Logger.getLogger(FrminformegralplacasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | OutOfMemoryError | IOException
                        | JRException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                            + e.getMessage());
            Logger.getLogger(InvgraldevoluControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (SQLException | DRException | SysmanException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
	
	/**
	 * 
	 * Metodo ejecutado para imprimir el informe en excel plano
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
/*	public void generalExcelPlano() 
	{
		String reporte = "800662InformeGralPorPlaca";	
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
	        reemplazar.put("elementoInicial", elementoInicial);
	        reemplazar.put("elementoFinal", elementoFinal);
	        reemplazar.put("elementoFinal", elementoFinal);
	        reemplazar.put("fechaCorte", "'" +
	                SysmanFunciones.convertirAFechaCadena(fecha) + "' ");
			String sql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);
			
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL,
					reporte);		

		} catch (JRException | IOException | SQLException | DRException | SysmanException |  ParseException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}  		
	}*/


//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaelementoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaelementoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        elementoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreElementoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();

        elementoFinal = null;
        nombreElementoFin = null;
        cargarListaelementoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaelementoFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaelementoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        elementoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreElementoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable excelPlano
	 * 
	 * @return excelPlano
	 */
	public boolean getExcelPlano() {
		return excelPlano;
	}

	/**
	 * Asigna la variable excelPlano
	 * 
	 * @param excelPlano Variable a asignar en excelPlano
	 */
	public void setExcelPlano(boolean excelPlano) {
		this.excelPlano = excelPlano;
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
	 * @param elementoInicial Variable a asignar en elementoInicial
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
	 * @param elementoFinal Variable a asignar en elementoFinal
	 */
	public void setElementoFinal(String elementoFinal) {
		this.elementoFinal = elementoFinal;
	}

	/**
	 * Retorna la variable nombreElementoIni
	 * 
	 * @return nombreElementoIni
	 */
	public String getNombreElementoIni() {
		return nombreElementoIni;
	}

	/**
	 * Asigna la variable nombreElementoIni
	 * 
	 * @param nombreElementoIni Variable a asignar en nombreElementoIni
	 */
	public void setNombreElementoIni(String nombreElementoIni) {
		this.nombreElementoIni = nombreElementoIni;
	}

	/**
	 * Retorna la variable nombreElementoFin
	 * 
	 * @return nombreElementoFin
	 */
	public String getNombreElementoFin() {
		return nombreElementoFin;
	}

	/**
	 * Asigna la variable nombreElementoFin
	 * 
	 * @param nombreElementoFin Variable a asignar en nombreElementoFin
	 */
	public void setNombreElementoFin(String nombreElementoFin) {
		this.nombreElementoFin = nombreElementoFin;
	}

	/**
	 * Retorna la variable fecha
	 * 
	 * @return fecha
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * Asigna la variable fecha
	 * 
	 * @param fecha Variable a asignar en fecha
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
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
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaelementoInicial
	 * 
	 * @return listaelementoInicial
	 */
	public RegistroDataModelImpl getListaelementoInicial() {
		return listaelementoInicial;
	}

	/**
	 * Asigna la lista listaelementoInicial
	 * 
	 * @param listaelementoInicial Variable a asignar en listaelementoInicial
	 */
	public void setListaelementoInicial(RegistroDataModelImpl listaelementoInicial) {
		this.listaelementoInicial = listaelementoInicial;
	}

	/**
	 * Retorna la lista listaelementoFinal
	 * 
	 * @return listaelementoFinal
	 */
	public RegistroDataModelImpl getListaelementoFinal() {
		return listaelementoFinal;
	}

	/**
	 * Asigna la lista listaelementoFinal
	 * 
	 * @param listaelementoFinal Variable a asignar en listaelementoFinal
	 */
	public void setListaelementoFinal(RegistroDataModelImpl listaelementoFinal) {
		this.listaelementoFinal = listaelementoFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
