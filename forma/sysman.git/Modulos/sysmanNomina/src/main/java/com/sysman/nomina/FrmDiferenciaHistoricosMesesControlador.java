/*-
 * FrmDiferenciaHistoricosMesesControlador.java
 *
 * 1.0
 * 
 * 11/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmDiferenciaHistoricosMesesControladorUrlEnum;
import com.sysman.nomina.enums.InformeembargoControladorEnum;
import com.sysman.nomina.enums.InformeembargoControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/10/2019
 * @author jalfonso
 */
@ManagedBean
@ViewScoped
public class  FrmDiferenciaHistoricosMesesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo ;
	//<DECLARAR_ATRIBUTOS>
	private String anio;
	private String mes1;
	private String mes2;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaanio;
	private List<Registro> listames1;
    private List<Registro> listames2;
	private String procesoNomina;
	private String anoNomina;
	private String mesNomina;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmDiferenciaHistoricosMesesControlador
	 */
	public FrmDiferenciaHistoricosMesesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
		anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
		mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
		try {
			numFormulario=2120;
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
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
		//<CARGAR_LISTA>

		anio = anoNomina;
		mes1 = mesNomina;
		mes2 = mesNomina;
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
		cargarListaanio();
		 cargarListames1();
		 cargarListames2();
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaanio
	 *
	 */
	public void cargarListaanio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try
		{
			listaanio = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmDiferenciaHistoricosMesesControladorUrlEnum.URL3309
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listames1
	 *
	 */
	public void cargarListames1(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(InformeembargoControladorEnum.PARAM3.getValue(),
				procesoNomina);
		param.put(InformeembargoControladorEnum.PARAM4.getValue(), anio);

		try
		{
			listames1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmDiferenciaHistoricosMesesControladorUrlEnum.URL3815
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}




	}
	
	
	/**
	 * 
	 * Carga la lista listames2
	 *
	 */
	public void cargarListames2(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(InformeembargoControladorEnum.PARAM3.getValue(),
				procesoNomina);
		param.put(InformeembargoControladorEnum.PARAM4.getValue(), anio);

		try
		{
			listames2 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmDiferenciaHistoricosMesesControladorUrlEnum.URL3815
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
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
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		generarreporte(ReportesBean.FORMATOS.EXCEL);

		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>

	public void generarreporte(ReportesBean.FORMATOS formato) {
		try {

			HashMap<String, Object> reemplazar = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();

			reemplazar.put("anio", anio);
			reemplazar.put("mes1", mes1);
			reemplazar.put("mes2", mes2);

			String reporte = "800355DiferenciaHistoricosMeses";


			String sql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);



			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,  ConectorPool.ESQUEMA_SYSMAN, formato, reporte);

		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SQLException | DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


	}
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control anio
	 * 
	 * 
	 */
	public void cambiaranio() {
		//<CODIGO_DESARROLLADO>
		 cargarListames1();
		 cargarListames2();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getMes1() {
		return mes1;
	}
	public void setMes1(String mes1) {
		this.mes1 = mes1;
	}
	public String getMes2() {
		return mes2;
	}
	public void setMes2(String mes2) {
		this.mes2 = mes2;
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
	 * @param listaanio
	 * Variable a asignar en  listaanio
	 */
	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}
	
	/**
     * Retorna la lista listames1
     * 
     * @return listames1
     */
public List<Registro> getListames1() {
        return listames1;
    }
    /**
     * Asigna la lista listames1
     * 
     * @param listames1
     * Variable a asignar en  listames1
     */
public void setListames1(List<Registro> listames1) {
        this.listames1 = listames1;
    }
    /**
     * Retorna la lista listames2
     * 
     * @return listames2
     */
public List<Registro> getListames2() {
        return listames2;
    }
    /**
     * Asigna la lista listames2
     * 
     * @param listames2
     * Variable a asignar en  listames2
     */
public void setListames2(List<Registro> listames2) {
        this.listames2 = listames2;
    }
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
