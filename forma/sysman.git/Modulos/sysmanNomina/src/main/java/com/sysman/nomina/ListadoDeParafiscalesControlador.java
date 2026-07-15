/*-
 * ListadoDeParafiscalesControlador.java
 *
 * 1.0
 * 
 * 03/05/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
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
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
 *
 * @version 1.0, 03/05/2022
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class  ListadoDeParafiscalesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String opcion;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	private String proceso;
	private String ano;
	private String mes;
	private String nombreMes;
	private String periodo;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Implementacion del EJB de SysmanUtil para acceder a funciones y/o
	 * procedimientos definidos en el paquete PCK_SYSMAN_UTL
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Crea una nueva instancia de ListadoDeParafiscalesControlador
	 */
	public ListadoDeParafiscalesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2351;
			validarPermisos();
			opcion = "2";
			
			proceso = SessionUtil.getSessionVar("procesoNomina").toString();
			ano = SessionUtil.getSessionVar("anioNomina").toString();
			mes = SessionUtil.getSessionVar("mesNomina").toString();
			nombreMes = SessionUtil.getSessionVar("nombreMesNomina").toString();
			periodo = SessionUtil.getSessionVar("periodoNomina").toString();
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
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	
	public void generarInforme(ReportesBean.FORMATOS formato) {
		archivoDescarga = null;

		try {

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("procesoNomina", proceso);
			reemplazar.put("anioNomina", ano);
			reemplazar.put("mesNomina", mes);
			reemplazar.put("mes", mes);
			reemplazar.put("periodo", periodo);

			String nombreReporte = "";
			
			if(opcion.equals("1")) {
				nombreReporte = ejbSysmanUtil.consultarParametro(compania, "LISTADO PARAFISCALES POR DEPENDENCIA",
						modulo, new Date(), false);
			}else{
				nombreReporte = ejbSysmanUtil.consultarParametro(compania, "LISTADO PARAFISCALES POR DEPENDENCIA AGRUPADO",
						modulo, new Date(), false);
			}
			

			Map<String, Object> parametros = new HashMap<>();
		    parametros.put("compania", SessionUtil.getCompaniaIngreso().getNombre());
		    parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());


			Reporteador.resuelveConsulta(nombreReporte, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SystemException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable opcion
	 * 
	 * @return  opcion
	 */
	public String getOpcion() {
		return opcion;
	}
	/**
	 * Asigna la variable  opcion
	 * 
	 * @param  opcion
	 * Variable a asignar en  opcion
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
