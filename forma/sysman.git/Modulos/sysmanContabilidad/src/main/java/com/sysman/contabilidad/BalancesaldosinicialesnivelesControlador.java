/*-
 * BalancesaldosinicialesnivelesControlador.java
 *
 * 1.0
 * 
 * 12/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalancesaldosinicialesnivelesControladorEnum;
import com.sysman.contabilidad.enums.BalancesaldosinicialesnivelesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario modal que genera dos reportes de balances de saldos iniciales a
 * partir del parámetro Año.
 * 
 * @version 1.0, 12/03/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class BalancesaldosinicialesnivelesControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el código de la compania en la
	 * cual inició sesión el usuario. El valor de esta constante es asignado en
	 * el constructor a la variable de sesión correspondiente.
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Año seleccionado por el usuario.
	 */
	private String anio;

	/**
	 * Indica si el ckeck Nivel Cinco Fue seelccionado o no.
	 */
	private boolean nivelCinco;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista.
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Constante a nivel de clase que aloja el código del módulo desde el cual
	 * el usuario inició sesión.
	 */
	private final String modulo = SessionUtil.getModulo();
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Listado de años tomado de la tabla ANO.
	 */
	private List<Registro> listaAno;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de BalancesaldosinicialesnivelesControlador.
	 */
	public BalancesaldosinicialesnivelesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			/** Formulario número 1741 */
			numFormulario = GeneralCodigoFormaEnum.FRM_BALANCE_SALDOS_INICIALES_NIVELES_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este método se ejecuta justo después de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualización del formulario, como son tablas,
	 * origenes de datos, inicialización de listas y demás necesarios.
	 */
	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		cargarListaAno();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este método es invocado en el método inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del formulario.
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * Carga la lista listaAno.
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(BalancesaldosinicialesnivelesControladorEnum.COMPANIA.getValue(), String.valueOf(compania));

		try {
			listaAno = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									BalancesaldosinicialesnivelesControladorUrlEnum.URL127.getValue())
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
	 * Genera un reporte con un determinado formato.
	 * 
	 * @param formato
	 *            Tipo de documento a generar.
	 */
	private void generarReporte(FORMATOS formato) {
		Map<String, Object> reemplazar = new HashMap<>();
		Map<String, Object> parametros = new HashMap<>();

		String reporte = nivelCinco ? "001727BalanceSaldosInicialesNivel5" : "001724BalanceSaldosInicialesNiveles";

		archivoDescarga = null;

		// <REEMPLAZAR VARIABLES EN CONSULTA>
		reemplazar.put("compania", compania);
		reemplazar.put("anio", anio);
		// </REEMPLAZAR VARIABLES EN CONSULTA>
		try {
			// <ENVIAR PARAMETROS AL REPORTE>
			parametros.put("PR_FORMS_BALANCESALDOSINICIALESNIVELES_ANO", anio);
			// </ENVIAR PARAMETROS AL REPORTE>

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Método ejecutado al oprimir el botón ImprimirExcel en la vista.
	 *
	 */
	public void oprimirImprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Método ejecutado al oprimir el botón ImprimirPdf en la vista.
	 *
	 */
	public void oprimirImprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio.
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio.
	 * 
	 * @param anio
	 *            Variable a asignar en anio.
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable nivelCinco.
	 */
	public boolean isNivelCinco() {
		return nivelCinco;
	}

	/** Asigna la variable nivelCinco. */
	public void setNivelCinco(boolean nivelCinco) {
		this.nivelCinco = nivelCinco;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista.
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno.
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno.
	 * 
	 * @param listaAno
	 *            Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
