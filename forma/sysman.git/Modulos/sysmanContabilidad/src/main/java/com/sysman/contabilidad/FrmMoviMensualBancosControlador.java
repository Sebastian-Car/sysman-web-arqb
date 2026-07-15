/*-
 * FrmMoviMensualBancosControlador.java
 *
 * 1.0
 * 
 * 28/01/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrmMoviMensualBancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 28/01/2023
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmMoviMensualBancosControlador extends BeanBaseModal {
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
	private boolean cuentasSinSaldo;
	
	/**
     * impresion especial excel
     */   
    private boolean reporteExcel;
    
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
	private String nombreMes;
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
	private List<Registro> listaAno;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listames;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmMoviMensualBancosControlador
	 */
	public FrmMoviMensualBancosControlador() {
		super();
		compania = SessionUtil.getCompania();
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
		try {
			numFormulario = 2395;
			validarPermisos();
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
		cargarListaAno();
		cargarListames();
		nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
		                                                       .parseInt(mes)];
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() 
	{
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAno() 
	{
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmMoviMensualBancosControladorUrlEnum.URL4391
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}

	/**
	 * 
	 * Carga la lista listames
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListames() 
	{
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmMoviMensualBancosControladorUrlEnum.URL3960
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton imprimirPdf en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirimprimirPdf() {
		archivoDescarga = null;
		reporteExcel = false;
		generaReporte(FORMATOS.PDF);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton imprimirExcel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirimprimirExcel() 
	{
		archivoDescarga = null;
		reporteExcel = true;
		generaReporte(FORMATOS.EXCEL97);
	}
	
	private void generaReporte(FORMATOS formato) 
	{
		String reporte = "002432InfMovimientoMensualBancos";
	     
        try {
     
            HashMap<String, Object> reemplazar = new HashMap<>(); 
            reemplazar.put("mes", mes);
            reemplazar.put("anio", anio);
            reemplazar.put("compania", compania);
            if(cuentasSinSaldo)
            	reemplazar.put("cuentasSinSaldo", -1);
            else
            	reemplazar.put("cuentasSinSaldo", 0);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            parametros.put("PR_ANO", anio);
            parametros.put("PR_MES", nombreMes.toUpperCase());
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", reporteExcel);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException ex) 
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(RelacionPagoDescuentosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
	}

	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAno() 
	{
		cargarListames();
	}

	/**
	 * Metodo ejecutado al cambiar el control mes
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarmes() 
	{
		nombreMes = null;
        if (!SysmanFunciones.validarVariableVacio(mes)) 
        {
            nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mes)];
        }
	}
	
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

	/**
	 * Retorna la lista listames
	 * 
	 * @return listames
	 */
	public List<Registro> getListames() {
		return listames;
	}

	/**
	 * Asigna la lista listames
	 * 
	 * @param listames Variable a asignar en listames
	 */
	public void setListames(List<Registro> listames) {
		this.listames = listames;
	}

	/**
	 * @return the cuentasSinSaldo
	 */
	public boolean isCuentasSinSaldo() {
		return cuentasSinSaldo;
	}

	/**
	 * @param cuentasSinSaldo the cuentasSinSaldo to set
	 */
	public void setCuentasSinSaldo(boolean cuentasSinSaldo) {
		this.cuentasSinSaldo = cuentasSinSaldo;
	}
}
