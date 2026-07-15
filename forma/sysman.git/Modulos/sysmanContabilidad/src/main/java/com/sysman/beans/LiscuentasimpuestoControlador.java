/*-
 * LiscuentasimpuestoControlador.java
 *
 * 1.0
 * 
 * 15/10/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.beans;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * 
 * Formulario que se usa para la genracion del reporte LisCuentasImpuesto
 *
 * @version 1.0, 15/10/2021
 * @author dramirez
 */
@ManagedBean
@ViewScoped
public class  LiscuentasimpuestoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Variable que almacena la fecha inicial
	 */
	private Date fechaInicial;
	/**
	 *  Variable que almacena la fecha final
	 */
	private Date fechaFinal;
	private StreamedContent archivoDescarga;

	public LiscuentasimpuestoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getCompania();
		fechaInicial = new Date();
		fechaFinal = new Date();
		try {
			//2323
			numFormulario = GeneralCodigoFormaEnum.LISCUENTAS_IMPUESTO_CONTROLADOR
					.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(LiscuentasimpuestoControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
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
		/*
FR2323-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
   formularioAbrir GetIdModulo, Me.Name
   DoCmd.Restore
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir
	 * en la vista
	 *
	 */
	public void oprimirImprimir() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);           
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		 archivoDescarga = null;
	     generaInforme(ReportesBean.FORMATOS.EXCEL97);            
		//</CODIGO_DESARROLLADO>
	}
	 public void generaInforme(ReportesBean.FORMATOS formato) {
	        if (fechaInicial.before(fechaFinal)) {
	            try {
	                Map<String, Object> parametros = new HashMap<>();
	                HashMap<String, Object> reemplazar = new HashMap<>();
	                reemplazar.put("compania", compania);
	                reemplazar.put("fechaInicial",
	                                SysmanFunciones.formatearFecha(fechaInicial));
	                reemplazar.put("fechaFinal",
	                                SysmanFunciones.formatearFecha(fechaFinal));
	                parametros.put("PR_FORMS_LISCUENTASIMPUESTO_FECHAINICIAL", "FECHA INICIAL "
	                    + SysmanFunciones.convertirAFechaCadena(fechaInicial));
	                parametros.put("PR_FORMS_LISCUENTASIMPUESTO_FECHAFINAL", "FECHA FINAL "
	                    + SysmanFunciones.convertirAFechaCadena(fechaFinal));
	                
	                Reporteador.resuelveConsulta("002135LisCuentasImpuesto",
	                                Integer.parseInt(modulo), reemplazar,
	                                parametros);

	                archivoDescarga = JsfUtil.exportarStreamed("002135LisCuentasImpuesto",
	                                parametros, ConectorPool.ESQUEMA_SYSMAN,
	                                formato);
	            }
	            catch (JRException | IOException | SysmanException
	                            | ParseException e) {
	                logger.error(e.getMessage(), e);
	                JsfUtil.agregarMensajeError(e.getMessage());
	            }

	        }
	        else {
	            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB574"));
	        }
	    }
	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	
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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
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
}
