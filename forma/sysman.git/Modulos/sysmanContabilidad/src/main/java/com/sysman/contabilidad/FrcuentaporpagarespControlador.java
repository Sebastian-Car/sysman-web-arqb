/*-
 * FrcuentaporpagarespControlador.java
 *
 * 1.0
 * 
 * 05/11/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
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
import com.sysman.contabilidad.enums.AnalisiscarteracxcControladorEnum;
import com.sysman.contabilidad.enums.AnalisiscarteracxcControladorUrlEnum;
import com.sysman.contabilidad.enums.frcuentaporpagarespControladorEnum;
import com.sysman.contabilidad.enums.frcuentaporpagarespControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
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

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;


/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 05/11/2025
 * @author CFBARRERA
 */
@ManagedBean
@ViewScoped
public class  FrcuentaporpagarespControlador extends BeanBaseModal{
	/**
	 * Clase que maneja los parámetros de consulta o generación de reportes contables.
	 * Contiene información de compañía, rango de cuentas, rango de terceros,
	 * fecha de corte, año, y archivo generado para descarga.
	 */
    /** Compañía asociada al reporte. */
    private final String compania;

    /** Cuenta contable inicial. */
    private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;

    /** Cuenta contable final (valor por defecto en SysmanConstantes). */
    private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    /** Tercero inicial. */
   
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;

    /** Tercero final (valor por defecto en SysmanConstantes). */
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;

    /** Fecha de corte. */
    private Date fechaCorte;

    /** Anio contable. */
    private int ano;

    /** Archivo generado para descarga. */
    private StreamedContent archivoDescarga;

    /** Lista de cuentas iniciales. */
    private RegistroDataModelImpl listaCuentaInicial;

    /** Lista de cuentas finales. */
    private RegistroDataModelImpl listaCuentaFinal;

    /** Lista de terceros iniciales. */
    private RegistroDataModelImpl listaTerceroInicial;

    /** Lista de terceros finales. */
    private RegistroDataModelImpl listaTerceroFinal;

    /** Tipos de cuenta incluidos (por ejemplo: "P,E"). */
    private String clasecuenta = "P,E";


	public FrcuentaporpagarespControlador() {
		super();
		compania = SessionUtil.getCompania();
		fechaCorte = new Date();
		ano = SysmanFunciones.ano(fechaCorte);
		try {
			numFormulario=GeneralCodigoFormaEnum.FRCUENTAPORPAGARESPCONTROLADOR
					.getCodigo();
			validarPermisos();
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
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
		cargarListaTerceroInicial();
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		
	}
	
	/**
     * Carga la lista de cuentas iniciales segun la compania, fecha y clase de cuenta.
     */
	public void cargarListaCuentaInicial(){
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							frcuentaporpagarespControladorUrlEnum.URL29025
							.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.FECHA.getName(), SysmanFunciones.convertirAFechaCadena(fechaCorte));

			param.put(GeneralParameterEnum.CLASECUENTA.getName(), clasecuenta);

			listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
     * Carga la lista de cuentas finales segun la compania, fecha y clase de cuenta.
     */
	public void cargarListaCuentaFinal(){
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							frcuentaporpagarespControladorUrlEnum.URL29025
							.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.FECHA.getName(), SysmanFunciones.convertirAFechaCadena(fechaCorte));

			param.put(GeneralParameterEnum.CLASECUENTA.getName(), clasecuenta);

			listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, "CODIGO");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
     * Carga la lista de terceros iniciales segun la compania.
     */
	public void cargarListaTerceroInicial(){	
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(frcuentaporpagarespControladorUrlEnum.URL2759.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"NIT");
	}

	/**
     * Carga la lista de terceros finales segun la compania y el tercero inicial seleccionado.
     */
	public void cargarListaTerceroFinal(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(frcuentaporpagarespControladorUrlEnum.URL3332.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(compania));
		param.put(frcuentaporpagarespControladorEnum.TERCEROINICIAL.getValue(), terceroInicial);

		listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");

	}

	 /**
     * Genera el reporte en formato Excel.
     * Inicializa el archivo de descarga y llama al metodo obtenerReporte().
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
    }

    /**
     * Genera el reporte en formato PDF.
     * Inicializa el archivo de descarga y llama al metodo obtenerReporte().
     */
    public void oprimirPdf() {
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
    }

    /**
     * Genera el reporte segun el formato indicado.
     * Configura los parametros y crea el archivo de descarga.
     *
     * @param formatos Formato del reporte (Excel o PDF).
     */
    public void obtenerReporte(FORMATOS formatos) {
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cuentaInicial",cuentaInicial);
			reemplazar.put("cuentaFinal",cuentaFinal);
			reemplazar.put("fechaCorte",sdf.format(fechaCorte));
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_NOMBRECOMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
            
            java.sql.Date fechaSql = new java.sql.Date(fechaCorte.getTime());
            parametros.put("PR_FECHACORTE", fechaSql);
            
			Reporteador.resuelveConsulta("002869AnalisisCarteraCXCEspecializado",
					Integer.parseInt(SessionUtil.getModulo()),reemplazar, parametros);
						
            archivoDescarga = JsfUtil.exportarStreamed(
                    "002869AnalisisCarteraCXCEspecializado", parametros,
                    ConectorPool.ESQUEMA_SYSMAN, formatos);

		} catch (IOException  | JRException | SysmanException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
    /**
     * Asigna la cuenta inicial seleccionada en la tabla.
     * Reinicia el valor de cuenta final.
     *
     * @param event Evento de seleccion de fila.
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * Asigna la cuenta final seleccionada en la tabla.
     *
     * @param event Evento de seleccion de fila.
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * Asigna el tercero inicial seleccionado.
     * Reinicia el tercero final y actualiza su lista.
     *
     * @param event Evento de seleccion de fila.
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        cargarListaTerceroFinal();
    }

    /**
     * Asigna el tercero final seleccionado.
     *
     * @param event Evento de seleccion de fila.
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
    }


	public String getCuentaInicial() {
		return cuentaInicial;
	}
	/**
	 * Asigna la variable  cuentaInicial
	 * 
	 * @param  cuentaInicial
	 * Variable a asignar en  cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}
	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return  cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}
	/**
	 * Asigna la variable  cuentaFinal
	 * 
	 * @param  cuentaFinal
	 * Variable a asignar en  cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}
	/**
	 * Retorna la variable terceroInicial
	 * 
	 * @return  terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}
	/**
	 * Asigna la variable  terceroInicial
	 * 
	 * @param  terceroInicial
	 * Variable a asignar en  terceroInicial
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}
	/**
	 * Retorna la variable terceroFinal
	 * 
	 * @return  terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}
	/**
	 * Asigna la variable  terceroFinal
	 * 
	 * @param  terceroFinal
	 * Variable a asignar en  terceroFinal
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}
	/**
	 * Retorna la variable fechaCorte
	 * 
	 * @return  fechaCorte
	 */

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @return the fechaCorte
	 */
	public Date getFechaCorte() {
		return fechaCorte;
	}
	/**
	 * @param fechaCorte the fechaCorte to set
	 */
	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCuentaInicial
	 * 
	 * @return listaCuentaInicial
	 */
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}
	/**
	 * Asigna la lista listaCuentaInicial
	 * 
	 * @param listaCuentaInicial
	 * Variable a asignar en  listaCuentaInicial
	 */
	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}
	/**
	 * Retorna la lista listaCuentaFinal
	 * 
	 * @return listaCuentaFinal
	 */
	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}
	/**
	 * Asigna la lista listaCuentaFinal
	 * 
	 * @param listaCuentaFinal
	 * Variable a asignar en  listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
	/**
	 * Retorna la lista listaTerceroInicial
	 * 
	 * @return listaTerceroInicial
	 */
	public RegistroDataModelImpl getListaTerceroInicial() {
		return listaTerceroInicial;
	}
	/**
	 * Asigna la lista listaTerceroInicial
	 * 
	 * @param listaTerceroInicial
	 * Variable a asignar en  listaTerceroInicial
	 */
	public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
		this.listaTerceroInicial = listaTerceroInicial;
	}
	/**
	 * Retorna la lista listaTerceroFinal
	 * 
	 * @return listaTerceroFinal
	 */
	public RegistroDataModelImpl getListaTerceroFinal() {
		return listaTerceroFinal;
	}
	/**
	 * Asigna la lista listaTerceroFinal
	 * 
	 * @param listaTerceroFinal
	 * Variable a asignar en  listaTerceroFinal
	 */
	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
