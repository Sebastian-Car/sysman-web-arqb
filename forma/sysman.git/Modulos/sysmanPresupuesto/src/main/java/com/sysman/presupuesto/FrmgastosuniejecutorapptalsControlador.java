/*-
 * FrmgastosuniejecutorapptalsControlador.java
 *
 * 1.0
 * 
 * 22/01/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmgastosuniejecutorapptalsControladorEnum;
import com.sysman.presupuesto.enums.FrmgastosuniejecutorapptalsControladorUrlEnum;
import com.sysman.presupuesto.enums.PlanpresupuestalptosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;



import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;





import org.primefaces.event.SelectEvent;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.primefaces.model.StreamedContent;

import org.primefaces.event.SelectEvent;



import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 22/01/2026
 * @author NCARDENAS
 */
@ManagedBean
@ViewScoped
public class  FrmgastosuniejecutorapptalsControlador extends BeanBaseModal{
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String anio;
	private String mes;
	private String cuentaInicial;
	private String cuentaFinal;
	private StreamedContent archivoDescarga;
	private String encabezado;
	private String titulo;
	private String unidad;

	private String naturaleza;
	private String nitCompania;
	private String nombreCompania;
	private boolean excel; 
	
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaanio;
	private List<Registro> listames;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listauniEjecutora;
	private RegistroDataModelImpl listarubroIni;
	private RegistroDataModelImpl listarubroFin;
	//</DECLARAR_LISTAS_COMBO_GRANDE>


	public FrmgastosuniejecutorapptalsControlador() {
		super();
		compania = SessionUtil.getCompania();
		 nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		 nombreCompania = SessionUtil.getCompaniaIngreso()
                 .getNombre();
		try {
			numFormulario=GeneralCodigoFormaEnum.FRM_UNIDADEJEPPTAL.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			anio = String.valueOf(SysmanFunciones.ano(new Date()));
			mes = String.valueOf(SysmanFunciones.mes(new Date()));
			cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
			cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
			//cuentaFinal = "9999999999999999";
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(FrmunidadejecutorasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}
	
	  public void cargarParametros() {
		  if("3040532".equals(SessionUtil.getMenuActual())) {
			  titulo = idioma.getString("TB_TB4501");
              encabezado = "Ejecución de ingresos por unidad ejecutora";          
              naturaleza = "C";
		  }else {
			  titulo = idioma.getString("TB_TB4500");
              encabezado = "Ejecución de gastos por unidad ejecutora";         
              naturaleza = "D";
		  }
		  
	  }

	@PostConstruct
	public void inicializar(){
		//<CARGAR_LISTA>
		cargarParametros();
		cargarListaanio();
		cargarListames();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListauniEjecutora(); 
		cargarListarubroIni(); 
		cargarListarubroFin();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}

	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>

	public void cargarListaanio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaanio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmgastosuniejecutorapptalsControladorUrlEnum.URL4001
									.getValue())
							.getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListames(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try {
			listames = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmgastosuniejecutorapptalsControladorUrlEnum.URL7007
									.getValue())
							.getUrl(), param));


		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListauniEjecutora(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanpresupuestalptosControladorUrlEnum.URL1992001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listauniEjecutora = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

		listauniEjecutora.setIncluirTodos(true);
	}

	public void cargarListarubroIni(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmgastosuniejecutorapptalsControladorUrlEnum.URL45117
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);

		listarubroIni = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");

	}

	public void cargarListarubroFin(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmgastosuniejecutorapptalsControladorUrlEnum.URL45119
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
		param.put(FrmgastosuniejecutorapptalsControladorEnum.CUENTAINICIAL
				.getValue(), cuentaInicial);

		listarubroFin = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>

	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		excel = true;
		getInforme(FORMATOS.EXCEL97);          
		//</CODIGO_DESARROLLADO>
	}

	public void oprimirPDF() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		excel = false;
		getInforme(FORMATOS.PDF);            
		//</CODIGO_DESARROLLADO>
	}

	private void getInforme(FORMATOS formato) {
		HashMap<String, Object> reemplazar = new HashMap<>();
		reemplazar.put("compania", compania);
		reemplazar.put("ano", anio);
		reemplazar.put("codigoInicial", cuentaInicial);
		reemplazar.put("codigoFinal", cuentaFinal);
		reemplazar.put("mes", mes);
		if (Integer.parseInt( SysmanFunciones.nvl( unidad,"0").toString()) > 0){
			reemplazar.put("unidadeje", "AND MOV.UNIDAD_EJECUTORA IN('"
		                + unidad + "')");
		}else {
			reemplazar.put("unidadeje","");
		}
		String consulta = "002900EjeGastosPorUnidadEje";
		String reporte = consulta;
		// MANEJO DE PARAMETROS DE REEMPLAZO
		Map<String, Object> parametros = new HashMap<>();
		// MANEJO DE PARAMETROS DEL REPORTE
		 parametros.put("PR_NITCOMPANIA",nitCompania);
		 parametros.put("PR_NOMBRECOMPANIA",nombreCompania);
		parametros.put("PR_ANO", anio);
		parametros.put("PR_NOMBREDEMES",
				SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
				                                           .parseInt(mes)].toUpperCase());

		parametros.put("PR_EXCEL", excel);
		
		if ("3040532".equals(SessionUtil.getMenuActual())) {
			Integer mesEntero;

			if (mes == null || mes.trim().isEmpty()) {
			    mesEntero = 12;
			} else {
			    mesEntero = Integer.valueOf(mes);
			}

			parametros.put("PR_MES_HASTA", mesEntero);
		
			 consulta = "002901EjeIngresosPorUnidadEje";
		 }else {
			
			consulta = "002900EjeGastosPorUnidadEje";
		 }
		
		reporte = consulta;

		Reporteador.resuelveConsulta(consulta,
				Integer.parseInt(SessionUtil.getModulo()),
				reemplazar, parametros);

		try {
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>

	public void cambiaranio() {
		//<CODIGO_DESARROLLADO>
		unidad = null;
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListames();
		cargarListarubroIni(); 
		cargarListarubroFin();
		cargarListauniEjecutora();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>

	public void seleccionarFilauniEjecutora(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		unidad = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();

	}

	public void seleccionarFilarubroIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial= SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
				.toString();
		cuentaFinal = null;
		cargarListarubroFin();
	}

	public void seleccionarFilarubroFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
				.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
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

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>


	public List<Registro> getListaanio() {
		return listaanio;
	}

	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}

	public List<Registro> getListames() {
		return listames;
	}

	public void setListames(List<Registro> listames) {
		this.listames = listames;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	public String getEncabezado() {
		return encabezado;
	}

	public void setEncabezado(String encabezado) {
		this.encabezado = encabezado;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public RegistroDataModelImpl getListauniEjecutora() {
		return listauniEjecutora;
	}

	public void setListauniEjecutora(RegistroDataModelImpl listauniEjecutora) {
		this.listauniEjecutora = listauniEjecutora;
	}

	
	public RegistroDataModelImpl getListarubroIni() {
		return listarubroIni;
	}

	public void setListarubroIni(RegistroDataModelImpl listarubroIni) {
		this.listarubroIni = listarubroIni;
	}

	public RegistroDataModelImpl getListarubroFin() {
		return listarubroFin;
	}

	public void setListarubroFin(RegistroDataModelImpl listarubroFin) {
		this.listarubroFin = listarubroFin;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	} 
	
	public String getNombreCompania() {
		return nombreCompania;
	}

	public void setNombreCompania(String nombreCompania) {
		this.nombreCompania = nombreCompania;
	}
	
	public boolean isExcel() {
		return excel;
	}

	public void setExcel(boolean excel) {
		this.excel = excel;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
