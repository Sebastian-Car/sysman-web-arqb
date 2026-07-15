/*-
 * TerceroExogenasControlador.java
 *
 * 1.0
 * 
 * 28/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.exogenas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.exogenas.ejb.impl.EjbExogenasCero;
import com.sysman.exogenas.enums.TerceroExogenasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para configurar el indicador de reportar en formato 2276 de los
 * terceros
 *
 * @version 1.0, 28/03/2019
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class TerceroExogenasControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private ContenedorArchivo contArchivocargarPlantillaExcel;
	private StreamedContent archivoDescarga;
	private String cadena;
	private long contador;
	private String salida;
	@EJB
	private EjbExogenasCero EjbExogenaCero;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de TerceroExogenasControlador
	 */
	public TerceroExogenasControlador() {
		super();
		compania = SessionUtil.getCompania();
		contArchivocargarPlantillaExcel = new ContenedorArchivo();
		try {
			// 2051
			numFormulario = GeneralCodigoFormaEnum.TERCERO_EXOGENAS_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
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

		tabla = "TERCERO";
		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(TerceroExogenasControladorUrlEnum.URL131.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(TerceroExogenasControladorUrlEnum.URL137.getValue());
	}

	// <METODOS_CARGAR_LISTA>
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 * @return true
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 * @return true
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove("NIT");
		registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public void oprimircargarPlantilla() throws IOException {
		archivoDescarga = null;
		Workbook workbook = null;
		cadena = "";

		try (FileInputStream file = new FileInputStream(contArchivocargarPlantillaExcel.getArchivo());) {

			if (validarArchivo()) {

				String rutaArchivo = contArchivocargarPlantillaExcel.getArchivo().getPath();

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
						rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					JsfUtil.agregarMensajeInformativo("No es un archivo de .xls");
					return;
				}
				Sheet sheet = workbook.getSheet("Sheet0");
				long i = 1;
				contador = 0;
				for (Row row : sheet) {
					contador++;
				}
				salida = null;
				for (Row row : sheet) {

					if (!validarCelda(row.getCell(0))) {
						break;
					}
					// carga cada 50 registros cuando la cantidad de los mismos son mas autor:cperez
					capturaDatosExcel(row);
					if (50 * (i / 50) == i || (i >= contador && !"".equals(cadena))) {
						if (!cadena.equals("")) {
							cargarDatos();
							cadena = "";
						}
					}

					i = i + 1;
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				if(!salida.equals("null")) {
					salida = salida.substring(5);
				}else {
					salida = idioma.getString("MSM_PROCESO_EJECUTADO");
				}
				out.write(salida.getBytes());
				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
						"LogTercerosExogenas.txt", "txt/html");
				JsfUtil.ejecutarJavaScript("PF('blockNuevo').hide()");
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
				reasignarOrigen();
			}
		} catch (JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			workbook.close();
			reasignarOrigen();
		}
	}
	public boolean validarArchivo() {
		if (contArchivocargarPlantillaExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}
	private boolean validarCelda(Cell celda) {
		if (celda == null) {
			return false;
		}

		return !celda.getStringCellValue().isEmpty();
	}

	private void capturaDatosExcel(Row row) {

		if (row.getRowNum() > 0) {

			for (int i = 0; i < row.getLastCellNum(); i++) {
				String val = "";

				val = row.getCell(i) + "";
				if(val.toUpperCase().equals("SI")) {
					val = "-1";
				}else if(val.toUpperCase().equals("NO")) {
					val= "0";
				}
				cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
			}
			cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());

			cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}

	}
	/*
	 * Se crea el metodo para almacenar las apropiaciones inciales que vienen en el excel
	 */
	private void cargarDatos() {
		String parametro = (SysmanFunciones.esBdSqlServer()) ? cadena.replace("TO_CLOB(", "").replace(")", "")
				: cadena;
		try {
			salida = salida + EjbExogenaCero.actTercero2276(compania,  parametro, SessionUtil.getUser().getCodigo());
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public ContenedorArchivo getContArchivocargarPlantillaExcel() {
		return contArchivocargarPlantillaExcel;
	}

	public void setContArchivocargarPlantillaExcel(ContenedorArchivo contArchivocargarPlantillaExcel) {
		this.contArchivocargarPlantillaExcel = contArchivocargarPlantillaExcel;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
