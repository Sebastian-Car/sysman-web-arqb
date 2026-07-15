/*-
 * ReportesFinancierosSIIF.java
 *
 * 1.0
 * 
 * 3/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ReportesFinancierosSIIFUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera información SIIF en Excel y archivo plano
 *
 * @version 1.0, 03/04/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ReportesFinancierosSIIF extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// <DECLARAR_ATRIBUTOS>

	/**
	 * Atributo que almacena el anio de trabajo que ha sido seleccionado al
	 * ingresar al modulo de Nomina
	 */
	private String anio;
	/**
	 * Atributo que almacena el mes de trabajo que ha sido seleccionado al
	 * ingresar al modulo de Nomina
	 */
	private String mes;
	/**
	 * Atributo que almacena el periodo de trabajo que ha sido seleccionado al
	 * ingresar al modulo de Nomina
	 */
	private String periodo;
	/**
	 * Atributo que almacena el codigo del proceso que ha sido seleccionado al
	 * ingresar al modulo de Nomina
	 */
	private String idProceso;

	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * ArchivoBase y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivoArchivoBase;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ReportesFinancierosSIIF
	 */
	public ReportesFinancierosSIIF() {
		super();
		compania = SessionUtil.getCompania();
		try {
			contArchivoArchivoBase = new ContenedorArchivo();
			numFormulario = 1752;
			validarPermisos();

			anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();

			mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();

			periodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();

			idProceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();

			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
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
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarExcel en la vista
	 *
	 */
	public void oprimirGenerarExcel() {
		archivoDescarga = null;

		armarExcel();
	}

	private void armarExcel() {

		int fila = 1;

		int totalDeduccion = 0;

		if (contArchivoArchivoBase.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1901"));
			return;
		}

		try (FileInputStream fileIn = new FileInputStream(contArchivoArchivoBase.getArchivo())) {

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put("PROCESO", idProceso);

			param.put(GeneralParameterEnum.ANO.getName(), anio);

			param.put(GeneralParameterEnum.MES.getName(), mes);

			param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

			List<Registro> listaDeducciones = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ReportesFinancierosSIIFUrlEnum.URL8111.getValue())
											.getUrl(),
									param));

			Workbook workbook = new HSSFWorkbook(fileIn);
			Sheet sheet = workbook.getSheet("DEDUCCIONES1");

			StringBuilder builder = new StringBuilder();

			for (Registro valor : listaDeducciones) {

				int deduccion = Integer.parseInt(SysmanFunciones.nvl(valor.getCampos().get("TOTAL"), "0").toString());

				totalDeduccion = totalDeduccion + deduccion;

				String strCuenta = SysmanFunciones.nvl(valor.getCampos().get("RUBROS_SIIF"), "").toString();

				if (!SysmanFunciones.validarVariableVacio(strCuenta)) {
					strCuenta = crearCuenta(strCuenta);
				}
				Row rowDatos = sheet.getRow(fila);

				Cell cellConsecutivo = rowDatos.getCell(0);
				cellConsecutivo.setCellValue(fila);

				Cell cellCuenta = rowDatos.getCell(1);
				cellCuenta.setCellValue(strCuenta);

				Cell cellTipoDocumento = rowDatos.getCell(2);
				cellTipoDocumento.setCellValue(1);

				Cell cellNit = rowDatos.getCell(3);
				cellNit.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("NIT"), "").toString());

				Cell cellTotal = rowDatos.getCell(8);
				cellTotal.setCellValue(deduccion);

				Cell cellConversion = rowDatos.getCell(9);
				cellConversion.setCellValue(fila + "|" + strCuenta + "|1|"
						+ SysmanFunciones.nvl(valor.getCampos().get("NIT"), "").toString() + "|||||" + deduccion);

				builder.append(fila + "|" + strCuenta + "|1|"
						+ SysmanFunciones.nvl(valor.getCampos().get("NIT"), "").toString() + "|||||" + deduccion);
				builder.append("\r\n");
				fila++;

			}

			Row rowTotal = sheet.getRow(fila + 2);
			Cell cellTotal = rowTotal.getCell(8);
			cellTotal.setCellValue(totalDeduccion);

			ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
			workbook.write(fileOut);
			fileOut.close();
			fileIn.close();

			ByteArrayInputStream excelSerializado = new ByteArrayInputStream(fileOut.toByteArray());

			ByteArrayInputStream planoSerializado = JsfUtil.serializarPlano(builder.toString());

			ByteArrayInputStream[] archivoSerial = { excelSerializado, planoSerializado };

			String[] nombres = { "DEDUCCIONES_OBLIG_PPTAL_SIIF_" + anio + "_" + mes + "_" + periodo + ".xls",
					"4_DEDUCCIONES_" + anio + "_" + mes + "_" + periodo + ".txt" };

			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(archivoSerial, nombres);

		} catch (IOException | SystemException | JRException | SQLException | DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>

	private String crearCuenta(String strCuenta) {
		int tamano = strCuenta.length();

		StringBuilder cadenaTerminada = new StringBuilder();

		cadenaTerminada.append(strCuenta.substring(0, 1));

		if (tamano >= 3) {
			cadenaTerminada.append("-" + strCuenta.substring(1, 3));
		}
		if (tamano >= 5) {
			cadenaTerminada.append("-" + strCuenta.substring(3, 5));
		}
		if (tamano >= 7) {
			cadenaTerminada.append("-" + strCuenta.substring(5, 7));
		}
		if (tamano >= 9) {
			cadenaTerminada.append("-" + strCuenta.substring(7, 9));
		}
		if (tamano >= 11) {
			cadenaTerminada.append("-" + strCuenta.substring(9, 11));
		}
		if (tamano >= 13) {
			cadenaTerminada.append("-" + strCuenta.substring(11, 13));
		}

		return cadenaTerminada.toString();
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna el objeto contArchivoArchivoBase
	 * 
	 * @return contArchivoArchivoBase
	 */
	public ContenedorArchivo getContArchivoArchivoBase() {
		return contArchivoArchivoBase;
	}

	/**
	 * Asigna el objeto contArchivoArchivoBase
	 * 
	 * @param contArchivoArchivoBase
	 *            Variable a asignar en contArchivoArchivoBase
	 */
	public void setContArchivoArchivoBase(ContenedorArchivo contArchivoArchivoBase) {
		this.contArchivoArchivoBase = contArchivoArchivoBase;
	}
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
