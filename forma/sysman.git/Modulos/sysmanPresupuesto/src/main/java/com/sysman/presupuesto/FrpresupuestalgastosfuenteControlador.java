/*-
 * FrpresupuestalgastosfuenteControlador.java
 *
 * 1.0
 * 
 * 23/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;

import java.io.IOException;
import java.sql.SQLException;
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
import org.primefaces.model.StreamedContent;

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
import com.sysman.presupuesto.enums.EjecucionGastosCaqControladorEnum;
import com.sysman.presupuesto.enums.EjecucionGastosCaqControladorUrlEnum;
import com.sysman.presupuesto.enums.EjecucionPptalGastosControladorEnum;
import com.sysman.presupuesto.enums.FrpresupuestalgastosfuenteControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Fromulario para la impresi�n de informe presupuestal de gastos por fuentes
 *
 * @version 1.0, 23/12/2020
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class FrpresupuestalgastosfuenteControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Constante a nivel de clase que almacena el codigo del usuario que inicio
	 * sesion
	 */
	private final String cod;
	/**
	 * Constante a nivel de clase que almacena el codigo
	 */
	// <DECLARAR_ATRIBUTOS>
	private String anio;
	/**
	 * variable que almacena el anio
	 */
	private String mesInicial;
	/**
	 * variable que almacena el mesInicial
	 */
	private String mesFinal;
	/**
	 * variable que almacena el mesFinal
	 */
	private String cuentaInicial;
	/**
	 * variable que almacena el cuentaInicial
	 */
	private String cuentaFinal;
	/**
	 * variable que almacena el cuentaFinal
	 */
	private String fuenteInicial;
	/**
	 * variable que almacena la fuenteInicial
	 */
	private String fuenteFinal;
	/**
	 * variable que almacena la fuenteFinal
	 */
	private String nmes1;
	/**
	 * variable que almacena el nmes1
	 */
	private String nmes2;
	/**
	 * variable que almacena el nmes2
	 */
	private String observaciones;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private String referenciaInicial;
	/**
	 * variable que almacena la referencia inicial
	 */
	private String referenciaFinal;
	/**
	 * variable que almacena la referencia final
	 */
	private String auxiliarInicial;
	/**
	 * variable que almacena el auxiliar inicial
	 */
	private String auxiliarFinal;
	/**
	 * variable que almacena el auxiliar final
	 */
	private String cCostoInicial;
	/**
	 * variable que almacena el centro de costo inicial
	 */
	private String cCostoFinal;
	/**
	 * variable que almacena erl centro de costo final
	 */
	private boolean indReferencia;
	/**
	 * variable que activa el check de las referencias inicial y final
	 */
	private boolean indFuenteRecursos;
	/**
	 * variable que activa el check de la fuentes de recursos inicial y final
	 */
	private boolean indAuxiliar;
	/**
	 * variable que activa el check de los auxiliares inicial y final
	 */
	private boolean indCCosto;
	/**
	 * variable que activa el check de los centros de costo inicial y final
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * variable de descarga
	 */
	private List<Registro> listaAno;
	/**
	 * variable que almacena la lista de anios
	 */
	private List<Registro> listaMesInicial;
	/**
	 * variable que almacena la lista de MesInicial
	 */
	private List<Registro> listaMesFinal;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * variable que almacena la lista de MesFinal
	 */
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 * variable que almacena la lista de CuentaInicial
	 */
	private RegistroDataModelImpl listaCuentaFinal;
	/**
	 * variable que almacena la lista de CuentaFinal
	 */
	private RegistroDataModelImpl listaFuenteInicial;
	/**
	 * variable que almacena la lista de FuenteInicial
	 */
	private RegistroDataModelImpl listafuenteFinal;
	/**
	 * variable que almacena la lista de FuenteFinal
	 */
	private RegistroDataModelImpl listaReferenciaInicial;
	/**
	 * variable que almacena la lista de referencia inicial
	 */
	private RegistroDataModelImpl listaReferenciaFinal;
	/**
	 * variable que almacena la lista de referencia final
	 */
	private RegistroDataModelImpl listaAuxiliarInicial;
	/**
	 * variable que almacena la lista de auxiliar inicial
	 */
	private RegistroDataModelImpl listaAuxiliarFinal;
	/**
	 * variable que almacena la lista de auxiliar final
	 */
	private RegistroDataModelImpl listaCCostoInicial;
	/**
	 * variable que almacenal la lista de centro de costo inicial
	 */
	private RegistroDataModelImpl listaCCostoFinal;

	/**
	 * variable que almacena la lista de centro de costo final
	 */
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrpresupuestalgastosfuenteControlador
	 */
	public FrpresupuestalgastosfuenteControlador() {
		super();
		compania = SessionUtil.getCompania();
		cod = GeneralParameterEnum.CODIGO.getName();
		try {
			// 2228
			numFormulario = GeneralCodigoFormaEnum.FR_PRESUPUESTAL_GASTOS_FUENTE_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(EjecucionPptalGastosControlador.class.getName()).log(Level.SEVERE, null, ex);
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
		cargarListaAno();
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		cargarListaMesInicial();
		mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
		cargarListaMesFinal();
		mesFinal = String.valueOf(SysmanFunciones.mes(new Date()) + 1);
		nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)];
		nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial) + 1];
		InicializarCombos();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
		cargarListaFuenteInicial();
		cargarListafuenteFinal();
		cargarListaAuxiliarInicial();
		cargarListaAuxiliarFinal();
		cargarListaReferenciaInicial();
		cargarListaReferenciaFinal();
		cargarListaCCostoInicial();
		cargarListaCCostoFinal();

		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	public void InicializarCombos() {
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		referenciaInicial = SysmanConstantes.DEFECTOFINAL_STRING;
		referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		auxiliarInicial = SysmanConstantes.DEFECTOFINAL_STRING;
		auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cCostoInicial = SysmanConstantes.DEFECTOFINAL_STRING;
		cCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	}

	@Override

	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrpresupuestalgastosfuenteControladorUrlEnum.URL5600.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			Logger.getLogger(EjecucionPptalGastosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMesInicial
	 *
	 */
	public void cargarListaMesInicial() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try {
			listaMesInicial = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrpresupuestalgastosfuenteControladorUrlEnum.URL4686.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			Logger.getLogger(EjecucionPptalGastosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMesFinal
	 * 
	 */
	public void cargarListaMesFinal() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

		try {
			listaMesFinal = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrpresupuestalgastosfuenteControladorUrlEnum.URL5111.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			Logger.getLogger(EjecucionPptalGastosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 */
	public void cargarListaCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL5941.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID");
	}

	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 */
	public void cargarListaCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL6882.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(EjecucionPptalGastosControladorEnum.PARAM0.getValue(), cuentaInicial);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID");
	}

	/**
	 * 
	 * Carga la lista listaFuenteInicial
	 *
	 */
	public void cargarListaFuenteInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL9413.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);
	}

	/**
	 * 
	 * Carga la lista listafuenteFinal
	 *
	 */
	public void cargarListafuenteFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL10036.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(EjecucionPptalGastosControladorEnum.PARAM1.getValue(), String.valueOf(fuenteInicial));
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listafuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);
	}

	/**
	 * carga la lista de listaReferenciaInicial
	 */

	public void cargarListaReferenciaInicial() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL6886.getValue());
		listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, cod);

	}

	/**
	 * 
	 * Carga la lista listaReferenciaFinal
	 *
	 */
	public void cargarListaReferenciaFinal() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(EjecucionPptalGastosControladorEnum.REFERENCIAINICIAL.getValue(), referenciaInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL6885.getValue());
		listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);

	}

	/**
	 * carga la lista de listaAuxiliarInicial
	 */

	public void cargarListaAuxiliarInicial() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL6883.getValue());
		listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);
	}

	/**
	 * 
	 * Carga la lista listaAuxiliarFinal
	 *
	 */
	public void cargarListaAuxiliarFinal() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(EjecucionPptalGastosControladorEnum.AUXILIARINICIAL.getValue(), auxiliarInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL6884.getValue());
		listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);

	}

	/**
	 * carga la lista de listaCCostoInicial
	 */

	public void cargarListaCCostoInicial() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL6887.getValue());
		listaCCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);
	}

	/**
	 * 
	 * Carga la lista listaCCostoFinal
	 *
	 */
	public void cargarListaCCostoFinal() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(EjecucionPptalGastosControladorEnum.CENTRO_COSTO.getValue(), cCostoInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrpresupuestalgastosfuenteControladorUrlEnum.URL6888.getValue());
		listaCCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaReporte(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaReporte(FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo invocado al ejecutar el comando remoto LimpiarObservaciones en la
	 * vista
	 *
	 *
	 */
	public void ejecutarLimpiarObservaciones() {
		// <CODIGO_DESARROLLADO>
		observaciones = "";
		// </CODIGO_DESARROLLADO>
	}

	private void generaReporte(FORMATOS formato) {
		try {
			String strSql = null;
			String reporte = null;
			
			Map<String, Object> reemplazar = new HashMap<>();

			archivoDescarga = null;
			reemplazar.put("cuentaInicial", cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("mesInicial", mesInicial);
			reemplazar.put("mesInicial-1", Integer.parseInt(mesInicial) - 1);
			reemplazar.put("mesFinal", mesFinal);
			reemplazar.put("anio", anio);


			reemplazar.put("cCostoInicial", cCostoInicial);
			reemplazar.put("cCostoFinal", cCostoFinal);

			reemplazar.put("auxiliarInicial", auxiliarInicial);
			reemplazar.put("auxiliarFinal", auxiliarFinal);

			reemplazar.put("referenciaInicial", referenciaInicial);
			reemplazar.put("referenciaFinal", referenciaFinal);

			reemplazar.put("fuenteInicial", fuenteInicial);
			reemplazar.put("fuenteFinal", fuenteFinal);


			if (indCCosto && indAuxiliar && indReferencia && indFuenteRecursos) {

				 reporte = "800503PresupuestalGastosFuenteConTodos";
			}

			else if (indCCosto) {
				
				reporte ="800499PresupuestalGastosFuenteConCCosto";
				
			}

			else if (indAuxiliar) {

				reporte ="800500PresupuestalGastosFuenteConAuxiliar";
						
			}

			else if (indReferencia) {

				 reporte ="800501PresupuestalGastosFuenteConReferencia";
						
			}

			else if (indFuenteRecursos) {	

				 reporte ="800502PresupuestalGastosFuenteConFuenteRecursos";			
			}

			else {
				
				reporte = "800415PresupuestalGastosFuente";
				 
			}
			
			strSql = Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()), reemplazar);

			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
					reporte);
			
		} catch (JRException | IOException | SQLException | DRException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/*
	 * public void validarAuxiliares(Map<String, Object> reemplazar) {
	 * reemplazar.put("centroCosto", ""); reemplazar.put("auxiliar", "");
	 * reemplazar.put("referencia", ""); reemplazar.put("fuenteRecurso", "");
	 * 
	 * if(indCCosto) { reemplazar.put("centroCosto", "AND CENTRO_COSTO BETWEEN '" +
	 * cCostoInicial + "' AND '" + cCostoFinal + "' "); } if(indAuxiliar) {
	 * reemplazar.put("auxiliar", "AND AUXILIAR BETWEEN '" + auxiliarInicial +
	 * "' AND '" + auxiliarFinal + "' "); } if(indReferencia) {
	 * reemplazar.put("referencia", "AND REFERENCIA BETWEEN '" + referenciaInicial +
	 * "' AND '" + referenciaFinal + "' "); } if(indFuenteRecursos) {
	 * reemplazar.put("fuenteRecurso", "AND FUENTE_RECURSO BETWEEN '" +
	 * fuenteInicial + "' AND '" + fuenteFinal + "' "); } }
	 */

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		cargarListaMesInicial();
		cargarListaCuentaInicial();
		cargarListaFuenteInicial();
		cargarListaAuxiliarInicial();
		cargarListaReferenciaInicial();
		InicializarCombos();
	}

	/**
	 * Metodo ejecutado al cambiar el control MesInicial
	 * 
	 */
	public void cambiarMesInicial() {
		// <CODIGO_DESARROLLADO>
		nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)];
		cargarListaMesFinal();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control MesFinal
	 * 
	 * 
	 */
	public void cambiarMesFinal() {
		// <CODIGO_DESARROLLADO>
		nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesFinal)];
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control IndReferencia
	 * 
	 * 
	 */
	public void cambiarIndReferencia() {
		// <CODIGO_DESARROLLADO>
		referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control IndFuenteRecursos
	 * 
	 * 
	 */
	public void cambiarIndFuenteRecursos() {
		// <CODIGO_DESARROLLADO>
		fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control IndAuxiliar
	 * 
	 * 
	 */
	public void cambiarIndAuxiliar() {
		// <CODIGO_DESARROLLADO>
		auxiliarInicial = SysmanConstantes.DEFECTOFINAL_STRING;
		auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control IndCCosto
	 * 
	 * 
	 */
	public void cambiarIndCCosto() {
		// <CODIGO_DESARROLLADO>
		cCostoInicial = SysmanConstantes.DEFECTOFINAL_STRING;
		cCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaInicial
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get("ID").toString();
		cargarListaCuentaFinal();
		cuentaFinal = null;
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get("ID").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial = registroAux.getCampos().get(cod).toString();
		cargarListafuenteFinal();
		fuenteFinal = null;
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listafuenteFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilafuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal = registroAux.getCampos().get(cod).toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciaInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referenciaInicial = registroAux.getCampos().get(cod).toString();
		cargarListaReferenciaFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciaFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referenciaFinal = registroAux.getCampos().get(cod).toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliarInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarInicial = registroAux.getCampos().get(cod).toString();
		cargarListaAuxiliarFinal();

	}

	public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarFinal = registroAux.getCampos().get(cod).toString();

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCCostoInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCCostoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cCostoInicial = registroAux.getCampos().get(cod).toString();
		cargarListaCCostoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCCostoFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCCostoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cCostoFinal = registroAux.getCampos().get(cod).toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
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
	 * Retorna la variable fuenteInicial
	 * 
	 * @return fuenteInicial
	 */
	public String getFuenteInicial() {
		return fuenteInicial;
	}

	/**
	 * Asigna la variable fuenteInicial
	 * 
	 * @param fuenteInicial Variable a asignar en fuenteInicial
	 */
	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}

	/**
	 * Retorna la variable fuenteFinal
	 * 
	 * @return fuenteFinal
	 */
	public String getFuenteFinal() {
		return fuenteFinal;
	}

	/**
	 * Asigna la variable fuenteFinal
	 * 
	 * @param fuenteFinal Variable a asignar en fuenteFinal
	 */
	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
	}

	/**
	 * Retorna la variable nmes1
	 * 
	 * @return nmes1
	 */
	public String getNmes1() {
		return nmes1;
	}

	/**
	 * Asigna la variable nmes1
	 * 
	 * @param nmes1 Variable a asignar en nmes1
	 */
	public void setNmes1(String nmes1) {
		this.nmes1 = nmes1;
	}

	/**
	 * Retorna la variable nmes2
	 * 
	 * @return nmes2
	 */
	public String getNmes2() {
		return nmes2;
	}

	/**
	 * Asigna la variable nmes2
	 * 
	 * @param nmes2 Variable a asignar en nmes2
	 */
	public void setNmes2(String nmes2) {
		this.nmes2 = nmes2;
	}

	/**
	 * Retorna la variable observaciones
	 * 
	 * @return observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * Asigna la variable observaciones
	 * 
	 * @param observaciones Variable a asignar en observaciones
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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
	 * @param listaMesInicial Variable a asignar en listaMesInicial
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
	 * @param listaMesFinal Variable a asignar en listaMesFinal
	 */
	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaCuentaInicial
	 * 
	 * @return listaCuentaInicial
	 */
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}

	/**
	 * Asigna la lista listaCuentaInicial
	 * 
	 * @param listaCuentaInicial Variable a asignar en listaCuentaInicial
	 */
	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}

	/**
	 * Retorna la lista listaCuentaFinal
	 * 
	 * @return listaCuentaFinal
	 */
	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}

	/**
	 * Asigna la lista listaCuentaFinal
	 * 
	 * @param listaCuentaFinal Variable a asignar en listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}

	/**
	 * Retorna la lista listaFuenteInicial
	 * 
	 * @return listaFuenteInicial
	 */
	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}

	/**
	 * Asigna la lista listaFuenteInicial
	 * 
	 * @param listaFuenteInicial Variable a asignar en listaFuenteInicial
	 */
	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}

	/**
	 * Retorna la lista listafuenteFinal
	 * 
	 * @return listafuenteFinal
	 */
	public RegistroDataModelImpl getListafuenteFinal() {
		return listafuenteFinal;
	}

	/**
	 * Asigna la lista listafuenteFinal
	 * 
	 * @param listafuenteFinal Variable a asignar en listafuenteFinal
	 */
	public void setListafuenteFinal(RegistroDataModelImpl listafuenteFinal) {
		this.listafuenteFinal = listafuenteFinal;
	}

	/**
	 * Retorna la variable indReferencia
	 * 
	 * @return indReferencia
	 */
	public boolean getIndReferencia() {
		return indReferencia;
	}

	/**
	 * Asigna la variable indReferencia
	 * 
	 * @param indReferencia Variable a asignar en indReferencia
	 */
	public void setIndReferencia(boolean indReferencia) {
		this.indReferencia = indReferencia;
	}

	/**
	 * Retorna la variable indFuenteRecursos
	 * 
	 * @return indReferencia
	 */
	public boolean getIndFuenteRecursos() {
		return indFuenteRecursos;
	}

	/**
	 * Asigna la variable indFuenteRecursos
	 * 
	 * @param indFuenteRecursos Variable a asignar en indFuenteRecursos
	 */
	public void setIndFuenteRecursos(boolean indFuenteRecursos) {
		this.indFuenteRecursos = indFuenteRecursos;
	}

	/**
	 * Retorna la variable indAuxiliar
	 * 
	 * @return indAuxiliar
	 */
	public boolean getIndAuxiliar() {
		return indAuxiliar;
	}

	/**
	 * Asigna la variable indAuxiliar
	 * 
	 * @param indAuxiliar Variable a asignar en indAuxiliar
	 */
	public void setIndAuxiliar(boolean indAuxiliar) {
		this.indAuxiliar = indAuxiliar;
	}

	/**
	 * Retorna la variable indCCosto
	 * 
	 * @return indCCosto
	 */
	public boolean getIndCCosto() {
		return indCCosto;
	}

	/**
	 * Asigna la variable indCCosto
	 * 
	 * @param indCCosto Variable a asignar en indCCosto
	 */
	public void setIndCCosto(boolean indCCosto) {
		this.indCCosto = indCCosto;
	}

	/**
	 * Retorna la variable referenciaInicial
	 * 
	 * @return referenciaInicial
	 */
	public String getReferenciaInicial() {
		return referenciaInicial;
	}

	/**
	 * Asigna la variable referenciaInicial
	 * 
	 * @param referenciaInicial Variable a asignar en referenciaInicial
	 */
	public void setReferenciaInicial(String referenciaInicial) {
		this.referenciaInicial = referenciaInicial;
	}

	/**
	 * Retorna la variable referenciaFinal
	 * 
	 * @return referenciaFinal
	 */
	public String getReferenciaFinal() {
		return referenciaFinal;
	}

	/**
	 * Asigna la variable referenciaFinal
	 * 
	 * @param referenciaFinal Variable a asignar en referenciaFinal
	 */
	public void setReferenciaFinal(String referenciaFinal) {
		this.referenciaFinal = referenciaFinal;
	}

	/**
	 * Retorna la lista listaReferenciaInicial
	 * 
	 * @return listaReferenciaInicial
	 */
	public RegistroDataModelImpl getListaReferenciaInicial() {
		return listaReferenciaInicial;
	}

	/**
	 * Asigna la lista listaReferenciaInicial
	 * 
	 * @param listaReferenciaInicial Variable a asignar en listaReferenciaInicial
	 */
	public void setListaReferenciaInicial(RegistroDataModelImpl listaReferenciaInicial) {
		this.listaReferenciaInicial = listaReferenciaInicial;
	}

	/**
	 * Retorna la lista listaReferenciaFinal
	 * 
	 * @return listaReferenciaFinal
	 */
	public RegistroDataModelImpl getListaReferenciaFinal() {
		return listaReferenciaFinal;
	}

	/**
	 * Asigna la lista listaReferenciaFinal
	 * 
	 * @param listaReferenciaFinal Variable a asignar en listaReferenciaFinal
	 */
	public void setListaReferenciaFinal(RegistroDataModelImpl listaReferenciaFinal) {
		this.listaReferenciaFinal = listaReferenciaFinal;
	}

	/**
	 * Getter y setter para centro de costo final
	 */
	/**
	 * @return the cCostoFinal
	 */
	public String getcCostoFinal() {
		return cCostoFinal;
	}

	/**
	 * @param cCostoFinal the cCostoFinal to set
	 */
	public void setcCostoFinal(String cCostoFinal) {
		this.cCostoFinal = cCostoFinal;
	}

	/**
	 * @return the listaCCostoFinal
	 */
	public RegistroDataModelImpl getListaCCostoFinal() {
		return listaCCostoFinal;
	}

	/**
	 * @param listaCCostoFinal the listaCCostoFinal to set
	 */
	public void setListaCCostoFinal(RegistroDataModelImpl listaCCostoFinal) {
		this.listaCCostoFinal = listaCCostoFinal;
	}

	/**
	 * @return the cCostoInicial
	 */
	public String getcCostoInicial() {
		return cCostoInicial;
	}

	/**
	 * @param cCostoInicial the cCostoInicial to set
	 */
	public void setcCostoInicial(String cCostoInicial) {
		this.cCostoInicial = cCostoInicial;
	}

	/**
	 * @return the listaCCostoInicial
	 */
	public RegistroDataModelImpl getListaCCostoInicial() {
		return listaCCostoInicial;
	}

	/**
	 * @param listaCCostoInicial the listaCCostoInicial to set
	 */
	public void setListaCCostoInicial(RegistroDataModelImpl listaCCostoInicial) {
		this.listaCCostoInicial = listaCCostoInicial;
	}

	/**
	 * @return the auxiliarInicial
	 */
	public String getAuxiliarInicial() {
		return auxiliarInicial;
	}

	/**
	 * @param auxiliarInicial the auxiliarInicial to set
	 */
	public void setAuxiliarInicial(String auxiliarInicial) {
		this.auxiliarInicial = auxiliarInicial;
	}

	/**
	 * @return the auxiliarFinal
	 */
	public String getAuxiliarFinal() {
		return auxiliarFinal;
	}

	/**
	 * @param auxiliarFinal the auxiliarFinal to set
	 */
	public void setAuxiliarFinal(String auxiliarFinal) {
		this.auxiliarFinal = auxiliarFinal;
	}

	/**
	 * @return the listaAuxiliarInicial
	 */
	public RegistroDataModelImpl getListaAuxiliarInicial() {
		return listaAuxiliarInicial;
	}

	/**
	 * @param listaAuxiliarInicial the listaAuxiliarInicial to set
	 */
	public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) {
		this.listaAuxiliarInicial = listaAuxiliarInicial;
	}

	/**
	 * @return the listaAuxiliarFinal
	 */
	public RegistroDataModelImpl getListaAuxiliarFinal() {
		return listaAuxiliarFinal;
	}

	/**
	 * @param listaAuxiliarFinal the listaAuxiliarFinal to set
	 */
	public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) {
		this.listaAuxiliarFinal = listaAuxiliarFinal;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}
