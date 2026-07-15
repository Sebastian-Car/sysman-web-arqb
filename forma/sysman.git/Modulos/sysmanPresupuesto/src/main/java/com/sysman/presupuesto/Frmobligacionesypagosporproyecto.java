package com.sysman.presupuesto;

/*-
 * Frmobligacionesypagosporproyecto.java
 *
 * 1.0
 * 
 * 21/07/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
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
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucionGastosCaqControladorEnum;
import com.sysman.presupuesto.enums.FrmobligacionesypagosporproyectoEnum;
import com.sysman.presupuesto.enums.FrmobligacionesypagosporproyectoUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;

/**
 * Formulario que permite generar el informe de los registros, obligaciones y
 * los pagos por proyecto realizados en el módulo de presupuesto.
 * 
 * @version 1.0, 21/07/2023
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class Frmobligacionesypagosporproyecto extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */

	private final String modulo;
	private final String compania;
//<DECLARAR_ATRIBUTOS>

	private int ano;
	private String proyectoInicial;
	private String proyectoFinal;
	private String mesInicial;
	private String mesFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	private List<Registro> listaAno;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaProyectoInicial;
	private RegistroDataModelImpl listaProyectoFinal;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de Frmobligacionesypagosporproyecto
	 */
	public Frmobligacionesypagosporproyecto() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();

		try {
			// 2419
			numFormulario = GeneralCodigoFormaEnum.FRM_REGISTROOBLIGACIONESYPAGOSPORPROYECTO_CONTROLADOR.getCodigo();

			validarPermisos();
			// <INI_ADICIONAL>
			ano = SysmanFunciones.ano(new Date());
			mesInicial = "1";
			mesFinal = String.valueOf(SysmanFunciones.mes(new Date()));
			/*
			 * mesFinal = String .valueOf(SysmanFunciones.getParteFecha( new Date(),
			 * Calendar.MONTH) + 1);
			 */
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(FrmobligacionesypagosControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
//<CARGAR_LISTA>
		cargarListaAno();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaProyectoInicial();
		cargarListaProyectoFinal();
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
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * 
	 */
	public void cargarListaAno() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmobligacionesypagosporproyectoUrlEnum.URL3752.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaProyectoInicial
	 *
	 * 
	 */
	public void cargarListaProyectoInicial() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmobligacionesypagosporproyectoUrlEnum.URL707.getValue());
		listaProyectoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaProyectoFinal
	 *
	 * 
	 */
	public void cargarListaProyectoFinal() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(FrmobligacionesypagosporproyectoEnum.REFERENCIAINICIAL.getValue(), proyectoInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmobligacionesypagosporproyectoUrlEnum.URL732.getValue());
		listaProyectoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * 
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		obtenerReporte(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
		
	}

	public void obtenerReporte(ReportesBean.FORMATOS formato) {
		
		HashMap<String, Object> reemplazar = new HashMap<>();
		archivoDescarga = null;
		String reporte = null;

		try {
			reemplazar.put("proyectoInicial", proyectoInicial);
			reemplazar.put("proyectoFinal", proyectoFinal);
			reemplazar.put("ano", ano);
			reemplazar.put("mesInicial", mesInicial);
			reemplazar.put("mesFinal", mesFinal);
			reemplazar.put("compania", compania);

			// CORPOBOYACA 800544OBLIGACIONESYPAGOS
			reporte = "800544OBLIGACIONESYPAGOS";

			Reporteador.resuelveConsulta(reporte, Integer.valueOf(modulo), reemplazar);

			archivoDescarga = JsfUtil.exportarExcelPlano(reporte, reporte, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
					reemplazar, reemplazar, Integer.valueOf(modulo));

		}catch (JRException | IOException | SysmanException |  SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 *
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Mes
	 * 
	 * 
	 * 
	 */
	public void cambiarMes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProyectoInicial
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyectoInicial(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		proyectoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();

		proyectoFinal = null;
		cargarListaProyectoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProyectoFinal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyectoFinal(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		proyectoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano Variable a asignar en ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable proyectoInicial
	 * 
	 * @return proyectoInicial
	 */
	public String getProyectoInicial() {
		return proyectoInicial;
	}

	/**
	 * Asigna la variable proyectoInicial
	 * 
	 * @param proyectoInicial Variable a asignar en proyectoInicial
	 */
	public void setProyectoInicial(String proyectoInicial) {
		this.proyectoInicial = proyectoInicial;
	}

	/**
	 * Retorna la variable proyectoFinal
	 * 
	 * @return proyectoFinal
	 */
	public String getProyectoFinal() {
		return proyectoFinal;
	}

	/**
	 * Asigna la variable proyectoFinal
	 * 
	 * @param proyectoFinal Variable a asignar en proyectoFinal
	 */
	public void setProyectoFinal(String proyectoFinal) {
		this.proyectoFinal = proyectoFinal;
	}

	/**
	 * Retorna la variable mesInicial
	 * 
	 * @return mesInicial
	 */
	public String getMesInicial() {
		return mesInicial;
	}

	/**
	 * Asigna la variable mesInicial
	 * 
	 * @param mesInicial Variable a asignar en mesInicial
	 */
	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	/**
	 * Retorna la variable mesFinal
	 * 
	 * @return mesFinal
	 */
	public String getMesFinal() {
		return mesFinal;
	}

	/**
	 * Asigna la variable mesFinal
	 * 
	 * @param mesFinal Variable a asignar en mesFinal
	 */
	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
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

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaProyectoInicial
	 * 
	 * @return listaProyectoInicial
	 */
	public RegistroDataModelImpl getListaProyectoInicial() {
		return listaProyectoInicial;
	}

	/**
	 * Asigna la lista listaProyectoInicial
	 * 
	 * @param listaProyectoInicial Variable a asignar en listaProyectoInicial
	 */
	public void setListaProyectoInicial(RegistroDataModelImpl listaProyectoInicial) {
		this.listaProyectoInicial = listaProyectoInicial;
	}

	/**
	 * Retorna la lista listaProyectoFinal
	 * 
	 * @return listaProyectoFinal
	 */
	public RegistroDataModelImpl getListaProyectoFinal() {
		return listaProyectoFinal;
	}

	/**
	 * Asigna la lista listaProyectoFinal
	 * 
	 * @param listaProyectoFinal Variable a asignar en listaProyectoFinal
	 */
	public void setListaProyectoFinal(RegistroDataModelImpl listaProyectoFinal) {
		this.listaProyectoFinal = listaProyectoFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
