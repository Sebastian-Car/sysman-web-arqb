/*-
 * FrmConfigRegimenesControlador.java
 *
 * 1.0
 * 
 * 04/08/2025
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.beanbase.BeanBaseContinuoNAcme;
import com.sysman.contabilidad.enums.FrmConfigRegimenesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.RetencionsControlador;
import com.sysman.general.enums.CambiosdenitsControladorEnum;
import com.sysman.general.enums.CambiosdenitsControladorUrlEnum;
import com.sysman.general.enums.CompaniasControladorUrlEnum;
import com.sysman.general.enums.RetencionsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModel;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 04/08/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmConfigRegimenesControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private long consecutivo;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	private List<Registro> listaAno;

	private List<Registro> listaregimenComprador;

	private List<Registro> listaregimenVendedor;

	private List<Registro> listatipoRetencion;

	@EJB
	private EjbSysmanUtil ejbSysmanUtil;

	private String regimen;

	private String anoseleccionado;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmConfigRegimenesControlador
	 */
	public FrmConfigRegimenesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_CONFIG_REGIMENES_CONTROLADOR.getCodigo();
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
		enumBase = GenericUrlEnum.CONFIGURACION_RETENCION;
		registro = new Registro();
		registro.getCampos().put("ANO", String.valueOf(SysmanFunciones.ano(new Date())));
		anoseleccionado = String.valueOf(SysmanFunciones.ano(new Date()));
		buscarLlave();
		reasignarOrigen();
		abrirFormulario();
		cargarListaAno();
		cargarListaregimenComprador();
		cargarListaregimenVendedor();
		cargarListatipoRetencion();
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmConfigRegimenesControladorUrlEnum.URL4001.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			Logger.getLogger(FrmConfigRegimenesControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaregimenComprador
	 *
	 */
	public void cargarListaregimenComprador() {
		try {
			listaregimenComprador = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmConfigRegimenesControladorUrlEnum.URL22001.getValue())
											.getUrl(),
									null));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaregimenVendedor
	 *
	 */
	public void cargarListaregimenVendedor() {
		try {
			listaregimenVendedor = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmConfigRegimenesControladorUrlEnum.URL22001.getValue())
											.getUrl(),
									null));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listatipoRetencion
	 *
	 */
	public void cargarListatipoRetencion() {
		try {
			listatipoRetencion = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmConfigRegimenesControladorUrlEnum.URL8013.getValue())
											.getUrl(),
									null));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		anoseleccionado = SysmanFunciones.toString(registro.getCampos().get("ANO"));
		reasignarOrigen();
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {

	}

//<SET_GET_ATRIBUTOS>
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	public long generaConsecutivo() {
		try {

			String condicion = SysmanFunciones.concatenar("COMPANIA = ''", compania, "'' AND ANO = ",
					registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString(),
					" AND REGIMEN_COMPRADOR = ''", regimen, "''");
			consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial("CONFIGURACION_RETENCION", condicion,
					"CONSECUTIVO", "1");

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return consecutivo;
	}

	@Override
	public void asignarValoresRegistro() {
		String anoActual = SysmanFunciones.nvlStr(SysmanFunciones.toString(registro.getCampos().get("ANO")), "N");
		if (anoActual.equals("N")) {
			registro.getCampos().put("ANO", anoseleccionado);
		}

		asignarValorDefecto();
	}

	@Override
	public void removerCombos() {
		// Metodo heredado
		registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
		registro.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());

	}

	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	@Override
	public void reasignarOrigen() {
		buscarUrls();
		asignarValorDefecto();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO").toString());
		parametrosListado.put("REGIMEN", regimen);

	}

	private void asignarValorDefecto() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			Registro rsExiste = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmConfigRegimenesControladorUrlEnum.URL59029.getValue())
											.getUrl(),
									param));

			if (rsExiste != null) {
				Object regimenCampo = rsExiste.getCampos().get("REGIMEN");

				regimen = SysmanFunciones.nvlStr(SysmanFunciones.toString(regimenCampo), "");

				if (regimen.isEmpty()) {
					JsfUtil.agregarMensajeError("No se ha configurado un regimen para la compañia actual");
				} else {
					registro.getCampos().put("REGIMEN_COMPRADOR", regimen);
				}
			} else {
				JsfUtil.agregarMensajeError("No se encontró configuración de régimen para la compañía actual.");
			}
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	@Override
	public boolean insertarAntes() {
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), generaConsecutivo());
		return true;
	}

	@Override
	public boolean insertarDespues() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().remove("NOMBRE_VENDEDOR");
		registro.getCampos().remove("NOMBRE_COMPRADOR");
		registro.getCampos().remove("NOMBRE_RETENCION");
		return true;
	}

	@Override
	public boolean actualizarDespues() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// TODO Auto-generated method stub
		return true;
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
	 * Retorna la lista listaregimenComprador
	 * 
	 * @return listaregimenComprador
	 */
	public List<Registro> getListaregimenComprador() {
		return listaregimenComprador;
	}

	/**
	 * Asigna la lista listaregimenComprador
	 * 
	 * @param listaregimenComprador Variable a asignar en listaregimenComprador
	 */
	public void setListaregimenComprador(List<Registro> listaregimenComprador) {
		this.listaregimenComprador = listaregimenComprador;
	}

	/**
	 * Retorna la lista listaregimenVendedor
	 * 
	 * @return listaregimenVendedor
	 */
	public List<Registro> getListaregimenVendedor() {
		return listaregimenVendedor;
	}

	/**
	 * Asigna la lista listaregimenVendedor
	 * 
	 * @param listaregimenVendedor Variable a asignar en listaregimenVendedor
	 */
	public void setListaregimenVendedor(List<Registro> listaregimenVendedor) {
		this.listaregimenVendedor = listaregimenVendedor;
	}

	/**
	 * Retorna la lista listatipoRetencion
	 * 
	 * @return listatipoRetencion
	 */
	public List<Registro> getListatipoRetencion() {
		return listatipoRetencion;
	}

	/**
	 * Asigna la lista listatipoRetencion
	 * 
	 * @param listatipoRetencion Variable a asignar en listatipoRetencion
	 */
	public void setListatipoRetencion(List<Registro> listatipoRetencion) {
		this.listatipoRetencion = listatipoRetencion;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
