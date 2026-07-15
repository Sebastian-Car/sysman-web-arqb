/*-
 * Frmdirenciasdescuentoscotizacion.java
 *
 * 1.0
 * 
 * 08/02/2021
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Compania;
import com.sysman.logica.Usuario;
import com.sysman.nomina.enums.FormularioIntegradoControladorUrlEnum;
import com.sysman.nomina.enums.ListadosBancosControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 08/02/2021
 * @author jorduz
 */
@ManagedBean
@ViewScoped
public class  Frmdirenciasdescuentoscotizacion extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private String opcion;
	private String opcion2;
	private final String modulo;
	private String nombreempresa;
	private String anio;
	private String mes;
	private String periodo;
	private String idProceso;
	private String idempleadoini;
	private String idempleadofin;
	private String idfondoini;
	private String idfondofin;
	private String anoini;
	private String mesini;
	private String anofin;
	private String mesfin;
	private String periodo1;
	private String periodo2;
	private String emplinicial;
	private String emplfinal;
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>

	private List<Registro> listacmbFondoPIni;
	private List<Registro> listacmbFondoPFin;
	private List<Registro> listacmbFondoSIni;
	private List<Registro> listacmbFondoSFin;
	private List<Registro> listaCmbAnioIni;
	private List<Registro> listacmbMesesIni;
	private List<Registro> listacmbAnioFin;
	private List<Registro> listacmbMesesFin;
	private List<Registro> listacmbPerIni;
	private List<Registro> listacmbPerFin;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaempleadoInicial;
	private RegistroDataModelImpl listacmbEmpFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de Frmdirenciasdescuentoscotizacion
	 */
	public Frmdirenciasdescuentoscotizacion() {
		super();
		compania 	= SessionUtil.getCompania();
		anoini = anofin = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		mesini = mesfin  =  SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo1 = periodo2 =  SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		idProceso 	= SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		modulo = SessionUtil.getModulo();
		nombreempresa = SessionUtil.getCompaniaIngreso().getNombre();
		opcion2 = "1";
		opcion = "1";


		try {
			numFormulario=2239;

			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
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
		cargarListacmbFondoPIni();
		cargarListacmbFondoPFin();
		cargarListacmbFondoSIni();
		cargarListacmbFondoSFin();
		cargarListaCmbAnioIni();
		cargarListacmbMesesIni();
		cargarListacmbAnioFin();
		cargarListacmbMesesFin();
		cargarListacmbPerIni();
		cargarListacmbPerFin();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarlistaempleadoInicial();
		cargarListacmbEmpFinal();
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
		//<CODIGO_DESARROLLADO>
		/*
FR2239-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
    DoCmd.Restore
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacmbFondoPIni
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbFondoPIni(){
		//stacmbFondoPIni = service.getListado(conectorPool, "SELECT "+

	}
	/**
	 * 
	 * Carga la lista listacmbFondoPFin
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbFondoPFin(){
		//istacmbFondoPFin = service.getListadconectorPool, "SELECT "+
	}
	/**
	 * 
	 * Carga la lista listacmbFondoSIni
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbFondoSIni(){
		//istacmbFondoSIni = service.getListado(conectorPool, "SELECT "+
	}
	/**
	 * 
	 * Carga la lista listacmbFondoSFin
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbFondoSFin(){
		//istacmbFondoSFin = service.getListado(conectorPool, "SELECT "+
	}
	/**
	 * 
	 * Carga la lista listaCmbAnioIni
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCmbAnioIni(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);

		try {
			listaCmbAnioIni = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ListadosBancosControladorUrlEnum.URL5765.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacmbMesesIni
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbMesesIni(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anoini);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);

		try {
			listacmbMesesIni = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ListadosBancosControladorUrlEnum.URL6657.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacmbAnioFin
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbAnioFin(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);

		try {
			listacmbAnioFin = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ListadosBancosControladorUrlEnum.URL5765.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacmbMesesFin
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbMesesFin(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anofin);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);

		try {
			listacmbMesesFin= RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ListadosBancosControladorUrlEnum.URL6657.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacmbPerIni
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbPerIni(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);
		param.put(GeneralParameterEnum.ANO.getName(), anoini);
		param.put(GeneralParameterEnum.MES.getName(), mesini);

		try {
			listacmbPerIni = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ListadosBancosControladorUrlEnum.URL8051.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacmbPerFin
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbPerFin(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);
		param.put(GeneralParameterEnum.ANO.getName(), anofin);
		param.put(GeneralParameterEnum.MES.getName(), mesfin);

		try {
			listacmbPerFin = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ListadosBancosControladorUrlEnum.URL8051.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaempleadoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarlistaempleadoInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FormularioIntegradoControladorUrlEnum.URL9095
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaempleadoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"ID_DE_EMPLEADO");
	}
	/**
	 * 
	 * Carga la lista listacmbEmpFinal
	 *
	 *
	 */
	public void cargarListacmbEmpFinal(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FormularioIntegradoControladorUrlEnum.URL9095
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacmbEmpFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"ID_DE_EMPLEADO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton InfDiferencias
	 * en la vista
	 *
	 * 
	 *
	 */

	public void cambiarOpcion() {
		// <CODIGO_DESARROLLADO>

		// <CODIGO_DESARROLLADO>
	}

	public void oprimirgenPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Comando20
	 * en la vista
	 * 
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirgenExcel() {
		//<CODIGO_DESARROLLADO>

		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL);


		//</CODIGO_DESARROLLADO>
	}

	private boolean validaciones() {
		boolean rta = true;
		if (SysmanFunciones.validarVariableVacio(anoini)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2324"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(mesini)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB703"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(periodo1)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2618"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(anofin)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2326"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(mesfin)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB704"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(periodo2)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2621"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(idempleadoini)) {
			JsfUtil.agregarMensajeAlerta("Debe seleccionar un empleado inicial");
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(idempleadofin)) {
			JsfUtil.agregarMensajeAlerta("Debe seleccionar un empleado final");
			rta = false;

		}
		return rta;
	}
	//</METODOS_BOTONES>
	private void generarInforme(FORMATOS formatos) {
		archivoDescarga = null;
		String reporte = null;
		String nombreInforme = "";
		String rempempleados = "";

		if (idempleadoini.equals("0") && idempleadofin.equals("0")) {
			rempempleados = "AND HISTORICOS.ID_DE_EMPLEADO BETWEEN '0'  AND '999999'";
		}
		else {
			rempempleados = "AND HISTORICOS.ID_DE_EMPLEADO BETWEEN " + idempleadoini + " AND " + idempleadofin;
		}


		try {
			if (!validaciones()) {
				return;
			}

			if (opcion.equals("1")) {
				reporte = "002181ParafiscalesAutoVolanteSalud";
				nombreInforme = reporte;
			} else {

				reporte = "002182ParafiscalesAutoVolantePension";
				nombreInforme =reporte;

			}

			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("proceso", idProceso);
			reemplazar.put("ano1", anoini);
			reemplazar.put("ano2", anofin);
			reemplazar.put("mes1", mesini);
			reemplazar.put("mes2", mesfin);
			reemplazar.put("periodo1", periodo1);
			reemplazar.put("periodo2", periodo2);
			reemplazar.put("empleado1", idempleadoini);
			reemplazar.put("empleado2", idempleadofin);
			reemplazar.put("rempempleados", rempempleados);
			String strsql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_NOMBREEMPRESA", nombreempresa);
			parametros.put("PR_STRSQL", strsql);
			archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, parametros, ConectorPool.ESQUEMA_SYSMAN,
					formatos);

		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException ex) {
			Logger.getLogger(ResumPorCentroCostoControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}	


	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaempleadoInicial
	 *
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaempleadoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		idempleadoini = SysmanFunciones
				.nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
				.toString();
		emplinicial = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
				.toString();
	}
	/**
	 *
	 */
	public void seleccionarFilacmbEmpFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		idempleadofin = SysmanFunciones
				.nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
				.toString();
		emplfinal = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
				.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>

	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable idempleadoini
	 * 
	 * @return  idempleadoini
	 */
	public String getIdempleadoini() {
		return idempleadoini;
	}
	/**
	 * Asigna la variable  idempleadoini
	 * 
	 * @param  idempleadoini
	 * Variable a asignar en  idempleadoini
	 */
	public void setIdempleadoini(String idempleadoini) {
		this.idempleadoini = idempleadoini;
	}
	/**
	 * Retorna la variable idempleadofin
	 * 
	 * @return  idempleadofin
	 */
	public String getIdempleadofin() {
		return idempleadofin;
	}
	/**
	 * Asigna la variable  idempleadofin
	 * 
	 * @param  idempleadofin
	 * Variable a asignar en  idempleadofin
	 */
	public void setIdempleadofin(String idempleadofin) {
		this.idempleadofin = idempleadofin;
	}
	/**
	 * Retorna la variable idfondoini
	 * 
	 * @return  idfondoini
	 */
	public String getIdfondoini() {
		return idfondoini;
	}
	/**
	 * Asigna la variable  idfondoini
	 * 
	 * @param  idfondoini
	 * Variable a asignar en  idfondoini
	 */
	public void setIdfondoini(String idfondoini) {
		this.idfondoini = idfondoini;
	}
	/**
	 * Retorna la variable idfondofin
	 * 
	 * @return  idfondofin
	 */
	public String getIdfondofin() {
		return idfondofin;
	}
	/**
	 * Asigna la variable  idfondofin
	 * 
	 * @param  idfondofin
	 * Variable a asignar en  idfondofin
	 */
	public void setIdfondofin(String idfondofin) {
		this.idfondofin = idfondofin;
	}
	/**
	 * Retorna la variable anoini
	 * 
	 * @return  anoini
	 */
	public String getAnoini() {
		return anoini;
	}
	/**
	 * Asigna la variable  anoini
	 * 
	 * @param  anoini
	 * Variable a asignar en  anoini
	 */
	public void setAnoini(String anoini) {
		this.anoini = anoini;
	}
	/**
	 * Retorna la variable mesini
	 * 
	 * @return  mesini
	 */
	public String getMesini() {
		return mesini;
	}
	/**
	 * Asigna la variable  mesini
	 * 
	 * @param  mesini
	 * Variable a asignar en  mesini
	 */
	public void setMesini(String mesini) {
		this.mesini = mesini;
	}
	/**
	 * Retorna la variable anofin
	 * 
	 * @return  anofin
	 */
	public String getAnofin() {
		return anofin;
	}
	/**
	 * Asigna la variable  anofin
	 * 
	 * @param  anofin
	 * Variable a asignar en  anofin
	 */
	public void setAnofin(String anofin) {
		this.anofin = anofin;
	}
	/**
	 * Retorna la variable mesfin
	 * 
	 * @return  mesfin
	 */
	public String getMesfin() {
		return mesfin;
	}
	/**
	 * Asigna la variable  mesfin
	 * 
	 * @param  mesfin
	 * Variable a asignar en  mesfin
	 */
	public void setMesfin(String mesfin) {
		this.mesfin = mesfin;
	}

	public String getPeriodo1() {
		return periodo1;
	}

	public void setPeriodo1(String periodo1) {
		this.periodo1 = periodo1;
	}

	public String getPeriodo2() {
		return periodo2;
	}

	public void setPeriodo2(String periodo2) {
		this.periodo2 = periodo2;
	}

	public String getEmplinicial() {
		return emplinicial;
	}

	public void setEmplinicial(String emplinicial) {
		this.emplinicial = emplinicial;
	}
	public String getEmplfinal() {
		return emplfinal;
	}
	public void setEmplfinal(String emplfinal) {
		this.emplfinal = emplfinal;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listacmbFondoPIni
	 * 
	 * @return listacmbFondoPIni
	 */
	public List<Registro> getListacmbFondoPIni() {
		return listacmbFondoPIni;
	}
	/**
	 * Asigna la lista listacmbFondoPIni
	 * 
	 * @param listacmbFondoPIni
	 * Variable a asignar en  listacmbFondoPIni
	 */
	public void setListacmbFondoPIni(List<Registro> listacmbFondoPIni) {
		this.listacmbFondoPIni = listacmbFondoPIni;
	}
	/**
	 * Retorna la lista listacmbFondoPFin
	 * 
	 * @return listacmbFondoPFin
	 */
	public List<Registro> getListacmbFondoPFin() {
		return listacmbFondoPFin;
	}
	/**
	 * Asigna la lista listacmbFondoPFin
	 * 
	 * @param listacmbFondoPFin
	 * Variable a asignar en  listacmbFondoPFin
	 */
	public void setListacmbFondoPFin(List<Registro> listacmbFondoPFin) {
		this.listacmbFondoPFin = listacmbFondoPFin;
	}
	/**
	 * Retorna la lista listacmbFondoSIni
	 * 
	 * @return listacmbFondoSIni
	 */
	public List<Registro> getListacmbFondoSIni() {
		return listacmbFondoSIni;
	}
	/**
	 * Asigna la lista listacmbFondoSIni
	 * 
	 * @param listacmbFondoSIni
	 * Variable a asignar en  listacmbFondoSIni
	 */
	public void setListacmbFondoSIni(List<Registro> listacmbFondoSIni) {
		this.listacmbFondoSIni = listacmbFondoSIni;
	}
	/**
	 * Retorna la lista listacmbFondoSFin
	 * 
	 * @return listacmbFondoSFin
	 */
	public List<Registro> getListacmbFondoSFin() {
		return listacmbFondoSFin;
	}
	/**
	 * Asigna la lista listacmbFondoSFin
	 * 
	 * @param listacmbFondoSFin
	 * Variable a asignar en  listacmbFondoSFin
	 */
	public void setListacmbFondoSFin(List<Registro> listacmbFondoSFin) {
		this.listacmbFondoSFin = listacmbFondoSFin;
	}
	/**
	 * Retorna la lista listaCmbAnioIni
	 * 
	 * @return listaCmbAnioIni
	 */
	public List<Registro> getListaCmbAnioIni() {
		return listaCmbAnioIni;
	}
	/**
	 * Asigna la lista listaCmbAnioIni
	 * 
	 * @param listaCmbAnioIni
	 * Variable a asignar en  listaCmbAnioIni
	 */
	public void setListaCmbAnioIni(List<Registro> listaCmbAnioIni) {
		this.listaCmbAnioIni = listaCmbAnioIni;
	}
	/**
	 * Retorna la lista listacmbMesesIni
	 * 
	 * @return listacmbMesesIni
	 */
	public List<Registro> getListacmbMesesIni() {
		return listacmbMesesIni;
	}
	/**
	 * Asigna la lista listacmbMesesIni
	 * 
	 * @param listacmbMesesIni
	 * Variable a asignar en  listacmbMesesIni
	 */
	public void setListacmbMesesIni(List<Registro> listacmbMesesIni) {
		this.listacmbMesesIni = listacmbMesesIni;
	}
	/**
	 * Retorna la lista listacmbAnioFin
	 * 
	 * @return listacmbAnioFin
	 */
	public List<Registro> getListacmbAnioFin() {
		return listacmbAnioFin;
	}
	/**
	 * Asigna la lista listacmbAnioFin
	 * 
	 * @param listacmbAnioFin
	 * Variable a asignar en  listacmbAnioFin
	 */
	public void setListacmbAnioFin(List<Registro> listacmbAnioFin) {
		this.listacmbAnioFin = listacmbAnioFin;
	}
	/**
	 * Retorna la lista listacmbMesesFin
	 * 
	 * @return listacmbMesesFin
	 */
	public List<Registro> getListacmbMesesFin() {
		return listacmbMesesFin;
	}
	/**
	 * Asigna la lista listacmbMesesFin
	 * 
	 * @param listacmbMesesFin
	 * Variable a asignar en  listacmbMesesFin
	 */
	public void setListacmbMesesFin(List<Registro> listacmbMesesFin) {
		this.listacmbMesesFin = listacmbMesesFin;
	}
	/**
	 * Retorna la lista listacmbPerIni
	 * 
	 * @return listacmbPerIni
	 */
	public List<Registro> getListacmbPerIni() {
		return listacmbPerIni;
	}
	/**
	 * Asigna la lista listacmbPerIni
	 * 
	 * @param listacmbPerIni
	 * Variable a asignar en  listacmbPerIni
	 */
	public void setListacmbPerIni(List<Registro> listacmbPerIni) {
		this.listacmbPerIni = listacmbPerIni;
	}
	/**
	 * Retorna la lista listacmbPerFin
	 * 
	 * @return listacmbPerFin
	 */
	public List<Registro> getListacmbPerFin() {
		return listacmbPerFin;
	}
	/**
	 * Asigna la lista listacmbPerFin
	 * 
	 * @param listacmbPerFin
	 * Variable a asignar en  listacmbPerFin
	 */
	public void setListacmbPerFin(List<Registro> listacmbPerFin) {
		this.listacmbPerFin = listacmbPerFin;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	public RegistroDataModelImpl getlistaempleadoInicial() {
		return listaempleadoInicial;
	}
	public void setlistaempleadoInicial(RegistroDataModelImpl listaempleadoInicial) {
		this.listaempleadoInicial = listaempleadoInicial;
	}
	public RegistroDataModelImpl getListacmbEmpFinal() {
		return listacmbEmpFinal;
	}
	public void setListacmbEmpFinal(RegistroDataModelImpl listacmbEmpFinal) {
		this.listacmbEmpFinal = listacmbEmpFinal;
	}

	public String getOpcion() {
		return opcion;
	}
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
	public String getOpcion2() {
		return opcion2;
	}
	public void setOpcion2(String opcion2) {
		this.opcion2 = opcion2;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
