/*-
 * conciliacionesBancariasControlador.java
 *
 * 1.0
 * 
 * 07/09/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ConciliacionesBancariasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 07/09/2022
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class conciliacionesBancariasControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private String compania;
	private String modulo;
	private String nombreCompania;
	private String cuenta;
	private int ano;
	private int mes;
	String clase = "";
	private String estadoAnio;
    private String clasesContables;
	private StreamedContent archivoDescarga;
	private List<Registro> listaAno;
	private RegistroDataModelImpl listaCuenta;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ConciliacionesBancariasControlador
	 */
	public conciliacionesBancariasControlador() {
		try {
			numFormulario = GeneralCodigoFormaEnum.CONCILIACIONES_BANCARIAS_CONTROLADOR.getCodigo();
			compania = SessionUtil.getCompania();
			modulo = SessionUtil.getModulo();
			nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(conciliacionesBancariasControlador.class.getName()).log(Level.SEVERE, null, ex);
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
		clasesContables = getClasesContables();
		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH) + 1;
		abrirFormulario();
		
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		cargarListaAno();
		cargarListaCuenta();
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConciliacionesBancariasControladorUrlEnum.URL001.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCuenta
	 *
	 */
	public void cargarListaCuenta() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConciliacionesBancariasControladorUrlEnum.URL003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), Integer.toString(ano));
		listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
     * Trae las clases contables para conciliacion bancaria.
     *
     * @return cadena con las clases separadas por coma.
     */
	 private String getClasesContables()
	    {
	        String clase = "";
	        try
	        {
	            clase = SysmanFunciones.nvl(
	            		ejbSysmanUtil.consultarParametro(compania,
	                                            "CLASES CONTABLES EN CONCILIACION BANCARIA",
	                                            SessionUtil.getModulo(), new Date(),
	                                            false),
	                            "").toString();
	            
	             clase = SysmanFunciones.separarCaracteres(clase, ",");
	             clase = clase.replace("", "'");
	            // clase = "'" + clase + "'";
	        }
	        catch (SystemException e)
	        {
	            Logger.getLogger(SubConciliacionControlador.class.getName())
	                            .log(Level.SEVERE, null, e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	        return clase;
	    }
	
	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.EXCEL);
	}

	private void generarReporte(ReportesBean.FORMATOS formato) {
		
		
    		Map<String, Object> reemplazos = new HashMap<>();
			reemplazos.put("compania", compania);
			reemplazos.put("ano", ano);
			reemplazos.put("mes", mes);
			reemplazos.put("cuenta", cuenta);
			reemplazos.put("clasescontables", clasesContables);

			String strSql = Reporteador.resuelveConsulta("800543VERPARTIDASCONCILIATORIAS",
					Integer.parseInt(modulo), reemplazos);

	        try (ByteArrayOutputStream out = new ByteArrayOutputStream();)

	        {
	                Workbook workbook = new XSSFWorkbook(
	                                JsfUtil.exportarHojaDatosStreamed(strSql,
	                                                ConectorPool.ESQUEMA_SYSMAN,
	                                                FORMATOS.EXCEL).getStream());
	                
	                workbook.setForceFormulaRecalculation(true);
	                workbook.write(out);
	                out.close();
	                workbook.close();
				
				archivoDescarga = JsfUtil.getArchivoDescarga(
						new ByteArrayInputStream(out.toByteArray()),
						  SysmanFunciones.concatenar("VER PARTIDAS CONCILIATORIAS", ".xlsx"));
				
			} catch (SysmanException | JRException | IOException | SQLException | DRException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}				

	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuenta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuenta(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		cuenta = registroAux.getCampos()
                .get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	/**
	 * Retorna la variable cuenta
	 * 
	 * @return cuenta
	 */
	public String getCuenta() {
		return cuenta;
	}

	/**
	 * Asigna la variable cuenta
	 * 
	 * @param cuenta Variable a asignar en cuenta
	 */
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	/**
	 * Retorna la variable nombreCompania
	 * 
	 * @return nombreCompania
	 */
	public String getNombreCompania() {
		return nombreCompania;
	}

	/**
	 * Asigna la variable nombreCompania
	 * 
	 * @param nombreCompania Variable a asignar en nombreCompania
	 */
	public void setNombreCompania(String nombreCompania) {
		this.nombreCompania = nombreCompania;
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
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCuenta
	 * 
	 * @return listaCuenta
	 */
	public RegistroDataModelImpl getListaCuenta() {
		return listaCuenta;
	}

	/**
	 * Asigna la lista listaCuenta
	 * 
	 * @param listaCuenta Variable a asignar en listaCuenta
	 */
	public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
		this.listaCuenta = listaCuenta;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
