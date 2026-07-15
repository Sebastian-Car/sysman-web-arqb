/*-
 * FrminfcertificadoauditoriaControlador.java
 *
 * 1.0
 * 
 * 06/09/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;

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
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmImportarRipsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 06/09/2022
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrminfcertificadoauditoriaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Variable que almacena el código de la clase de la cuenta inicial a partir de la cual se van a buscar 
	 * las auditorias de glosas realizadas
	 */
	private String codClaseCuentaInicial;
	/**
	 * Variable que almacena la clase de la cuenta inicial a partir de la cual se van a buscar 
	 * las auditorias de glosas realizadas
	 */
	private String claseCuentaInicial;
	
	/**
	 * Variable que almacena la fecha inicial a partir de la cual se van a buscar 
	 * las auditorias de glosas realizadas
	 */
	private Date fechaInicial;
	/**
	 * Variable que almacena la fecha final a partir de la cual se van a buscar 
	 * las auditorias de glosas realizadas
	 */
	private Date fechaFinal;
	
	/**
     * Este atributo se usa para almacenar el código del tercero
     * prestador de servicio
     */
	private String tercero;
	
	/**
     * Este atributo se usa para almacenar el nombre del tercero
     * prestador de servicio
     */
    private String nombreTercero;
    
    /**
     * Este atributo se usa para almacenar el responsable de la auditoría
     */
    private String responsableAudi;
    
    /**
     * Este atributo se usa para almacenar el nombre de la firma 1 que lleva el informe
     */
    private String firma1;
    
    /**
     * Este atributo se usa para almacenar el nombre de la firma 2 que lleva el informe
     */
    private String firma2;
    
    /**
     * Este atributo se usa para almacenar el cargo de la firma 1 que lleva el informe
     */
    private String cargo1;
    
    /**
     * Este atributo se usa para almacenar el cargo de la firma 2 que lleva el informe
     */
    private String cargo2;
    
    /**
     * Check PorRadicado
     */    
    private boolean porRadicado;
    
    /**
     * Este atributo se usa para almacenar el radicado
     */ 
    private String radicadoL;
    
    private String nuevoInfCM;
    private boolean cargaPdf;
    private String tipoInforme;
    private String informeConciliacion;
    
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	/**
	 * lista para guardar la lsita de clases de cuentas
	 */
	private RegistroDataModelImpl listaclaseCuentaInicial;
		
	/**
     * Este atributo se usa para almacenar la lista de terceros
     */
    private RegistroDataModelImpl listaTercero;
    
	/**
	 * lista para guardar la lsita de radicados
	 */
	private RegistroDataModelImpl listaRadicado;
	
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	
	/**
	 * Crea una nueva instancia de FrminfcertificadoauditoriaControlador
	 */
	public FrminfcertificadoauditoriaControlador() {
		super();
		compania = SessionUtil.getCompania();
		tipoInforme = "1";
		try {
			//2367
			numFormulario = GeneralCodigoFormaEnum.FRM_INF_CERTIFICADO_AUDITORIA.getCodigo();
			validarPermisos();
			porRadicado=false;
			cargaPdf=false;
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
		cargarListaclaseCuentaInicial();
		cargarListaTercero();
		cargarListaRadicado();	
		abrirFormulario();
		
		try {
            setFechaInicial(SysmanFunciones.primeroDeMesFecha(new Date()));
            setFechaFinal(SysmanFunciones.ultimoDiaDate(new Date()));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            
        }
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() 
	{
		codClaseCuentaInicial = null;
		claseCuentaInicial = null;
		nombreTercero = null;
		
		cargarParametros();
	}

	/**
	 * 
	 * Carga la lista listaclaseCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaclaseCuentaInicial() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                FrmImportarRipsControladorUrlEnum.URL4395
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);
		
		listaclaseCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		                		urlBean.getUrlConteo().getUrl(), param,
		                		true, "CODIGO");
	}
		
	/**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() 
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarRipsControladorUrlEnum.URL4391
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }
    
	/**
     * 
     * Carga la lista listaTercero         //CC_1357 MROSERO 25/04/2025
     *
     */
    public void cargarListaRadicado() 
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarRipsControladorUrlEnum.URL4396
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaRadicado  = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CONSECUTIVO");
    }
    
    
    /**
     * 
     * Carga los valores de los parámetros necesarios para el reporte
     *
     */    
    public void cargarParametros() 
    {    	   	
    	try {
	    		firma1 = SysmanFunciones
				                    .nvl(ejbSysmanUtil.consultarParametro(compania,
				                            "FIRMA 1 CERTIFICADO AUDITORIA CUENTAS MEDICAS",
				                            SessionUtil.getModulo(),
				                            new Date(), true), "")
				                .toString();
                firma2 = SysmanFunciones
			                        .nvl(ejbSysmanUtil.consultarParametro(compania,
			                                "FIRMA 2 CERTIFICADO AUDITORIA CUENTAS MEDICAS",
			                                SessionUtil.getModulo(),
			                                new Date(), true), "")
                                .toString();
                cargo1 = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO 1 CERTIFICADO AUDITORIA CUENTAS MEDICAS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                cargo2 = SysmanFunciones
		                        .nvl(ejbSysmanUtil.consultarParametro(compania,
		                                        "CARGO 2 CERTIFICADO AUDITORIA CUENTAS MEDICAS",
		                                        SessionUtil.getModulo(),
		                                        new Date(), true), "")
		                        .toString();
                responsableAudi = SysmanFunciones
                        .nvl(ejbSysmanUtil.consultarParametro(compania,
                                        "RESPONSABLE CERTIFICADO AUDITORIA CUENTAS MEDICAS",
                                        SessionUtil.getModulo(),
                                        new Date(), true), "")
                        .toString();
                nuevoInfCM = SysmanFunciones
                		.nvl(ejbSysmanUtil.consultarParametro(compania,
                						"MANEJA NUEVO FORMATO CERTIFICADO DE AUDITORIA",
                						SessionUtil.getModulo(),
                						new Date(),true),"")
                		.toString();
                cargaPdf = nuevoInfCM.equals("SI");
                informeConciliacion = SysmanFunciones
                		.nvl(ejbSysmanUtil.consultarParametro(compania,
                						"FORMATO CERTIFICADO DE ACTA DE CONCILIACION",
                						SessionUtil.getModulo(),
                						new Date(),false),"002950ActaConciliacionCM")
                		.toString();
            }
            
        catch (SystemException e) 
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        
    }

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 */
	public void oprimirExcel() 
	{
		archivoDescarga = null;
        getInforme(FORMATOS.EXCEL97);  
	}
	
	/**
     * 
     * Metodo ejecutado al oprimir el boton Pdf
     * en la vista
     *
     *
     */
	public void oprimirPdf() 
	{
		archivoDescarga = null;
		getInforme(FORMATOS.PDF);
    }
	
	private void getInforme(FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            //CC_1357 MROSERO 25/04/2025
        	reemplazar.put("compania", compania);
        	if (porRadicado) {
        	reemplazar.put("consecutivo", radicadoL);	
        	} else {
	        reemplazar.put("Tercero", tercero);
	        reemplazar.put("ClaseCuentaInicial", codClaseCuentaInicial);       
			reemplazar.put("FechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazar.put("FechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));	       		
        	}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}
        
        
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        // MANEJO DE PARAMETROS DEL REPORTE
        parametros.put("PR_RESPONSABLE", responsableAudi);
        parametros.put("PR_FIRMA1", firma1);
        parametros.put("PR_FIRMA2", firma2);
        parametros.put("PR_CARGO1", cargo1);
        parametros.put("PR_CARGO2", cargo2);
        parametros.put("PR_NOMBRE_TERCERO", nombreTercero);
        parametros.put("PR_NIT_TERCERO", tercero); 
        parametros.put("PR_MODALIDAD", claseCuentaInicial);
        parametros.put("POR_RADICADO",porRadicado );
        
        String consulta = null;
        String reporte = "";
        //CC_1357 MROSERO 25/04/2025
        if (porRadicado) {
        	if (tipoInforme.equals("1")) {
        		consulta = cargaPdf?"002945CertificadoAuditoriaxRadicados":"002769CertificadoAuditoriaxRadicados";
        	} else {
        		consulta = "002951ActaConciliacionxRadicado";
        	}        	
        	String nombreEntidad = SessionUtil.getCompaniaIngreso().getNombre();
        	parametros.put("PR_NOMBRE_TERCERO", nombreEntidad);
        	parametros.put("PR_NIT_TERCERO", tercero + " - " + nombreTercero);        	

        }else {
        	if (tipoInforme.equals("1")) {
        		consulta = cargaPdf?"002943CertificadoAuditoriaCM":"002382CertificadoAuditoriaCM";
        	} else {
        		consulta = informeConciliacion;
        	}
        }
        
        if (tipoInforme.equals("1")) {
        	reporte = cargaPdf?"002943CertificadoAuditoriaCM":"002382CertificadoAuditoriaCM";
        } else {
        	reporte = informeConciliacion;
        }
        
   
        

        Reporteador.resuelveConsulta(consulta,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        } catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

	/**
	 * Metodo ejecutado al cambiar el control fechaInicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarfechaInicial() 
	{
		if(fechaFinal==null)
            fechaFinal= fechaInicial; 
	}
	
	/**
	 * Metodo ejecutado al cambiar el Radicado
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarRadicado() {
        //<CODIGO_DESARROLLADO>
       //</CODIGO_DESARROLLADO>
   }

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaclaseCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaclaseCuentaInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		codClaseCuentaInicial = SysmanFunciones.nvl(
        		registroAux.getCampos().get(
                        GeneralParameterEnum.CODIGO.getName()),
        		" ")
        		.toString();
		claseCuentaInicial = SysmanFunciones.nvl(
                		registroAux.getCampos().get(
                                GeneralParameterEnum.NOMBRE.getName()),
                		" ")
                		.toString();
	}
		
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        tercero = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
        nombreTercero = SysmanFunciones
                .nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
    }
    //CC_1357 MROSERO 25/04/2025
    public void seleccionarFilaRadicado(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	        radicadoL= SysmanFunciones.toString((registroAux.getCampos().get("CONSECUTIVO")));
    	        nombreTercero =SysmanFunciones.toString((registroAux.getCampos().get("NOMBRE")));
    	        tercero =SysmanFunciones.toString((registroAux.getCampos().get("NIT")));
    	}

	/**
	 * Retorna la variable claseCuentaInicial
	 * 
	 * @return claseCuentaInicial
	 */
	public String getClaseCuentaInicial() {
		return claseCuentaInicial;
	}

	/**
	 * Asigna la variable claseCuentaInicial
	 * 
	 * @param claseCuentaInicial Variable a asignar en claseCuentaInicial
	 */
	public void setClaseCuentaInicial(String claseCuentaInicial) {
		this.claseCuentaInicial = claseCuentaInicial;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * @return the listaclaseCuentaInicial
	 */
	public RegistroDataModelImpl getListaclaseCuentaInicial() {
		return listaclaseCuentaInicial;
	}

	/**
	 * @param listaclaseCuentaInicial the listaclaseCuentaInicial to set
	 */
	public void setListaclaseCuentaInicial(RegistroDataModelImpl listaclaseCuentaInicial) {
		this.listaclaseCuentaInicial = listaclaseCuentaInicial;
	}
	
	/**
	 * @return the nombreTercero
	 */
	public String getNombreTercero() {
		return nombreTercero;
	}

	/**
	 * @param nombreTercero the nombreTercero to set
	 */
	public void setNombreTercero(String nombreTercero) {
		this.nombreTercero = nombreTercero;
	}

	/**
	 * @return the listaTercero
	 */
	public RegistroDataModelImpl getListaTercero() {
		return listaTercero;
	}

	/**
	 * @param listaTercero the listaTercero to set
	 */
	public void setListaTercero(RegistroDataModelImpl listaTercero) {
		this.listaTercero = listaTercero;
	}

	/**
	 * @return the codClaseCuentaInicial
	 */
	public String getCodClaseCuentaInicial() {
		return codClaseCuentaInicial;
	}

	/**
	 * @param codClaseCuentaInicial the codClaseCuentaInicial to set
	 */
	public void setCodClaseCuentaInicial(String codClaseCuentaInicial) {
		this.codClaseCuentaInicial = codClaseCuentaInicial;
	}

	/**
	 * @return the tercero
	 */
	public String getTercero() {
		return tercero;
	}

	/**
	 * @param tercero the tercero to set
	 */
	public void setTercero(String tercero) {
		this.tercero = tercero;
	}

	/**
	 * @return the responsableAudi
	 */
	public String getResponsableAudi() {
		return responsableAudi;
	}

	/**
	 * @param responsableAudi the responsableAudi to set
	 */
	public void setResponsableAudi(String responsableAudi) {
		this.responsableAudi = responsableAudi;
	}

	/**
	 * @return the firma1
	 */
	public String getFirma1() {
		return firma1;
	}

	/**
	 * @param firma1 the firma1 to set
	 */
	public void setFirma1(String firma1) {
		this.firma1 = firma1;
	}

	/**
	 * @return the firma2
	 */
	public String getFirma2() {
		return firma2;
	}

	/**
	 * @param firma2 the firma2 to set
	 */
	public void setFirma2(String firma2) {
		this.firma2 = firma2;
	}

	/**
	 * @return the cargo1
	 */
	public String getCargo1() {
		return cargo1;
	}

	/**
	 * @param cargo1 the cargo1 to set
	 */
	public void setCargo1(String cargo1) {
		this.cargo1 = cargo1;
	}

	/**
	 * @return the cargo2
	 */
	public String getCargo2() {
		return cargo2;
	}

	/**
	 * @param cargo2 the cargo2 to set
	 */
	public void setCargo2(String cargo2) {
		this.cargo2 = cargo2;
	}

	public boolean isPorRadicado() {
		return porRadicado;
	}

	public void setPorRadicado(boolean porRadicado) {
		this.porRadicado = porRadicado;
	}

	public String getRadicadoL() {
		return radicadoL;
	}

	public void setRadicadoL(String radicadoL) {
		this.radicadoL = radicadoL;
	}
	
	public boolean isCargaPdf() {
		return cargaPdf;
	}

	public void setCargaPdf(boolean cargaPdf) {
		this.cargaPdf = cargaPdf;
	}
	
	public String getTipoInforme() {
        return tipoInforme;
    }
	
	public void setTipoInforme(String tipoInforme) {
        this.tipoInforme = tipoInforme;
    }

	public RegistroDataModelImpl getListaRadicado() {
		return listaRadicado;
	}

	public void setListaRadicado(RegistroDataModelImpl listaRadicado) {
		this.listaRadicado = listaRadicado;
	}
	
}
