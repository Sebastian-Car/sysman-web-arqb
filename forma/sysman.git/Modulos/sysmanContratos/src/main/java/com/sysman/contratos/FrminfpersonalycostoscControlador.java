/*-
 * FrminfpersonalycostoscControlador.java
 *
 * 1.0
 * 
 * 26/12/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 26/12/2024
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrminfpersonalycostoscControlador extends BeanBaseModal {
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
	private Date fechaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFinal;
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
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminfpersonalycostoscControlador
	 */
	public FrminfpersonalycostoscControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.INFORMEDECONTRATOS_CONTROLADOR
                    .getCodigo();
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
	public void inicializar() 
	{
		abrirFormulario();
        fechaInicial = new Date();
        fechaFinal = new Date();
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
	
	public boolean validarFechas(Date fechaInicial, Date fechaFinal)
    {
        return fechaInicial.compareTo(fechaFinal) <= 0;
    }
	
	private void generarReporte(FORMATOS formato)
    {
		try
        {        	
        	String reporte = "800684InformePersonalYCostosContratos";
			HashMap<String, Object> reemplazar = new HashMap<>();   
            reemplazar.put("compania", compania);
            reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()), reemplazar);
            
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL,
    					reporte);
    		
        }        
        catch (SQLException | DRException | SysmanException | ParseException | JRException | IOException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
        } 
    }

//<METODOS_CARGAR_LISTA>
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
		if (fechaInicial != null && fechaFinal != null)
        {
            if (!validarFechas(fechaInicial, fechaFinal))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3371"));
                return;
            }
            generarReporte(ReportesBean.FORMATOS.PDF);
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
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
		if (fechaInicial != null && fechaFinal != null)
        {
            if (!validarFechas(fechaInicial, fechaFinal))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3371"));
                return;
            }
            generarReporte(FORMATOS.EXCEL);
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
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
//</SET_GET_LISTAS_COMBO_GRANDE>
}
