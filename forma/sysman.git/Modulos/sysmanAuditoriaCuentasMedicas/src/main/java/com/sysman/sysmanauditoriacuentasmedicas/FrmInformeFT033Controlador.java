/*-
 * FrmInformeFT033Controlador.java
 *
 * 1.0
 * 
 * 23/04/2026
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
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;

import com.sysman.auditoriacuentasmedicas.ejb.EjbAuditoriaCuentasMedicasCeroLocal;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmImportarRipsControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/04/2026
 * @author LinaPaolaVegaAvella
 */
@ManagedBean
@ViewScoped
public class FrmInformeFT033Controlador extends BeanBaseModal {
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
	private String claseCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String claseCuentaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFinal;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaClaseCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaClaseCuentaFinal;

	private StreamedContent archivoDescarga;

	@EJB
	private EjbAuditoriaCuentasMedicasCeroLocal ejbAuditoriaCuentasMedicasCero;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmInformeFT033Controlador
	 */
	public FrmInformeFT033Controlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_FT030.getCodigo();
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
		cargarListaClaseCuentaInicial();
		cargarListaClaseCuentaFinal();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();

		try {
			setFechaInicial(SysmanFunciones.primeroDeMesFecha(new Date()));
			setFechaFinal(SysmanFunciones.ultimoDiaDate(new Date()));
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
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
	/**
	 * 
	 * Carga la lista listaClaseCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaClaseCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmImportarRipsControladorUrlEnum.URL4395.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaClaseCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaClaseCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaClaseCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmImportarRipsControladorUrlEnum.URL4395.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaClaseCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
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
		    ArrayList<String> informes = new ArrayList<String>();
		    try {
		        Map<String, Object> reemplazos = new TreeMap<>();
		        reemplazos.put("compania",     compania);
		        reemplazos.put("claseInicial", claseCuentaInicial);
		        reemplazos.put("claseFinal",   claseCuentaFinal);
		        reemplazos.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
		        reemplazos.put("fechaFinal",   SysmanFunciones.convertirAFechaCadena(fechaFinal));

		        informes.add(Reporteador.resuelveConsulta(
		            "800582InformeCarteraFT033",
		            Integer.parseInt(SessionUtil.getModulo()),
		            reemplazos));


		        archivoDescarga = JsfUtil.exportarHojaDatosStreamed(
		            informes.get(0),
		            ConectorPool.ESQUEMA_SYSMAN,
		            ReportesBean.FORMATOS.EXCEL,
		            "InformeNormativoFT033");

		    } catch (Exception e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError("Error al generar el informe: " + e.getMessage());
			}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Txt en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirTxt() {
		// <CODIGO_DESARROLLADO>
		generarInforme(FORMATOS.TXT);
		// </CODIGO_DESARROLLADO>
	}

	public void generarInforme(FORMATOS formato) {
		try {
			Map<String, Object> param = new TreeMap<>();

			if (FORMATOS.TXT.equals(formato)) {

				String contenido = ejbAuditoriaCuentasMedicasCero.informeFT_033_plano(compania, fechaInicial,
						fechaFinal, claseCuentaInicial, claseCuentaFinal);

				if (contenido == null || contenido.isEmpty()) {
					JsfUtil.agregarMensajeError("No se encontraron registros para el periodo seleccionado.");
					return;
				}

				// GENERAR Y DESCARGAR EL ARCHIVO TXT
				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(contenido);

				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "InformeNormativoFT033" + ".txt");

			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError("Error al generar el informe: " + e.getMessage());
		}
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control FechaInicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarFechaInicial() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaClaseCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaClaseCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		claseCuentaInicial = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaClaseCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaClaseCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		claseCuentaFinal = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
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
	 * Retorna la variable claseCuentaFinal
	 * 
	 * @return claseCuentaFinal
	 */
	public String getClaseCuentaFinal() {
		return claseCuentaFinal;
	}

	/**
	 * Asigna la variable claseCuentaFinal
	 * 
	 * @param claseCuentaFinal Variable a asignar en claseCuentaFinal
	 */
	public void setClaseCuentaFinal(String claseCuentaFinal) {
		this.claseCuentaFinal = claseCuentaFinal;
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
	 * @param date Variable a asignar en fechaInicial
	 */
	public void setFechaInicial(Date date) {
		this.fechaInicial = date;
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
	 * @param date Variable a asignar en fechaFinal
	 */
	public void setFechaFinal(Date date) {
		this.fechaFinal = date;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
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
	 * @param listaClaseCuentaInicial Variable a asignar en listaClaseCuentaInicial
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
	 * @param listaClaseCuentaFinal Variable a asignar en listaClaseCuentaFinal
	 */
	public void setListaClaseCuentaFinal(RegistroDataModelImpl listaClaseCuentaFinal) {
		this.listaClaseCuentaFinal = listaClaseCuentaFinal;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

//</SET_GET_LISTAS_COMBO_GRANDE>
}
