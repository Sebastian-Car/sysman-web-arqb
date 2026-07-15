/*-
 * FrmInformedeProyeccionesControlador.java
 *
 * 1.0
 * 
 * 18/08/2018
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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmInformedeProyeccionesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

/**
 *
 * @version 1.0, 18/08/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmInformedeProyeccionesControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;

	// <DECLARAR_ATRIBUTOS>
	private String anio;
	private String mes;
	private String periodo;
	private String proceso;
	private String grupo;
	private String informePersonaliza =  "NO";

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>

	private List<Registro> listaAno1;
	private List<Registro> listaMes1;
	private List<Registro> listaPeriodo1;
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmInformedeProyeccionesControlador
	 */
	public FrmInformedeProyeccionesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		// grupo = "5";

		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_DE_PROYECCIONES_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
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

		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		informePersonaliza =  getParametro(
                "DIAS PS Y PV IGUALES A DIAS PACTADOS EN INFORME BENEFICIOS A EMPLEADOS", "NO");
		// <CARGAR_LISTA>
		cargarListaAno1();
		cargarListaMes1();
		cargarListaPeriodo1();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
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

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno1
	 */
	public void cargarListaAno1() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmInformedeProyeccionesControladorUrlEnum.URL0001.getValue())
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
	 */
	public void cargarListaMes1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try {
			listaMes1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmInformedeProyeccionesControladorUrlEnum.URL0002.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaPeriodo1
	 *
	 */
	public void cargarListaPeriodo1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		try {
			listaPeriodo1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmInformedeProyeccionesControladorUrlEnum.URL0003.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista donde se generan
	 * los informes de beneficios
	 *
	 */
	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		// </CODIGO_DESARROLLADO>
		generarReportes();
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		// </CODIGO_DESARROLLADO>
		generarExcel();
	}

	public void generarExcel() {
		try {
			String nombreReporte = null;
			HashMap<String, Object> reemplaza = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			reemplaza.put("ano", anio);
			reemplaza.put("mes", mes);
			reemplaza.put("periodo", periodo);
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());

			nombreReporte = obtenerNombreReporte(grupo);

			Reporteador.resuelveConsulta(nombreReporte, Integer.parseInt(modulo), reemplaza, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
					ReportesBean.FORMATOS.EXCEL97);

		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	 /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
    private String consultarParametro(String nombre, boolean mayus)
			throws SystemException {
		return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
				new Date(), mayus);
	}
	public void generarReportes() {
		try {
			String nombreReporte = null;
			HashMap<String, Object> reemplaza = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			reemplaza.put("ano", anio);
			reemplaza.put("mes", mes);
			reemplaza.put("periodo", periodo);
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
			
			// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
			nombreReporte = obtenerNombreReporte(grupo);

			Reporteador.resuelveConsulta(nombreReporte, Integer.parseInt(modulo), reemplaza, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
					ReportesBean.FORMATOS.PDF);

		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public String obtenerNombreReporte(String opcionReporte) {
		String nombreReporte = null;
		if (opcionReporte.equals("1")) {

			nombreReporte = "001940BENEFICIOS_TOTAL";

		} else if (opcionReporte.equals("2")) {

			nombreReporte = "001936BENEFICIOS_BASP";

		} else if (opcionReporte.equals("3")) {

			nombreReporte = "001937BENEFICIOS_PS";

		} else if (opcionReporte.equals("4")) {

			nombreReporte = "001938BENEFICIOS_PV";

		} else if (opcionReporte.equals("5")) {

			nombreReporte = "001939BENEFICIOS_PN";

		} else if (opcionReporte.equals("6")) {

			nombreReporte = "001942INFPROVISIONESMENSUALESNIIF5CES";

		} else if (opcionReporte.equals("7")) {

			nombreReporte = "001979BENEFICIOS_BD";

		} else if (opcionReporte.equals("8")) {

			nombreReporte = "001996BENEFICIOS_VAC";

		} else if (opcionReporte.equals("9")) {

			nombreReporte = "001997BENEFICIOS_BER";

		}
		if(informePersonaliza.equals("SI")) {
			if (nombreReporte.equals("001937BENEFICIOS_PS")) {
				nombreReporte = "002509BENEFICIOS_PS";
			} else if (nombreReporte.equals("001938BENEFICIOS_PV")) {
				nombreReporte = "002510BENEFICIOS_PV_DIAS";
			} else if (nombreReporte.equals("001997BENEFICIOS_BER")) {
				nombreReporte = "002511BENEFICIOS_BER_DIAS";
			}					
		}
		return nombreReporte;
	}
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>

	public void cambiarAno1() {
		periodo = null;
		mes = null;
		grupo = null;
		cargarListaMes1();
	}

	public void cambiarMes1() {
		periodo = null;
		grupo = null;
		cargarListaPeriodo1();
	}

	public void cambiargrupo() {

	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
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
	 * @param listaAno1
	 *            Variable a asignar en listaAno1
	 */
	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
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
	 * @param listaMes1
	 *            Variable a asignar en listaMes1
	 */
	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
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
	 * @param listaPeriodo1
	 *            Variable a asignar en listaPeriodo1
	 */
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * @param anio
	 *            the anio to set
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * @return the mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * @param mes
	 *            the mes to set
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * @return the periodo
	 */
	public String getPeriodo() {
		return periodo;
	}

	/**
	 * @param periodo
	 *            the periodo to set
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	/**
	 * @return the modulo
	 */
	public String getModulo() {
		return modulo;
	}

	/**
	 * @param archivoDescarga
	 *            the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * @return the proceso
	 */
	public String getProceso() {
		return proceso;
	}

	/**
	 * @param proceso
	 *            the proceso to set
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	/**
	 * @return the grupo
	 */
	public String getGrupo() {
		return grupo;
	}

	/**
	 * @param grupo
	 *            the grupo to set
	 */
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

}