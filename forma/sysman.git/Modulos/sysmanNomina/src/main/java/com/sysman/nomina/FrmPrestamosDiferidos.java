/*-
 * FrmPrestamosDiferidos.java
 *
 * 1.0
 * 
 * 23/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.PeriodoTrabajoControladorUrlEnum;
import com.sysman.nomina.enums.PrestamosDiferidosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 23/04/2021
 * @author gfigueredo
 * 
 * @version 1.1, 14/07/2021
 * @author gfigueredo
 * Se cambia el nombre 002248DiferidosQuincenalesNari�o por 002248DiferidosQuincenalesNarino
 */
@ManagedBean
@ViewScoped
public class FrmPrestamosDiferidos extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * Variable utilizada para almacenar el dato de a�o inicial
	 */
	private String ano1;
	/**
	 * Variable utilizada para almacenar el dato de a�o final
	 */
	private String ano2;
	/**
	 * Variable utilizada para almacenar el dato de mes inicial
	 */
	private String mes1;
	/**
	 * Variable utilizada para almacenar el dato de mes final
	 */
	private String mes2;
	/**
	 * Variable utilizada para almacenar el dato de periodo inicial
	 */
	private String periodo1;
	/**
	 * Variable utilizada para almacenar el dato de periodo final
	 */
	private String periodo2;
	/**
	 * Variable utilizada para almacenar el dato del proceso
	 */
	private String proceso;
	/**
	 * Variable utilizada para almacenar el dato concepto inicial
	 */
	private String conceptoI;
	/**
	 * Variable utilizada para almacenar el dato de concepto final
	 */
	private String conceptoF;
	/**
	 * Variable utilizada para almacenar el dato de empleado inicial
	 */
	private String empleadoI;
	/**
	 * Variable utilizada para almacenar el dato de empleado final
	 */
	private String empleadoF;
	/**
	 * Variable utilizada para almacenar el dato de nombre concepto inicial
	 */
	private String nombreConceptoInicial;
	/**
	 * Variable utilizada para almacenar el dato de nombre concepto final
	 */
	private String nombreConceptoFinal;
	/**
	 * Variable utilizada para almacenar el dato de nombre empleado inicial
	 */
	private String nombreEmpleadoInicial;
	/**
	 * Variable utilizada para almacenar el dato de nombre empleado final
	 */
	private String nombreEmpleadoFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * Lista que almacena los datos de a�o inicial
	 */
	private List<Registro> listaAno1;
	/**
	 * Lista que almacena los datos de a�o final
	 */
	private List<Registro> listaAno2;
	/**
	 * Lista que almacena los datos de mes inicial
	 */
	private List<Registro> listaMes1;
	/**
	 * Lista que almacena los datos de mes final
	 */
	private List<Registro> listaMes2;
	/**
	 * Lista que almacena los datos de periodo inicial
	 */
	private List<Registro> listaPeriodo1;
	/**
	 * Lista que almacena los datos de periodo final
	 */
	private List<Registro> listaPeriodo2;
	/**
	 * Lista que almacena los datos de proceso
	 */
	private List<Registro> listaProceso;
	/**
	 * Lista que almacena los datos de concepto inicial
	 */
	private RegistroDataModelImpl listaConceptoI;
	/**
	 * Lista que almacena los datos de concepto final
	 */
	private RegistroDataModelImpl listaConceptoF;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista que almacena los datos de empelado inicial
	 */
	private RegistroDataModelImpl listaEmpleadoI;
	/**
	 * Lista que almacena los datos de empleado final
	 */
	private RegistroDataModelImpl listaEmpleadoF;
//</DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Variable que almacena el dato de modulo.
	 */
	private String modulo;
	
    private String nombreCompania;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de FrmPrestamosDiferidos
	 */
	public FrmPrestamosDiferidos() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		ano1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		mes1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		ano2 = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		mes2 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo2 = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
        nombreCompania = SessionUtil.getCompaniaIngreso()
                .getNombre();
		try {
			numFormulario = 2262;
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
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
		cargarListaProceso();
		cargarListaAno1();
		cargarListaAno2();
		cargarListaMes1();
		cargarListaMes2();
		cargarListaPeriodo1();
		cargarListaPeriodo2();

		cargarListaConceptoI();
		cargarListaConceptoF();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaEmpleadoI();
		cargarListaEmpleadoF();
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
		 * FR2262-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 1,
		 * Me.Name End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno1
	 *
	 *
	 */
	public void cargarListaAno1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		try {
			listaAno1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													PrestamosDiferidosControladorUrlEnum.URL4735.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	/**
	 * 
	 * Carga la lista listaAno2
	 *
	 *
	 */
	public void cargarListaAno2() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		try {
			listaAno2 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													PrestamosDiferidosControladorUrlEnum.URL4735.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	/**
	 * 
	 * Carga la lista listaMes1
	 *
	 *
	 */
	public void cargarListaMes1(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano1);
		try {
			listaMes1 = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL5723
									.getValue())
							.getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMes2
	 *
	 *
	 */
	public void cargarListaMes2(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano2);
		try {
			listaMes2 = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL5723
									.getValue())
							.getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		}

	/**
	 * 
	 * Carga la lista listaPeriodo1
	 *
	 *
	 */
	public void cargarListaPeriodo1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano1);
		param.put(GeneralParameterEnum.MES.getName(), mes1);
		try {
			listaPeriodo1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													PrestamosDiferidosControladorUrlEnum.URL7274.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	/**
	 * 
	 * Carga la lista listaPeriodo2
	 *
	 *
	 */
	public void cargarListaPeriodo2() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano2);
		param.put(GeneralParameterEnum.MES.getName(), mes2);
		try {
			listaPeriodo2 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													PrestamosDiferidosControladorUrlEnum.URL7274.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 *
	 */
	public void cargarListaProceso() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaProceso = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													PrestamosDiferidosControladorUrlEnum.URL4058.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	/**
	 * 
	 * Carga la lista listaConceptoI
	 *
	 *
	 */

	public void cargarListaConceptoI() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PrestamosDiferidosControladorUrlEnum.URL5724.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		listaConceptoI = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.ID_DE_CONCEPTO.getName());

	}

	public void seleccionarFilaConceptoI(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		conceptoI = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.ID_DE_CONCEPTO.getName()), "")
				.toString();
		nombreConceptoInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE_CONCEPTO.getName()), "").toString();

		cargarListaConceptoF();

	}

	public void cargarListaConceptoF() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PrestamosDiferidosControladorUrlEnum.URL5724.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		listaConceptoF = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.ID_DE_CONCEPTO.getName());

	}

	public void seleccionarFilaConceptoF(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		conceptoF = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.ID_DE_CONCEPTO.getName()), "")
				.toString();
		nombreConceptoFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE_CONCEPTO.getName()), "").toString();
		
		cargarListaConceptoI();

	}

	/**
	 * 
	 * Carga la lista listaEmpleadoI
	 *
	 *
	 */
	public void cargarListaEmpleadoI() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PrestamosDiferidosControladorUrlEnum.URL5725.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		listaEmpleadoI = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.ID_DE_EMPLEADO.getName());
	}

	/**
	 * 
	 * Carga la lista listaEmpleadoF
	 *
	 *
	 */
	public void cargarListaEmpleadoF() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PrestamosDiferidosControladorUrlEnum.URL5725.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		listaEmpleadoF = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.ID_DE_EMPLEADO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF en la vista
	 *
	 *
	 *
	 */
	public void oprimirPDF() {
		generarReporte(FORMATOS.PDF);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EXCEL en la vista
	 *
	 *
	 *
	 */
	public void oprimirEXCEL() {
		generarReporte(FORMATOS.EXCEL);
	}

	public void generarReporte(FORMATOS formatos) {

		try {
			archivoDescarga = null;

			String reporte = "002248DiferidosQuincenalesNarino";
			

			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("anoI", ano1);
			reemplazar.put("mesI", mes1);
			reemplazar.put("periodoI", periodo1);
			reemplazar.put("anoF", ano2);
			reemplazar.put("mesF", mes2);
			reemplazar.put("periodoF", periodo2);
			reemplazar.put("conceptoI", conceptoI);
			reemplazar.put("conceptoF", conceptoF);
			reemplazar.put("empleadoI", empleadoI);
			reemplazar.put("empleadoF", empleadoF);

			Map<String, Object> parametros = new HashMap<>();

			parametros.put("PR_AHORA", new Date());

			String strsql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);

			parametros.put("PR_STRSQL", strsql);
			parametros.put("PR_NOMBRECOMPANIA",
                    SessionUtil.getCompaniaIngreso().getNombre());
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);

		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			Logger.getLogger(ResumPorCentroCostoControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano1
	 * 
	 *
	 * 
	 */
	public void cambiarAno1() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Ano2
	 * 
	 *
	 * 
	 */
	public void cambiarAno2() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Mes1
	 * 
	 *
	 * 
	 */
	public void cambiarMes1() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Mes2
	 * 
	 *
	 * 
	 */
	public void cambiarMes2() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Proceso
	 * 
	 *
	 * 
	 */
	public void cambiarProceso() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control ConceptoI
	 * 
	 *
	 * 
	 */
	public void cambiarConceptoI() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control ConceptoF
	 * 
	 *
	 * 
	 */
	public void cambiarConceptoF() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control EmpleadoI
	 * 
	 *
	 * 
	 */
	public void cambiarEmpleadoI() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control EmpleadoF
	 * 
	 *
	 * 
	 */
	public void cambiarEmpleadoF() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEmpleadoI
	 *
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEmpleadoI(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		empleadoI = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.ID_DE_EMPLEADO.getName()), "")
				.toString();
		nombreEmpleadoInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRECOMPLETO.getName()), "").toString();
		
		
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEmpleadoF
	 *
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEmpleadoF(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		empleadoF = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.ID_DE_EMPLEADO.getName()), "")
				.toString();
		nombreEmpleadoFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRECOMPLETO.getName()), "").toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano1
	 * 
	 * @return ano1
	 */
	public String getAno1() {
		return ano1;
	}

	/**
	 * Asigna la variable ano1
	 * 
	 * @param ano1 Variable a asignar en ano1
	 */
	public void setAno1(String ano1) {
		this.ano1 = ano1;
	}

	/**
	 * Retorna la variable ano2
	 * 
	 * @return ano2
	 */
	public String getAno2() {
		return ano2;
	}

	/**
	 * Asigna la variable ano2
	 * 
	 * @param ano2 Variable a asignar en ano2
	 */
	public void setAno2(String ano2) {
		this.ano2 = ano2;
	}

	/**
	 * Retorna la variable mes1
	 * 
	 * @return mes1
	 */
	public String getMes1() {
		return mes1;
	}

	/**
	 * Asigna la variable mes1
	 * 
	 * @param mes1 Variable a asignar en mes1
	 */
	public void setMes1(String mes1) {
		this.mes1 = mes1;
	}

	/**
	 * Retorna la variable mes2
	 * 
	 * @return mes2
	 */
	public String getMes2() {
		return mes2;
	}

	/**
	 * Asigna la variable mes2
	 * 
	 * @param mes2 Variable a asignar en mes2
	 */
	public void setMes2(String mes2) {
		this.mes2 = mes2;
	}

	/**
	 * Retorna la variable periodo1
	 * 
	 * @return periodo1
	 */
	public String getPeriodo1() {
		return periodo1;
	}

	/**
	 * Asigna la variable periodo1
	 * 
	 * @param periodo1 Variable a asignar en periodo1
	 */
	public void setPeriodo1(String periodo1) {
		this.periodo1 = periodo1;
	}

	/**
	 * Retorna la variable periodo2
	 * 
	 * @return periodo2
	 */
	public String getPeriodo2() {
		return periodo2;
	}

	/**
	 * Asigna la variable periodo2
	 * 
	 * @param periodo2 Variable a asignar en periodo2
	 */
	public void setPeriodo2(String periodo2) {
		this.periodo2 = periodo2;
	}

	/**
	 * Retorna la variable proceso
	 * 
	 * @return proceso
	 */
	public String getProceso() {
		return proceso;
	}

	/**
	 * Asigna la variable proceso
	 * 
	 * @param proceso Variable a asignar en proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	/**
	 * Retorna la variable conceptoI
	 * 
	 * @return conceptoI
	 */
	public String getConceptoI() {
		return conceptoI;
	}

	/**
	 * Asigna la variable conceptoI
	 * 
	 * @param conceptoI Variable a asignar en conceptoI
	 */
	public void setConceptoI(String conceptoI) {
		this.conceptoI = conceptoI;
	}

	/**
	 * Retorna la variable conceptoF
	 * 
	 * @return conceptoF
	 */
	public String getConceptoF() {
		return conceptoF;
	}

	/**
	 * Asigna la variable conceptoF
	 * 
	 * @param conceptoF Variable a asignar en conceptoF
	 */
	public void setConceptoF(String conceptoF) {
		this.conceptoF = conceptoF;
	}

	/**
	 * Retorna la variable empleadoI
	 * 
	 * @return empleadoI
	 */
	public String getEmpleadoI() {
		return empleadoI;
	}

	/**
	 * Asigna la variable empleadoI
	 * 
	 * @param empleadoI Variable a asignar en empleadoI
	 */
	public void setEmpleadoI(String empleadoI) {
		this.empleadoI = empleadoI;
	}

	/**
	 * Retorna la variable empleadoF
	 * 
	 * @return empleadoF
	 */
	public String getEmpleadoF() {
		return empleadoF;
	}

	/**
	 * Asigna la variable empleadoF
	 * 
	 * @param empleadoF Variable a asignar en empleadoF
	 */
	public void setEmpleadoF(String empleadoF) {
		this.empleadoF = empleadoF;
	}

	/**
	 * Retorna la variable nombreConceptoInicial
	 * 
	 * @return nombreConceptoInicial
	 */
	public String getNombreConceptoInicial() {
		return nombreConceptoInicial;
	}

	/**
	 * Asigna la variable nombreConceptoInicial
	 * 
	 * @param nombreConceptoInicial Variable a asignar en nombreConceptoInicial
	 */
	public void setNombreConceptoInicial(String nombreConceptoInicial) {
		this.nombreConceptoInicial = nombreConceptoInicial;
	}

	/**
	 * Retorna la variable nombreConceptoFinal
	 * 
	 * @return nombreConceptoFinal
	 */
	public String getNombreConceptoFinal() {
		return nombreConceptoFinal;
	}

	/**
	 * Asigna la variable nombreConceptoFinal
	 * 
	 * @param nombreConceptoFinal Variable a asignar en nombreConceptoFinal
	 */
	public void setNombreConceptoFinal(String nombreConceptoFinal) {
		this.nombreConceptoFinal = nombreConceptoFinal;
	}

	/**
	 * Retorna la variable nombreEmpleadoInicial
	 * 
	 * @return nombreEmpleadoInicial
	 */
	public String getNombreEmpleadoInicial() {
		return nombreEmpleadoInicial;
	}

	/**
	 * Asigna la variable nombreEmpleadoInicial
	 * 
	 * @param nombreEmpleadoInicial Variable a asignar en nombreEmpleadoInicial
	 */
	public void setNombreEmpleadoInicial(String nombreEmpleadoInicial) {
		this.nombreEmpleadoInicial = nombreEmpleadoInicial;
	}

	/**
	 * Retorna la variable nombreEmpleadoFinal
	 * 
	 * @return nombreEmpleadoFinal
	 */
	public String getNombreEmpleadoFinal() {
		return nombreEmpleadoFinal;
	}

	/**
	 * Asigna la variable nombreEmpleadoFinal
	 * 
	 * @param nombreEmpleadoFinal Variable a asignar en nombreEmpleadoFinal
	 */
	public void setNombreEmpleadoFinal(String nombreEmpleadoFinal) {
		this.nombreEmpleadoFinal = nombreEmpleadoFinal;
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
	 * Retorna la lista listaAno1
	 * 
	 * @return listaAno1
	 */
	public List<Registro> getListaAno1() {
		return listaAno1;
	}

	/**
	 * Asigna la lista listaAno1
	 * 
	 * @param listaAno1 Variable a asignar en listaAno1
	 */
	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}

	/**
	 * Retorna la lista listaAno2
	 * 
	 * @return listaAno2
	 */
	public List<Registro> getListaAno2() {
		return listaAno2;
	}

	/**
	 * Asigna la lista listaAno2
	 * 
	 * @param listaAno2 Variable a asignar en listaAno2
	 */
	public void setListaAno2(List<Registro> listaAno2) {
		this.listaAno2 = listaAno2;
	}

	/**
	 * Retorna la lista listaMes1
	 * 
	 * @return listaMes1
	 */
	public List<Registro> getListaMes1() {
		return listaMes1;
	}

	/**
	 * Asigna la lista listaMes1
	 * 
	 * @param listaMes1 Variable a asignar en listaMes1
	 */
	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
	}

	/**
	 * Retorna la lista listaMes2
	 * 
	 * @return listaMes2
	 */
	public List<Registro> getListaMes2() {
		return listaMes2;
	}

	/**
	 * Asigna la lista listaMes2
	 * 
	 * @param listaMes2 Variable a asignar en listaMes2
	 */
	public void setListaMes2(List<Registro> listaMes2) {
		this.listaMes2 = listaMes2;
	}

	/**
	 * Retorna la lista listaPeriodo1
	 * 
	 * @return listaPeriodo1
	 */
	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}

	/**
	 * Asigna la lista listaPeriodo1
	 * 
	 * @param listaPeriodo1 Variable a asignar en listaPeriodo1
	 */
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}

	/**
	 * Retorna la lista listaPeriodo2
	 * 
	 * @return listaPeriodo2
	 */
	public List<Registro> getListaPeriodo2() {
		return listaPeriodo2;
	}

	/**
	 * Asigna la lista listaPeriodo2
	 * 
	 * @param listaPeriodo2 Variable a asignar en listaPeriodo2
	 */
	public void setListaPeriodo2(List<Registro> listaPeriodo2) {
		this.listaPeriodo2 = listaPeriodo2;
	}

	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}

	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso Variable a asignar en listaProceso
	 */
	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}

	/**
	 * Retorna la lista listaConceptoI
	 * 
	 * @return listaConceptoI
	 */
	public RegistroDataModelImpl getListaConceptoI() {
		return listaConceptoI;
	}

	/**
	 * Asigna la lista listaConceptoI
	 * 
	 * @param listaConceptoI Variable a asignar en listaConceptoI
	 */
	public void setListaConceptoI(RegistroDataModelImpl listaConceptoI) {
		this.listaConceptoI = listaConceptoI;
	}

	/**
	 * Retorna la lista listaConceptoF
	 * 
	 * @return listaConceptoF
	 */
	public RegistroDataModelImpl getListaConceptoF() {
		return listaConceptoF;
	}

	/**
	 * Asigna la lista listaConceptoF
	 * 
	 * @param listaConceptoF Variable a asignar en listaConceptoF
	 */
	public void setListaConceptoF(RegistroDataModelImpl listaConceptoF) {
		this.listaConceptoF = listaConceptoF;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaEmpleadoI
	 * 
	 * @return listaEmpleadoI
	 */
	public RegistroDataModelImpl getListaEmpleadoI() {
		return listaEmpleadoI;
	}

	/**
	 * Asigna la lista listaEmpleadoI
	 * 
	 * @param listaEmpleadoI Variable a asignar en listaEmpleadoI
	 */
	public void setListaEmpleadoI(RegistroDataModelImpl listaEmpleadoI) {
		this.listaEmpleadoI = listaEmpleadoI;
	}

	/**
	 * Retorna la lista listaEmpleadoF
	 * 
	 * @return listaEmpleadoF
	 */
	public RegistroDataModelImpl getListaEmpleadoF() {
		return listaEmpleadoF;
	}

	/**
	 * Asigna la lista listaEmpleadoF
	 * 
	 * @param listaEmpleadoF Variable a asignar en listaEmpleadoF
	 */
	public void setListaEmpleadoF(RegistroDataModelImpl listaEmpleadoF) {
		this.listaEmpleadoF = listaEmpleadoF;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
