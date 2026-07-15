/*-
 * Frminformenormativofac120Controlador.java
 *
 * 1.0
 * 
 * 06/04/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

import com.sysman.auditoriacuentasmedicas.ejb.EjbAuditoriaCuentasMedicasCeroLocal;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.Frminformenormativofac120ControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.DatosSesion;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CompaniasControladorEnum;

import java.util.Map;
import java.util.TreeMap;




/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 06/04/2026
 * @author CristianFerneySuescu
 */
@ManagedBean
@ViewScoped
public class  Frminformenormativofac120Controlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	/**
	 * Identificador de la compania asociada al proceso.
	 */
	private final String compania;

	/**
	 * Codigo o identificador de la clase de cuenta inicial utilizada como filtro.
	 */
	private String claseCuentaInicial;

	/**
	 * Codigo o identificador de la clase de cuenta final utilizada como filtro.
	 */
	private String claseCuentaFinal;

	/**
	 * Fecha inicial del rango de consulta o procesamiento.
	 */
	private Date fechaInicial;

	/**
	 * Fecha final del rango de consulta o procesamiento.
	 */
	private Date fechaFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	
	private int codigoEntidad;
	private String departamento;
	private String ciudad;
	private String tipoEntidad;
	private String codigoUbicacion;
	
	
	/**
	 * Archivo generado para descarga por el usuario.
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Lista de datos para la seleccion de la clase de cuenta inicial.
	 */
	private RegistroDataModelImpl listaClaseCuentaInicial;

	/**
	 * Lista de datos para la seleccion de la clase de cuenta final.
	 */
	private RegistroDataModelImpl listaClaseCuentaFinal;
	
	@EJB
    private EjbAuditoriaCuentasMedicasCeroLocal ejbAuditoriaCuentasMedicasCero;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	private String nombreReporte = "800731InformeCarteraFAC120";
	/**
	 * Crea una nueva instancia de Frminformenormativofac120Controlador
	 */
	public Frminformenormativofac120Controlador() {
		super();
		 compania = SessionUtil.getCompania();
		 codigoEntidad = SessionUtil.getCompaniaIngreso().getTipoEntidad();
    	 departamento = SessionUtil.getCompaniaIngreso().getCodigoDepartamento();
    	 ciudad = SessionUtil.getCompaniaIngreso().getCodigoCiudad();
    	
    	
		try {
			//Codigo formulario = 2575
			numFormulario= GeneralCodigoFormaEnum.FRM_INF_NORMATIVO_FAC120_CONTROLADOR.getCodigo();
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
	public void inicializar() {
		cargarListaClaseCuentaInicial();
		cargarListaClaseCuentaFinal();

		abrirFormulario();

		try {
			fechaInicial=SysmanFunciones.primeroDeMesFecha(new Date());
			fechaFinal= SysmanFunciones.ultimoDiaDate(new Date());
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		claseCuentaInicial = null;
		claseCuentaFinal = null;
	}


	//<METODOS_CARGAR_LISTA>
	/*
	 * Carga la lista listaClaseCuentaInicial
	 *
	 */
	public void cargarListaClaseCuentaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						Frminformenormativofac120ControladorUrlEnum.URL4395
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaClaseCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");

	}
	/**
	 * 
	 * Carga la lista listaClaseCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaClaseCuentaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						Frminformenormativofac120ControladorUrlEnum.URL4395
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaClaseCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}

	/**
	 * Genera y descarga el archivo Excel del informe FT024.
	 * Se ejecuta al oprimir el boton Excel en la vista,
	 * construyendo los parametros y generando el reporte.
	 * En caso de error se registra en log y se muestra mensaje.
	 */
	public void oprimirExcel() {
		archivoDescarga = null;	

		ArrayList<String> informes=new ArrayList<String>();

		try {
			String agrupado = ejbSysmanUtil.consultarParametro(compania, "CAUSACION DE CUENTAS MEDICAS AGRUPADO", "84", new Date(), false);
			agrupado = agrupado.equals("SI")?"1":"0";

			Map<String, Object> reemplazos = new TreeMap<>();

			reemplazos.put("compania", compania);
			reemplazos.put("claseInicial", claseCuentaInicial);
			reemplazos.put("claseFinal",claseCuentaFinal);
			reemplazos.put("agrupado",agrupado);
			reemplazos.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazos.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));

			informes.add(Reporteador.resuelveConsulta("800731InformeCarteraFAC120",
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos));
			
			generaNombreReporte();

			archivoDescarga= JsfUtil.exportarHojaDatosStreamed(informes.get(0), ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL, nombreReporte);


		} catch (JRException | IOException | ParseException | SQLException | DRException |com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}  
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar en la vista
	 *
	 */
	public void oprimirTxt() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.TXT);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Actualiza valores al cambiar la fecha inicial.
	 * Si la fecha final es nula la asigna igual,
	 * reinicia las clases de cuenta y recarga las listas.
	 */
	public void cambiarFechaInicial() 
	{
		if(fechaFinal==null)
			fechaFinal= fechaInicial;
		claseCuentaInicial = null;
		claseCuentaFinal = null;
		cargarListaClaseCuentaInicial();
		cargarListaClaseCuentaFinal();

	}
	
	public void generarInforme(FORMATOS formato) {
	    try {
	        Map<String, Object> param = new TreeMap<>();

	        if (FORMATOS.TXT.equals(formato)) {
	            
				generaNombreReporte();
				
	            String contenido = ejbAuditoriaCuentasMedicasCero.informeFAC120_plano(
	                                                compania,
	                                                fechaInicial,
	                                                fechaFinal,
	                                                claseCuentaInicial,
	                                                claseCuentaFinal);

	            if (contenido == null || contenido.isEmpty()) {
	                JsfUtil.agregarMensajeError("No se encontraron registros para el periodo seleccionado.");
	                return;
	            }

	            // GENERAR Y DESCARGAR EL ARCHIVO TXT
	            ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(contenido);

				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, nombreReporte + ".txt");
			
	      
	        }

	    } catch (Exception e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError("Error al generar el informe: " + e.getMessage());
	    }
	}

	private void generaNombreReporte() {
		// Determinar tipo de entidad según codigo
		if (codigoEntidad == 1) {
			tipoEntidad = "DE"; // Departamento
			if (departamento == null || departamento.isEmpty()) {
				JsfUtil.agregarMensajeError("La compañía no tiene configurado código departamento.");
				return;
			} else {
				codigoUbicacion = String.format("%012d", Integer.parseInt(departamento));
			}
		} else if (codigoEntidad == 2) {
			tipoEntidad = "DI"; // Distrito/Municipio
			if (ciudad == null || ciudad.isEmpty()) {
				JsfUtil.agregarMensajeError("La compañía no tiene configurado código ciudad.");
				return;
			} else {
				codigoUbicacion = String.format("%012d", Integer.parseInt(ciudad));
			}
		} else {
			JsfUtil.agregarMensajeError("Tipo de entidad no válido para generar el informe FAC120");
			return;
		}
        
        // Formatear fecha de corte (último día del mes final)
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaFinal);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        String fechaCorte = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());	            
        // Construir nombre del archivo
        // FAC + 120 + FSSM + AAAAMMDD + XX + XXXXXXXXXXXX + .TXT
        nombreReporte = "FAC120FSSM" + fechaCorte + tipoEntidad + codigoUbicacion;
        		
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaClaseCuentaInicial
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaClaseCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		claseCuentaInicial = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
				.toString();
	}


	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaClaseCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaClaseCuentaFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		claseCuentaFinal = SysmanFunciones.nvl(
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
				" ")
				.toString();
	}
	
	// Metodos getters y setters
	
	/**
	 * Retorna la variable claseCuentaInicial
	 * 
	 * @return  claseCuentaInicial
	 */
	public String getClaseCuentaInicial() {
		return claseCuentaInicial;
	}
	
	
	/**
	 * Asigna la variable  claseCuentaInicial
	 * 
	 * @param  claseCuentaInicial
	 * Variable a asignar en  claseCuentaInicial
	 */
	public void setClaseCuentaInicial(String claseCuentaInicial) {
		this.claseCuentaInicial = claseCuentaInicial;
	}
	
	
	/**
	 * Retorna la variable claseCuentaFinal
	 * 
	 * @return  claseCuentaFinal
	 */
	public String getClaseCuentaFinal() {
		return claseCuentaFinal;
	}
	
	
	/**
	 * Asigna la variable  claseCuentaFinal
	 * 
	 * @param  claseCuentaFinal
	 * Variable a asignar en  claseCuentaFinal
	 */
	public void setClaseCuentaFinal(String claseCuentaFinal) {
		this.claseCuentaFinal = claseCuentaFinal;
	}
	
	
	/**
	 * Retorna la variable FechaInicial
	 * 
	 * @return FechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * Asigna la variable FechaInicial
	 * 
	 * @param FechaInicial Variable a asignar en FechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * Retorna la variable FechaFinal
	 * 
	 * @return FechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * Asigna la variable FechaFinal
	 * 
	 * @param FechaFinal Variable a asignar en FechaFinal
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

	/**
	 * Retorna la lista listaClaseCuentaInicial
	 * 
	 * @return listaClaseCuentaInicial
	 */
	public RegistroDataModelImpl getListaClaseCuentaInicial() {
		return listaClaseCuentaInicial;
	}
	
	
	/**
	 * Asigna la lista listaClaseCuentaInicial
	 * 
	 * @param listaClaseCuentaInicial
	 * Variable a asignar en  listaClaseCuentaInicial
	 */
	public void setListaClaseCuentaInicial(RegistroDataModelImpl listaClaseCuentaInicial) {
		this.listaClaseCuentaInicial = listaClaseCuentaInicial;
	}
	
	/**
	 * Retorna la lista listaClaseCuentaFinal
	 * 
	 * @return listaClaseCuentaFinal
	 */
	public RegistroDataModelImpl getListaClaseCuentaFinal() {
		return listaClaseCuentaFinal;
	}
	
	
	/**
	 * Asigna la lista listaClaseCuentaFinal
	 * 
	 * @param listaClaseCuentaFinal
	 * Variable a asignar en  listaClaseCuentaFinal
	 */
	public void setListaClaseCuentaFinal(RegistroDataModelImpl listaClaseCuentaFinal) {
		this.listaClaseCuentaFinal = listaClaseCuentaFinal;
	}

}
