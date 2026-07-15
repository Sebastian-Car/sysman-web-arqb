/*-
 * FrinformeretencionespracticadasControlador.java
 *
 * 1.0
 * 
 * 20/04/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrinformeretencionespracticadasUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 20/04/2023
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrinformeretencionespracticadasControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	
	private final Date fechaActual;
	
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anio;
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
	private Date fechaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFinal;
	
	private boolean ckActEcca;
	private boolean ckReteicaMunicipal;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listacbAno;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacbCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacbCuentaFinal;
	
	private StreamedContent archivoDescarga;
	
	@EJB
    private EjbSysmanUtilRemote sysmanUtil;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrinformeretencionespracticadasControlador
	 */
	public FrinformeretencionespracticadasControlador() {
		super();
		compania = SessionUtil.getCompania();
		fechaActual = new Date();
		try {
			numFormulario = 2402;
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
		fechaInicial = fechaFinal = fechaActual;
		anio = String.valueOf(SysmanFunciones.ano(fechaActual));
//<CARGAR_LISTA>
		cargarListacbAno();
		cargarListacbCuentaInicial();
		cargarListacbCuentaFinal();
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
	/**
	 * 
	 * Carga la lista listacbAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacbAno() 
	{
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listacbAno = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrinformeretencionespracticadasUrlEnum.URL0001.getValue())
					.getUrl(),
					param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listacbCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacbCuentaInicial() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrinformeretencionespracticadasUrlEnum.URL0002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listacbCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listacbCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacbCuentaFinal() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrinformeretencionespracticadasUrlEnum.URL0003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put("CODIGOINICIAL", cuentaInicial);

		listacbCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
	
	public void cambiarcbAno() 
	{
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	}
	
	public void cambiarcbCuentaInicial() 
	{
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cargarListacbCuentaInicial();
	}
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton imprimirExcel en la vista
	 *
	 *
	 */
	public void oprimirimprimirExcel() 
	{
		setArchivoDescarga(null);
		String reporte = "800562InformeRetencionesPracticadas";
		HashMap<String, Object> reemplazar = new HashMap<>();
		reemplazar.put("compania", anio == null ? "" : compania);
		reemplazar.put("ano", anio == null ? "" : anio);
		reemplazar.put("codigoInicial", cuentaInicial == null ? "" : cuentaInicial);
		reemplazar.put("codigoFinal", cuentaFinal == null ? "" : cuentaFinal);
		reemplazar.put("fechaInicial", fechaInicial == null ? "" : SysmanFunciones.formatearFecha(fechaInicial));
		reemplazar.put("fechaFinal", fechaFinal == null ? "" : SysmanFunciones.formatearFecha(fechaFinal));
		
		try {
			boolean manejaEquivalenteRete = "SI".equals(SysmanFunciones
 					.nvl(sysmanUtil.consultarParametro(compania, "MANEJA EQUIVALENTE DE RETENCIONES",
 							SessionUtil.getModulo(), new Date(), true), "NO"));
		    if(manejaEquivalenteRete)
		    {
		    	reporte = "800593InformeRetencionesPracticadasConEquivalente";
		    }else if(ckActEcca) {
		    	reporte = "800683InformeRetencionesPracticadasActEcca";		    	
		    }else if(ckReteicaMunicipal) {
		    	reporte = "800711InformeRetencionesPracticadasReteicaMpal";	
		    }
			String sql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);
			
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL,
					reporte);		

	} catch (JRException | IOException | SQLException | DRException | SysmanException | SystemException  e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}  		
	}
	
	public void cambiarckActEcca() {
		// <CODIGO_DESARROLLADO>
		ckReteicaMunicipal = false;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control ckReteicaMunicipal
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarckReteicaMunicipal() {
		// <CODIGO_DESARROLLADO>
		ckActEcca = false;
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	public void seleccionarFilacbCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListacbCuentaFinal();
	}

	public void seleccionarFilacbCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
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

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listacbAno
	 * 
	 * @return listacbAno
	 */
	public List<Registro> getListacbAno() {
		return listacbAno;
	}

	/**
	 * Asigna la lista listacbAno
	 * 
	 * @param listacbAno Variable a asignar en listacbAno
	 */
	public void setListacbAno(List<Registro> listacbAno) {
		this.listacbAno = listacbAno;
	}

	/**
	 * Retorna la lista listacbCuentaInicial
	 * 
	 * @return listacbCuentaInicial
	 */
	public RegistroDataModelImpl getListacbCuentaInicial() {
		return listacbCuentaInicial;
	}

	/**
	 * Asigna la lista listacbCuentaInicial
	 * 
	 * @param listacbCuentaInicial Variable a asignar en listacbCuentaInicial
	 */
	public void setListacbCuentaInicial(RegistroDataModelImpl listacbCuentaInicial) {
		this.listacbCuentaInicial = listacbCuentaInicial;
	}

	/**
	 * Retorna la lista listacbCuentaFinal
	 * 
	 * @return listacbCuentaFinal
	 */
	public RegistroDataModelImpl getListacbCuentaFinal() {
		return listacbCuentaFinal;
	}

	/**
	 * Asigna la lista listacbCuentaFinal
	 * 
	 * @param listacbCuentaFinal Variable a asignar en listacbCuentaFinal
	 */
	public void setListacbCuentaFinal(RegistroDataModelImpl listacbCuentaFinal) {
		this.listacbCuentaFinal = listacbCuentaFinal;
	}
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * @return the ckActEcca
	 */
	public boolean isCkActEcca() {
		return ckActEcca;
	}

	/**
	 * @param ckActEcca the ckActEcca to set
	 */
	public void setCkActEcca(boolean ckActEcca) {
		this.ckActEcca = ckActEcca;

	}

	public boolean isCkReteicaMunicipal() {
		return ckReteicaMunicipal;
		
	}

	public void setCkReteicaMunicipal(boolean ckReteicaMunicipal) {
		this.ckReteicaMunicipal = ckReteicaMunicipal;		

	}
}
