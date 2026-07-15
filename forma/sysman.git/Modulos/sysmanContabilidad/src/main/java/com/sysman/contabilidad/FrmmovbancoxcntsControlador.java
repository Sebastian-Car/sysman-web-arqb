/*-
 * FrmmovbancoxcntsControlador.java
 *
 * 1.0
 * 
 * 25/09/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;

import com.sysman.contabilidad.enums.frmmovbancoxcntsControladorUrlEnum;
import com.sysman.contabilidad.enums.frmmovbancoxcntsControladorEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;


/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 25/09/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class  FrmmovbancoxcntsControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo;
	private final String codigoConst;
	//<DECLARAR_ATRIBUTOS>
	String tipoInicial;
	private String tipoFinal;
	private String cuentaInicial;
	private String cuentaFinal;
	private Date fechaInicial;
	private Date fechaFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>

	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaTipoInicial;
	private RegistroDataModelImpl listaTipoFinal;
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Crea una nueva instancia de FrmmovbancoxcntsControlador
	 */
	public FrmmovbancoxcntsControlador() 
	{
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		codigoConst = "CODIGO";
		try {
			numFormulario = GeneralCodigoFormaEnum.MOVIMIENTOBANCOPORCUENTA
					.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(FrmmovbancoxcntsControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar(){
		abrirFormulario();
		cargarListaTipoInicial();
		cargarListaCuentaInicial();

	}

	@Override
	public void abrirFormulario(){
		fechaInicial = new Date();
		fechaFinal = new Date();
		tipoInicial =  SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaInicial =  SysmanConstantes.DEFECTOINICIAL_STRING;

		tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cuentaFinal= SysmanConstantes.DEFECTOFINAL_STRING;
	}
	//<METODOS_CARGAR_LISTA>

	public void cargarListaTipoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						frmmovbancoxcntsControladorUrlEnum.URL15005
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoConst);
	}

	public void cargarListaTipoFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						frmmovbancoxcntsControladorUrlEnum.URL15003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(frmmovbancoxcntsControladorEnum.TIPOINICIAL.getValue(),
				String.valueOf(tipoInicial));

		listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoConst);
	}

	public void cargarListaCuentaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						frmmovbancoxcntsControladorUrlEnum.URL4888
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(frmmovbancoxcntsControladorEnum.CLASECUENTA.getValue(), "B");
		param.put(GeneralParameterEnum.ANO.name(),
				SysmanFunciones.ano(fechaInicial));

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoConst);

	}

	public void cargarListaCuentaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						frmmovbancoxcntsControladorUrlEnum.URL6232
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(frmmovbancoxcntsControladorEnum.CLASECUENTA.getValue(), "B");

		param.put(GeneralParameterEnum.ANO.name(),
				SysmanFunciones.ano(fechaInicial));

		param.put(frmmovbancoxcntsControladorEnum.CUENTAINICIAL.getValue(),
				cuentaInicial);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoConst);
	}

	public void oprimirPresentar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.PDF);

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;

		generarInforme(FORMATOS.EXCEL97);

		// </CODIGO_DESARROLLADO>
	}

	public void generarInforme(ReportesBean.FORMATOS formato) 
	{
		
		try
		{
			String reporte = "002852MovimientoDeBancosPorCuenta";

			if (SysmanFunciones.ano(fechaInicial) != SysmanFunciones
					.ano(fechaFinal))
			{
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4488"));
				return;
			}

			/* Anio respecto a la fecha incial */
			int anio = SysmanFunciones.ano(fechaInicial);

			/*- Mes respecto a la fecha inicial*/
			int mesIni = SysmanFunciones.mes(fechaInicial);

			/* Mes anterior al inicial */
			int mesAnt = mesIni - 1;


			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("anio", anio);
			reemplazar.put("codIni", "'" + cuentaInicial + "'");
			reemplazar.put("codFin", "'" + cuentaFinal + "'");
			reemplazar.put("tipoIni", "'" + tipoInicial + "'");
			reemplazar.put("tipoFin", "'" + tipoFinal + "'");

			reemplazar.put("fechaIni",
					SysmanFunciones.formatearFecha(fechaInicial));

			reemplazar.put("fechaFin",
					SysmanFunciones.formatearFecha(fechaFinal));
			//parametro para la Firma del Libro de Boncos
			String nombretesorero = ejbSysmanUtil.consultarParametro(compania,
	                "NOMBRE TESORERO", modulo, new Date(),
	                true);
			String cargotesorero = ejbSysmanUtil.consultarParametro(compania,
			           "CARGO TESORERO", modulo,
			           new Date(), true);
			
				
			Map<String, Object> parametros = new HashMap<>();
			        parametros.put("PR_NOMBRETESORERO", nombretesorero);
			        parametros.put("PR_CARGOTESORERO", cargotesorero);

			parametros.put("PR_NOMBRECOMPANIA",
					SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_FECHAINICIAL",
					SysmanFunciones.convertirAFechaCadena(fechaInicial));

			parametros.put("PR_FECHAFINAL",
					SysmanFunciones.convertirAFechaCadena(fechaFinal));



			Reporteador.resuelveConsulta(reporte,
					Integer.valueOf(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(
					reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN,
					formato);

		}
		catch (JRException | IOException | SystemException
	            | ParseException | SysmanException    e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	 
	public void cambiarFechaInicial() {
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;;
		
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
	}

	public void seleccionarFilaTipoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(codigoConst), "")
				.toString();
		cargarListaTipoFinal();
	}

	public void seleccionarFilaTipoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(codigoConst), "")
				.toString();
	}

	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(codigoConst), "")
				.toString();
		cargarListaCuentaFinal();
	}

	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(codigoConst), "")
				.toString();

	}


	public String getTipoInicial() {
		return tipoInicial;
	}

	public void setTipoInicial(String tipoInicial) {
		this.tipoInicial = tipoInicial;
	}

	public String getTipoFinal() {
		return tipoFinal;
	}

	public void setTipoFinal(String tipoFinal) {
		this.tipoFinal = tipoFinal;
	}

	public String getCuentaInicial() {
		return cuentaInicial;
	}

	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	public String getCuentaFinal() {
		return cuentaFinal;
	}

	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	

	public RegistroDataModelImpl getListaTipoInicial() {
		return listaTipoInicial;
	}


	public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
		this.listaTipoInicial = listaTipoInicial;
	}

	public RegistroDataModelImpl getListaTipoFinal() {
		return listaTipoFinal;
	}

	public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
		this.listaTipoFinal = listaTipoFinal;
	}

	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}

	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}

	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}

	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
