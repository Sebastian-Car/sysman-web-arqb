/*-
 * LispjBalancexProcesoControlador.java
 *
 * 1.0
 * 
 * 23/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.sysman.contabilidad.enums.LispjBalancexProcesoControladorEnum;
import com.sysman.contabilidad.enums.LispjBalancexProcesoControladorUrlEnum;
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
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/05/2024
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class LispjBalancexProcesoControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	
	private final Date fechaActual;
	
	private String anio;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String procesoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String procesoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cuentaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String terceroInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String terceroFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreTerceroIni;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreTerceroFin;
	
	private boolean verTercero;
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
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCuentaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTerceroInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTerceroFinal;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de LispjBalancexProcesoControlador
	 */
	public LispjBalancexProcesoControlador() {
		super();
		compania = SessionUtil.getCompania();
		fechaActual = new Date();
		try {
			numFormulario = GeneralCodigoFormaEnum.LIS_PJ_BALANCE_X_PROCESO_CONTROLADOR
                    .getCodigo();
			validarPermisos();
			cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
            cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
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
		fechaInicial = fechaFinal = fechaActual;
		anio = String.valueOf(SysmanFunciones.ano(fechaActual));
		cargarListaCuentaInicial();
		cargarListaTerceroInicial();

		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {		
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCuentaInicial() {	
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LispjBalancexProcesoControladorUrlEnum.URL0001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());	
	}

	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCuentaFinal() {
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LispjBalancexProcesoControladorUrlEnum.URL0002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(LispjBalancexProcesoControladorEnum.PARAM3.getValue(), cuentaInicial);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaTerceroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTerceroInicial() {		
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		LispjBalancexProcesoControladorUrlEnum.URL0003
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, LispjBalancexProcesoControladorEnum.PARAM2.getValue());
	}

	/**
	 * 
	 * Carga la lista listaTerceroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTerceroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		LispjBalancexProcesoControladorUrlEnum.URL0004
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(LispjBalancexProcesoControladorEnum.PARAM1.getValue(),
		                terceroInicial);
		
		listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, LispjBalancexProcesoControladorEnum.PARAM2.getValue());
	}
	
	public void cambiarfiltroTercero() 
	{
		terceroInicial = "0";
		terceroFinal = SysmanConstantes.CONS_TERCERO;
        cargarListaTerceroInicial();
	}
	
	/**
     * Metodo ejecutado al cambiar el control fechaInicial
     *
     */
    public void cambiarfechainicial() {
    	if(validarAniosFechas()) {
	        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
	        cargarListaCuentaInicial();
        }
    }
    
    /**
     * Metodo ejecutado al cambiar el control fecha
     *
     */
    public void cambiarfechafinal() {
        if(validarAniosFechas()) {
	        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
	        cargarListaCuentaInicial();
        }
    }
	
	private boolean validarAniosFechas() {
       
		if (SysmanFunciones.ano(fechaInicial) != SysmanFunciones.ano(fechaFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3154"));
            return false;
        }        
        return true;
    }

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		archivoDescarga = null;
		generaInforme(FORMATOS.EXCEL);
	}

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
		generaInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}
	
	/**
     * metodo que contiene la logica para imprimir un reporte en
     * formato pdf y excel
     * 
     * @param formato
     */
    private void generaInforme(FORMATOS formato) {

        try {
        	if(validarAniosFechas()) 
        	{
	        	String formatoReporte = "002585BALANCEPORPROCESO_CREMIL";
	        	Map<String, Object> parametros = new HashMap<>();
	            HashMap<String, Object> reemplazar = new HashMap<>();
	
	            reemplazar.put("compania", compania);
	            reemplazar.put("cuentaInicial", cuentaInicial);
	            reemplazar.put("cuentaFinal", cuentaFinal);
	            reemplazar.put("fechaInicial",
	                            SysmanFunciones.formatearFecha(fechaInicial));
	            reemplazar.put("fechaFinal",
	                            SysmanFunciones.formatearFecha(fechaFinal));            
	            if(!verTercero) {
	            	terceroInicial = "0";
	        		terceroFinal = SysmanConstantes.CONS_TERCERO;
	            }
	            reemplazar.put("terceroInicial", terceroInicial);
	            reemplazar.put("terceroFinal", terceroFinal);
	            
	            reemplazar.put("terceroCond",
	                    verTercero
	                        ? " AND PROCESOS_JUDICIALES.TERCERO BETWEEN '"
	                            + terceroInicial + "' "
	                            + "AND '" + terceroFinal + "'"
	                        : " ");
	            
	            Reporteador.resuelveConsulta(formatoReporte,
	                            Integer.parseInt(SessionUtil.getModulo()),
	                            reemplazar, parametros);
	            String titulo = "BALANCE POR PROCESO";
	            
	            parametros.put("PR_TITULO_INFORME", titulo);
	            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
	            parametros.put("PR_CUENTAFINAL", cuentaFinal);
	            parametros.put("PR_NOMBRE_CONTADOR", 
	            		ejbSysmanUtil.consultarParametro(compania,"NOMBRE JEFE DE CONTABILIDAD", 
	            				SessionUtil.getModulo(), new Date(), true));
				parametros.put("PR_CARGO_CONTADOR",
						ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE DE CONTABILIDAD", 
								SessionUtil.getModulo(), new Date(), true));
				
	            archivoDescarga = JsfUtil.exportarStreamed(formatoReporte,
	                            parametros,
	                            ConectorPool.ESQUEMA_SYSMAN,
	                            formato);
        	}
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_INFORME_NO_EXISTE"));            
        }
        catch (JRException | IOException | SysmanException |SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } 
    }

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
/**

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();		
        cuentaFinal = null;
        cargarListaCuentaFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTerceroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroInicial = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()));
		nombreTerceroIni = SysmanFunciones.nvl(registroAux.getCampos().get(
                GeneralParameterEnum.NOMBRE.getName()),"")
                .toString();
		terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		nombreTerceroFin = null;
		cargarListaTerceroFinal();		
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTerceroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroFinal = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()));
		nombreTerceroFin = SysmanFunciones.nvl(registroAux.getCampos().get(
                GeneralParameterEnum.NOMBRE.getName()),"")
                .toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable procesoInicial
	 * 
	 * @return procesoInicial
	 */
	public String getProcesoInicial() {
		return procesoInicial;
	}

	/**
	 * Asigna la variable procesoInicial
	 * 
	 * @param procesoInicial Variable a asignar en procesoInicial
	 */
	public void setProcesoInicial(String procesoInicial) {
		this.procesoInicial = procesoInicial;
	}

	/**
	 * Retorna la variable procesoFinal
	 * 
	 * @return procesoFinal
	 */
	public String getProcesoFinal() {
		return procesoFinal;
	}

	/**
	 * Asigna la variable procesoFinal
	 * 
	 * @param procesoFinal Variable a asignar en procesoFinal
	 */
	public void setProcesoFinal(String procesoFinal) {
		this.procesoFinal = procesoFinal;
	}

	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}

	/**
	 * Asigna la variable cuentaInicial
	 * 
	 * @param cuentaInicial Variable a asignar en cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}

	/**
	 * Asigna la variable cuentaFinal
	 * 
	 * @param cuentaFinal Variable a asignar en cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	/**
	 * Retorna la variable terceroInicial
	 * 
	 * @return terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}

	/**
	 * Asigna la variable terceroInicial
	 * 
	 * @param terceroInicial Variable a asignar en terceroInicial
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	/**
	 * Retorna la variable terceroFinal
	 * 
	 * @return terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}

	/**
	 * Asigna la variable terceroFinal
	 * 
	 * @param terceroFinal Variable a asignar en terceroFinal
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * Asigna la variable fechaInicial
	 * 
	 * @param fechaInicial Variable a asignar en fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * Asigna la variable fechaFinal
	 * 
	 * @param fechaFinal Variable a asignar en fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * Retorna la variable nombreTerceroIni
	 * 
	 * @return nombreTerceroIni
	 */
	public String getNombreTerceroIni() {
		return nombreTerceroIni;
	}

	/**
	 * Asigna la variable nombreTerceroIni
	 * 
	 * @param nombreTerceroIni Variable a asignar en nombreTerceroIni
	 */
	public void setNombreTerceroIni(String nombreTerceroIni) {
		this.nombreTerceroIni = nombreTerceroIni;
	}

	/**
	 * Retorna la variable nombreTerceroFin
	 * 
	 * @return nombreTerceroFin
	 */
	public String getNombreTerceroFin() {
		return nombreTerceroFin;
	}

	/**
	 * Asigna la variable nombreTerceroFin
	 * 
	 * @param nombreTerceroFin Variable a asignar en nombreTerceroFin
	 */
	public void setNombreTerceroFin(String nombreTerceroFin) {
		this.nombreTerceroFin = nombreTerceroFin;
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
	 * @param listaCuentaInicial Variable a asignar en listaCuentaInicial
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
	 * @param listaCuentaFinal Variable a asignar en listaCuentaFinal
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
	 * @param listaTerceroInicial Variable a asignar en listaTerceroInicial
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
	 * @param listaTerceroFinal Variable a asignar en listaTerceroFinal
	 */
	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the verTercero
	 */
	public boolean isVerTercero() {
		return verTercero;
	}

	/**
	 * @param verTercero the verTercero to set
	 */
	public void setVerTercero(boolean verTercero) {
		this.verTercero = verTercero;
	}
}
