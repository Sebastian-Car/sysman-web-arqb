/*-
 * FrminformesivicofControlador.java
 *
 * 1.0
 * 
 * 10/11/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrminformesivicofControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 10/11/2023
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  FrminformesivicofControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;

	private String informe;

	private String ano;

	private String mesInicial;

	private String mesFinal;

	private StreamedContent archivoDescarga;

	private List<Registro> listaAno;

	private List<Registro> listaMesInicial;

	private List<Registro> listaMesFinal;

	private RegistroDataModelImpl listacbInforme;

	public FrminformesivicofControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2434;
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
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
		//<CARGAR_LISTA>
		cargarListaAno();
		cargarListaMesInicial();
		cargarListaMesFinal();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacbInforme();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrminformesivicofControladorUrlEnum.URL4001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaMesInicial
	 *
	 */
	public void cargarListaMesInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				SysmanFunciones.ano(new Date()));

		try {
			listaMesInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrminformesivicofControladorUrlEnum.URL7001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaMesFinal
	 *
	 */
	public void cargarListaMesFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				SysmanFunciones.ano(new Date()));
		param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

		try {
			listaMesFinal = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrminformesivicofControladorUrlEnum.URL7012
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listacbInforme
	 *
	 */
	public void cargarListacbInforme(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrminformesivicofControladorUrlEnum.URL1750001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), "9");
		param.put(GeneralParameterEnum.SUBTIPO.getName(), "0");
		listacbInforme = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btExcel
	 * en la vista
	 *
	 *
	 */
	public void oprimirbtExcel() {
		archivoDescarga=null; 
		generaReporte(FORMATOS.EXCEL);           

	}

	private void generaReporte(FORMATOS formato) {

		try {
			Registro consulta = null;
			String  nombreConsulta = null;
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.TIPO.getName(), "9");
			param.put("SUBTIPO",0);
			param.put("REPORTE",informe);

			consulta = RegistroConverter.toRegistro(
					requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrminformesivicofControladorUrlEnum.URL1750008
									.getValue())
							.getUrl(),
							param));

			if (consulta != null) {

				if (consulta.getCampos().get("CONSULTA") == null) {

					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4429"));
				}

				else {

					nombreConsulta = consulta.getCampos().get("CONSULTA").toString();

					try {

						HashMap<String, Object> reemplazar = new HashMap<>();
						reemplazar.put("compania", compania);
						reemplazar.put("ano", ano);
						reemplazar.put("mesInicial",mesInicial);
						reemplazar.put("mesFinal",mesFinal);

						String sql= Reporteador.resuelveConsulta(nombreConsulta,Integer.parseInt(SessionUtil.getModulo()),
								reemplazar);
						
						if(nombreConsulta.equals("800604DIS_FONDOS_SIVICOF")) {
							HashMap<String,Object> parametros = new HashMap<>();
							parametros.put("PR_STRSQL", sql);
							
							Reporteador.resuelveConsulta(nombreConsulta, Integer.parseInt(SessionUtil.getModulo()), reemplazar);
							archivoDescarga = JsfUtil.exportarStreamed(nombreConsulta, parametros, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);							
						}else {
							
							
							archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, nombreConsulta);
						
						}
					} catch (IOException | JRException e) {
						e.printStackTrace();
					}

				}
			}

			else {

				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4430"));
			}
		}
		catch ( DRException | SQLException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	public void cambiarMesInicial() {
		cargarListaMesFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacbInforme
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacbInforme(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		informe= registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * Retorna la variable informe
	 * 
	 * @return  informe
	 */
	public String getInforme() {
		return informe;
	}
	/**
	 * Asigna la variable  informe
	 * 
	 * @param  informe
	 * Variable a asignar en  informe
	 */
	public void setInforme(String informe) {
		this.informe = informe;
	}
	/**
	 * Retorna la variable ano
	 * 
	 * @return  ano
	 */
	public String getAno() {
		return ano;
	}
	/**
	 * Asigna la variable  ano
	 * 
	 * @param  ano
	 * Variable a asignar en  ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}
	/**
	 * Retorna la variable mesInicial
	 * 
	 * @return  mesInicial
	 */
	public String getMesInicial() {
		return mesInicial;
	}
	/**
	 * Asigna la variable  mesInicial
	 * 
	 * @param  mesInicial
	 * Variable a asignar en  mesInicial
	 */
	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}
	/**
	 * Retorna la variable mesFinal
	 * 
	 * @return  mesFinal
	 */
	public String getMesFinal() {
		return mesFinal;
	}
	/**
	 * Asigna la variable  mesFinal
	 * 
	 * @param  mesFinal
	 * Variable a asignar en  mesFinal
	 */
	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
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
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	/**
	 * Retorna la lista listaMesInicial
	 * 
	 * @return listaMesInicial
	 */
	public List<Registro> getListaMesInicial() {
		return listaMesInicial;
	}
	/**
	 * Asigna la lista listaMesInicial
	 * 
	 * @param listaMesInicial
	 * Variable a asignar en  listaMesInicial
	 */
	public void setListaMesInicial(List<Registro> listaMesInicial) {
		this.listaMesInicial = listaMesInicial;
	}
	/**
	 * Retorna la lista listaMesFinal
	 * 
	 * @return listaMesFinal
	 */
	public List<Registro> getListaMesFinal() {
		return listaMesFinal;
	}
	/**
	 * Asigna la lista listaMesFinal
	 * 
	 * @param listaMesFinal
	 * Variable a asignar en  listaMesFinal
	 */
	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}
	/**
	 * Retorna la lista listacbInforme
	 * 
	 * @return listacbInforme
	 */
	public RegistroDataModelImpl getListacbInforme() {
		return listacbInforme;
	}
	/**
	 * Asigna la lista listacbInforme
	 * 
	 * @param listacbInforme
	 * Variable a asignar en  listacbInforme
	 */
	public void setListacbInforme(RegistroDataModelImpl listacbInforme) {
		this.listacbInforme = listacbInforme;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
