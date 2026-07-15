/*-
 * FrmPrepararDistControlador.java
 *
 * 1.0
 * 
 * 22/08/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.nomina.ejb.EjbNominaDiezRemote;
import com.sysman.nomina.enums.FrmCalculoDistribucionControladorUrlEnum;
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

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 22/08/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class FrmPrepararDistControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	private String mesBase;
	private int anioBase;
	private int mesPreparar;
	private int anioPreparar;
	private String periodoBase;
	private String periodoPreparar;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	private List<Registro> listaMesBase;
	private List<Registro> listaAnoBase;
	private List<Registro> listaMesPreparar;
	private List<Registro> listaAnoPreparar;
	private List<Registro> listaPeriodoBase;
	private List<Registro> listaPeriodoPreparar;
	private String proceso;
	private String anio;
	private String mes;
	private String periodo;
	
	@EJB
    private EjbNominaCeroGeneralRemote ejbNominaCero;
	
	@EJB
    private EjbNominaDiezRemote ejbNominaDiez;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmPrepararDistControlador
	 */
	public FrmPrepararDistControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 2483;
			validarPermisos();

			proceso = (String) SessionUtil.getSessionVar("procesoNomina");
			anio = (String) SessionUtil.getSessionVar("anioNomina");
			mes = (String) SessionUtil.getSessionVar("mesNomina");
			periodo = (String) SessionUtil.getSessionVar("periodoNomina");
			
			cargarValores();	
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
		cargarListaMesBase();
		cargarListaAnoBase();
		cargarListaMesPreparar();
		cargarListaAnoPreparar();
		cargarListaPeriodoBase();
		cargarListaPeriodoPreparar();
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
		/*
		 * FR2483-AL_ABRIR Private Sub Form_Open(Cancel As Integer) PerPre log
		 * "IngresÃ³ a Procesos, Prestamos Diferidos, Preparar Diferidos" End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaMesBase
	 *
	 */
	public void cargarListaMesBase() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ID_PROCESO", proceso);
		param.put("ANO", anioBase);

		try {
			listaMesBase = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCalculoDistribucionControladorUrlEnum.URL4541.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaAnoBase
	 *
	 */
	public void cargarListaAnoBase() {
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("IDPROCESO", proceso);

		try {
			listaAnoBase = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCalculoDistribucionControladorUrlEnum.URL3927.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMesPreparar
	 *
	 */
	public void cargarListaMesPreparar() {
 
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ID_PROCESO", proceso);
		param.put("ANO", anioPreparar);

		try {
			listaMesPreparar = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCalculoDistribucionControladorUrlEnum.URL4541.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaAnoPreparar
	 *
	 */
	public void cargarListaAnoPreparar() {
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("IDPROCESO", proceso);

		try {
			listaAnoPreparar = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCalculoDistribucionControladorUrlEnum.URL3927.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaPeriodoBase
	 *
	 */
	public void cargarListaPeriodoBase() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ID_DE_PROCESO", proceso);
		param.put("ANO", anioBase);
		param.put("MES", mesBase);

		try {
			listaPeriodoBase = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCalculoDistribucionControladorUrlEnum.URL3930.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaPeriodoPreparar
	 *
	 */
	public void cargarListaPeriodoPreparar() {
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ID_DE_PROCESO", proceso);
		param.put("ANO", anioPreparar);
		param.put("MES", mesPreparar);

		try {
			listaPeriodoPreparar = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCalculoDistribucionControladorUrlEnum.URL3930.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cerrar en la vista
	 *
	 *
	 */
	public void oprimirCerrar() {
		// <CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Preparar en la vista
	 *
	 *
	 */
	public void oprimirPreparar() {
		// <CODIGO_DESARROLLADO>
		try {
			boolean periodoActivo = ejbNominaCero
					.validarPeriodoActivoNomina(compania,
							Integer.parseInt(proceso),
							anioPreparar,
							mesPreparar,
							Integer.parseInt(periodoPreparar));

			if(periodoActivo) {
				int cantidad = ejbNominaDiez.copiarDistribucion(compania, Integer.parseInt(proceso), anioBase, anioPreparar, Integer.parseInt(mesBase), mesPreparar, Integer.parseInt(periodoBase), Integer.parseInt(periodoPreparar));
				String mensaje = null;
				if(cantidad > 0) {	
					mensaje = "Proceso ejecutado exitosamente. \n Se copiaron " + cantidad + " registros para el mes: " + mesPreparar + " año: " + anioPreparar + " periodo: " + periodoPreparar + ".";	
				}else {
					mensaje = "No se encontraron registros para copiar";
				}
				JsfUtil.agregarMensajeInformativo(mensaje);
			}

		 
	 }
    catch ( NumberFormatException | SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);
    }
		
		// </CODIGO_DESARROLLADO>
	}
	
	
	public void cargarValores() {
		
		anioBase = anioPreparar = Integer.parseInt(anio);
		mesBase = mes;
		mesPreparar = Integer.parseInt(mes) + 1;
		periodoBase = periodoPreparar = periodo;
        if (mesBase.equals("12")) {
        	anioPreparar = Integer.parseInt(anio) + 1;
            mesPreparar = 1;
            cargarListaMesBase();
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
	 * Retorna la variable mesBase
	 * 
	 * @return mesBase
	 */
	public String getMesBase() {
		return mesBase;
	}

	/**
	 * Asigna la variable mesBase
	 * 
	 * @param mesBase Variable a asignar en mesBase
	 */
	public void setMesBase(String mesBase) {
		this.mesBase = mesBase;
	}

	/**
	 * Retorna la variable anioBase
	 * 
	 * @return anioBase
	 */
	public int getAnioBase() {
		return anioBase;
	}

	/**
	 * Asigna la variable anioBase
	 * 
	 * @param anioBase Variable a asignar en anioBase
	 */
	public void setAnioBase(int anioBase) {
		this.anioBase = anioBase;
	}

	/**
	 * Retorna la variable mesPreparar
	 * 
	 * @return mesPreparar
	 */
	public int getMesPreparar() {
		return mesPreparar;
	}

	/**
	 * Asigna la variable mesPreparar
	 * 
	 * @param mesPreparar Variable a asignar en mesPreparar
	 */
	public void setMesPreparar(int mesPreparar) {
		this.mesPreparar = mesPreparar;
	}

	/**
	 * Retorna la variable anioPreparar
	 * 
	 * @return anioPreparar
	 */
	public int getAnioPreparar() {
		return anioPreparar;
	}

	/**
	 * Asigna la variable anioPreparar
	 * 
	 * @param anioPreparar Variable a asignar en anioPreparar
	 */
	public void setAnioPreparar(int anioPreparar) {
		this.anioPreparar = anioPreparar;
	}

	/**
	 * Retorna la variable periodoBase
	 * 
	 * @return periodoBase
	 */
	public String getPeriodoBase() {
		return periodoBase;
	}

	/**
	 * Asigna la variable periodoBase
	 * 
	 * @param periodoBase Variable a asignar en periodoBase
	 */
	public void setPeriodoBase(String periodoBase) {
		this.periodoBase = periodoBase;
	}

	/**
	 * Retorna la variable periodoPreparar
	 * 
	 * @return periodoPreparar
	 */
	public String getPeriodoPreparar() {
		return periodoPreparar;
	}

	/**
	 * Asigna la variable periodoPreparar
	 * 
	 * @param periodoPreparar Variable a asignar en periodoPreparar
	 */
	public void setPeriodoPreparar(String periodoPreparar) {
		this.periodoPreparar = periodoPreparar;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaMesBase
	 * 
	 * @return listaMesBase
	 */
	public List<Registro> getListaMesBase() {
		return listaMesBase;
	}

	/**
	 * Asigna la lista listaMesBase
	 * 
	 * @param listaMesBase Variable a asignar en listaMesBase
	 */
	public void setListaMesBase(List<Registro> listaMesBase) {
		this.listaMesBase = listaMesBase;
	}

	/**
	 * Retorna la lista listaAnoBase
	 * 
	 * @return listaAnoBase
	 */
	public List<Registro> getListaAnoBase() {
		return listaAnoBase;
	}

	/**
	 * Asigna la lista listaAnoBase
	 * 
	 * @param listaAnoBase Variable a asignar en listaAnoBase
	 */
	public void setListaAnoBase(List<Registro> listaAnoBase) {
		this.listaAnoBase = listaAnoBase;
	}

	/**
	 * Retorna la lista listaMesPreparar
	 * 
	 * @return listaMesPreparar
	 */
	public List<Registro> getListaMesPreparar() {
		return listaMesPreparar;
	}

	/**
	 * Asigna la lista listaMesPreparar
	 * 
	 * @param listaMesPreparar Variable a asignar en listaMesPreparar
	 */
	public void setListaMesPreparar(List<Registro> listaMesPreparar) {
		this.listaMesPreparar = listaMesPreparar;
	}

	/**
	 * Retorna la lista listaAnoPreparar
	 * 
	 * @return listaAnoPreparar
	 */
	public List<Registro> getListaAnoPreparar() {
		return listaAnoPreparar;
	}

	/**
	 * Asigna la lista listaAnoPreparar
	 * 
	 * @param listaAnoPreparar Variable a asignar en listaAnoPreparar
	 */
	public void setListaAnoPreparar(List<Registro> listaAnoPreparar) {
		this.listaAnoPreparar = listaAnoPreparar;
	}

	/**
	 * Retorna la lista listaPeriodoBase
	 * 
	 * @return listaPeriodoBase
	 */
	public List<Registro> getListaPeriodoBase() {
		return listaPeriodoBase;
	}

	/**
	 * Asigna la lista listaPeriodoBase
	 * 
	 * @param listaPeriodoBase Variable a asignar en listaPeriodoBase
	 */
	public void setListaPeriodoBase(List<Registro> listaPeriodoBase) {
		this.listaPeriodoBase = listaPeriodoBase;
	}

	/**
	 * Retorna la lista listaPeriodoPreparar
	 * 
	 * @return listaPeriodoPreparar
	 */
	public List<Registro> getListaPeriodoPreparar() {
		return listaPeriodoPreparar;
	}

	/**
	 * Asigna la lista listaPeriodoPreparar
	 * 
	 * @param listaPeriodoPreparar Variable a asignar en listaPeriodoPreparar
	 */
	public void setListaPeriodoPreparar(List<Registro> listaPeriodoPreparar) {
		this.listaPeriodoPreparar = listaPeriodoPreparar;
	}
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
