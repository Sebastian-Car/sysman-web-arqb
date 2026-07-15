/*-
 * FrminfPlanaccionControlador.java
 *
 * 1.0
 * 
 * 07/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroRemote;
import com.sysman.plandesarrollo.enums.FrminfPlanaccionControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Migracion del formulario access FRM_INF_PLANACCION a web controlador
 * FrminfPlanaccionControlador forma frminfplanaccion.xhtml creacion de menu
 * para abrir el formulario modal, creacion de properties para el formulario
 * modal, asi como generacion del archivo descargable en excel con el reporte a
 * partir de un boton.
 * 
 *
 * 
 * 
 * @version 1.0, 07/03/2018
 * @author crodriguez
 */
@ManagedBean
@ViewScoped
public class FrminfPlanaccionControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Variable que obtiene la vigencia seleccionada del combo.
	 */
	private String vigencia;

	/**
	 * Variable que almacena la vigencia minima que existe en la base de datos a
	 * partir de una consulta dss.
	 */
	private String vigenciaMin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * selPlantilla y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivoselPlantilla;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * 
	 */
	private List<Registro> listavigencia;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminfPlanaccionControlador
	 */

	private final String fXlsxCons;

	@EJB
	private EjbPlanDesarrolloCeroRemote ejbPlanDesarrolloCeroRemote;

	public FrminfPlanaccionControlador() {
		super();
		compania = SessionUtil.getCompania();
		fXlsxCons = ".xlsx";
		try {
			// 1736
			numFormulario = GeneralCodigoFormaEnum.FRMINFPLANACCION_CONTROLADOR.getCodigo();
			contArchivoselPlantilla = new ContenedorArchivo();
			traerVigenciaMin();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	/**
	 * Metodo que se encarga de obtener la vigencia minima.
	 */

	private void traerVigenciaMin() {
		try {

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			Registro registrox = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrminfPlanaccionControladorUrlEnum.URL_155.getValue())
											.getUrl(),
									param));

			vigenciaMin = SysmanFunciones.nvl(registrox.getCampos().get("MIN_VIGENCIA"), "").toString();

		} catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualizacion del formulario, como son tablas,
	 * origenes de datos, inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		cargarListavigencia();
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
	 * Carga la lista listavigencia
	 *
	 * 
	 */
	public void cargarListavigencia() {
		try {
			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listavigencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrminfPlanaccionControladorUrlEnum.URL_217.getValue())
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
	 * Metodo ejecutado al oprimir el boton cmdExcel en la vista
	 *
	 * 
	 */
	public void oprimircmdExcel() {
		// <CODIGO_DESARROLLADO>

		archivoDescarga = null;

		if (validarArchivo()) {
			try {
				String datosResultado = ejbPlanDesarrolloCeroRemote.prepararInfPlanAccion(compania,
						Integer.parseInt(vigenciaMin), Integer.parseInt(vigencia));
				if (datosResultado != null) {
					descargarArchivo(datosResultado);
				}

			} catch (NumberFormatException | SystemException | FileNotFoundException e) {

				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo que se encarga de validar las extension del archivo a descargar.
	 * 
	 */

	private void descargarArchivo(String datosExcel) throws FileNotFoundException {

		archivoDescarga = null;

		String rutaArchivo = contArchivoselPlantilla.getArchivo().getPath();

		File fileA = new File(rutaArchivo);
		FileInputStream file = new FileInputStream(fileA);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).toLowerCase();

			if (fXlsxCons.equals(extension)) {
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				if ("INDICADORES".equals(workbook.getSheetAt(0).getSheetName())
						&& "PLAN DE ACCIÓN".equals(workbook.getSheetAt(1).getSheetName()))
					descargarArchivoXlsx(workbook, datosExcel);
				else
					msgAlertaErrorArchivoValido();

				workbook.close();
			} else if (".xls".equals(extension)) {
				HSSFWorkbook workbook = new HSSFWorkbook(file);
				if ("INDICADORES".equals(workbook.getSheetAt(0).getSheetName())
						&& "PLAN DE ACCIÓN".equals(workbook.getSheetAt(1).getSheetName()))
					descargarArchivoXls(workbook, datosExcel);
				else
					msgAlertaErrorArchivoValido();

				workbook.close();
			} else {
				msgAlertaErrorArchivoValido();
			}
			file.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

	}

	/**
	 * 
	 * Metodo que descarga el archivo en formato .xls
	 * 
	 */
	private void descargarArchivoXls(HSSFWorkbook workbook, String datosExcel) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			String[] hojas = datosExcel.split(",.HOJ.,");
			if (hojas.length >= 2) {
				String[] datosHIndicadores = hojas[1].split(SysmanConstantes.SEPARADOR_REG);
				asignarValor(datosHIndicadores, workbook.getSheetAt(0), 0);

				String[] datosHPlanAccion = hojas[0].split(SysmanConstantes.SEPARADOR_REG);

				asignarValor(datosHPlanAccion, workbook.getSheetAt(1), 1);

				workbook.write(out);
				out.close();
				workbook.close();

				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
						SysmanFunciones.concatenar("InformeGeneralPlanDesarrollo", ".xls"));

			} else {
				msgAlertaErrorArchivo();
			}

		} catch (NumberFormatException | IOException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo que descarga el archivo en formato .xlsx
	 * 
	 */
	private void descargarArchivoXlsx(XSSFWorkbook workbook, String datosExcel) {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			String[] hojas = datosExcel.split(",.HOJ.,");
			if (hojas.length >= 2) {
				String[] datosHIndicadores = hojas[1].split(SysmanConstantes.SEPARADOR_REG);
				asignarXValor(datosHIndicadores, workbook.getSheetAt(0), 0);

				String[] datosHPlanAccion = hojas[0].split(SysmanConstantes.SEPARADOR_REG);

				asignarXValor(datosHPlanAccion, workbook.getSheetAt(1), 0);

				workbook.write(out);
				out.close();
				workbook.close();

				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
						SysmanFunciones.concatenar("InformeGeneralPlanDesarrollo", fXlsxCons));

			} else {
				msgAlertaErrorArchivo();
			}
		} catch (NumberFormatException | IOException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo que se encarga de insertar los datos en el archivos excel con
	 * formato .xlsx
	 * 
	 */
	private void asignarXValor(String[] datosGenerales, XSSFSheet sheet, int tipo) {
		String[] colum;
		Row row;
		for (int i = 0; i < datosGenerales.length; i++) {
			colum = datosGenerales[i].split(SysmanConstantes.SEPARADOR_COL);
			row = sheet.createRow(i + 4);
			int k = 0;
			for (int j = 0; j < colum.length; j++) {
				Cell nCell = row.createCell(k);
				nCell.setCellValue(colum[j]);
				if (tipo == 1 && j == 1) {
					k = 3;
				}
				k++;
			}
		}

	}

	/**
	 * 
	 * Metodo que se encarga de insertar los datos en el archivos excel con
	 * formato .xls
	 * 
	 */

	public void asignarValor(String[] datosGenerales, HSSFSheet sheet, int tipo) {
		String[] colum;
		Row row;
		for (int i = 0; i < datosGenerales.length; i++) {
			colum = datosGenerales[i].split(SysmanConstantes.SEPARADOR_COL);
			row = sheet.createRow(i + 4);
			int k = 0;
			for (int j = 0; j < colum.length; j++) {
				Cell nCell = row.createCell(k);
				nCell.setCellValue(colum[j]);
				if (tipo == 1 && j == 1) {
					k = 10;
				}
				k++;
			}
		}
	}

	/**
	 * 
	 * Metodo que valida si el archivo seleccionado de la plantilla es valido.
	 * 
	 */
	public boolean validarArchivo() {

		File fArchivo = contArchivoselPlantilla.getArchivo();

		if (fArchivo != null) {

			String rutaArchivo = String.valueOf(contArchivoselPlantilla.getArchivo());
			String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).toLowerCase();
			if (fXlsxCons.equals(extension) || ".xls".equals(extension)) {
				return true;
			} else {
				msgAlertaErrorArchivoValido();
				return false;
			}
		} else {
			msgAlertaErrorArchivo();
			return false;
		}
	}

	public void msgAlertaErrorArchivo() {
		JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4016"));
	}

	public void msgAlertaErrorArchivoValido() {
		JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4017"));
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
	 * Retorna la variable vigencia
	 * 
	 * @return vigencia
	 */
	public String getVigencia() {
		return vigencia;
	}

	/**
	 * Asigna la variable vigencia
	 * 
	 * @param vigencia
	 *            Variable a asignar en vigencia
	 */
	public void setVigencia(String vigencia) {
		this.vigencia = vigencia;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna el objeto contArchivoselPlantilla
	 * 
	 * @return contArchivoselPlantilla
	 */
	public ContenedorArchivo getContArchivoselPlantilla() {
		return contArchivoselPlantilla;
	}

	/**
	 * Asigna el objeto contArchivoselPlantilla
	 * 
	 * @param contArchivoselPlantilla
	 *            Variable a asignar en contArchivoselPlantilla
	 */
	public void setContArchivoselPlantilla(ContenedorArchivo contArchivoselPlantilla) {
		this.contArchivoselPlantilla = contArchivoselPlantilla;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listavigencia
	 * 
	 * @return listavigencia
	 */
	public List<Registro> getListavigencia() {
		return listavigencia;
	}

	/**
	 * Asigna la lista listavigencia
	 * 
	 * @param listavigencia
	 *            Variable a asignar en listavigencia
	 */
	public void setListavigencia(List<Registro> listavigencia) {
		this.listavigencia = listavigencia;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
