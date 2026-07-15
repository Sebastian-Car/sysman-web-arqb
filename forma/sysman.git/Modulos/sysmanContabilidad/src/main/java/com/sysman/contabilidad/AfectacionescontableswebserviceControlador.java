/*-
 * AfectacionescontableswebserviceControlador.java
 *
 * 1.0
 * 
 * 06/03/2023
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
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
 * @version 1.0, 06/03/2023
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class AfectacionescontableswebserviceControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	
	private String modulo;
	
	private String ano;
	
	private Date fechaFinal;
	
	private Date fechaInicial;
	//<DECLARAR_ATRIBUTOS>
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
	 * Crea una nueva instancia de AfectacionescontableswebserviceControlador
	 */
	public AfectacionescontableswebserviceControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getCompania();
		ano = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)); 
		try {
			//2399
			numFormulario = GeneralCodigoFormaEnum.AFECTACIONESCONTABLESWEBSERVICE.getCodigo();
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
	public void inicializar() {
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
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

	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton generar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirgenerar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}
	public void generarInforme(ReportesBean.FORMATOS formato) {

        archivoDescarga = null;
        String informe = "800508AfectacionesContablesWebService";
        String nombreArchivo = "AFECTACION_CONTABLE_"+ano+"_"+SysmanFunciones.mes(fechaInicial)+"_"+SysmanFunciones.dia(fechaInicial);
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("usuario", "webservice");
            reemplazar.put("ano", Integer.parseInt(ano));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            reemplazar.put("fechaini",sdf.format(fechaInicial));
            reemplazar.put("fechafin",sdf.format(fechaFinal));

            Map<String, Object> parametros = new HashMap<>();
            
            String strSql = Reporteador.resuelveConsulta(informe,
                    Integer.parseInt(SessionUtil.getModulo()), reemplazar);
            
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                    ConectorPool.ESQUEMA_SYSMAN,
                    FORMATOS.EXCEL, nombreArchivo);

        }
        catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
}
